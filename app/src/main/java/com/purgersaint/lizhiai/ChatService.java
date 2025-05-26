package com.purgersaint.lizhiai;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

class Message{
    String role;
    String content;

    public Message(String role,String content){
        this.role=role;
        this.content=content;
    }

    public JsonObject toJsonObject(){
        JsonObject obj = new JsonObject();
        obj.addProperty("role",role);
        obj.addProperty("content",content);
        return obj;
    }
}


interface ApiService{
    @POST
    Call<JsonElement> sendChat(
            //访问地址
            @Url String fullUrl,
            //Api密钥
            @Header("Authorization") String authToken,
            //结构体
            @Body JsonObject requestBody
    );
}

public class ChatService {
    public static final String TAG = "ChatService";
    private ApiService apiService;

    public ChatService(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.example.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public interface ChatCallBack {
        void backRes(String modelMsg);
        void backErr(String errorMsg);
    }

    public void getBackRes(LLM now,String UserText,ChatCallBack callBack){
        if(now == null || now.url == null || now.key == null || now.modelName == null){
            callBack.backErr("模型配置不完整");
            return;
        }
        if(UserText == null || UserText.trim().isEmpty()){
            callBack.backErr("用户消息为空");
            return;
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model",now.modelName);

        JsonArray messagesArray = new JsonArray();

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role","user");
        userMessage.addProperty("content",UserText);
        messagesArray.add(userMessage);

        requestBody.add("messages",messagesArray);
        requestBody.add("temperature",new JsonPrimitive(now.tem));

        String authToken = "Bearer "+now.key;
        String fullApiUrl = now.url;

        Call<JsonElement> call = apiService.sendChat(fullApiUrl,authToken,requestBody);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                new Handler(Looper.getMainLooper()).post(()->{
                    if(response.isSuccessful() && response.body()!=null){
                        JsonElement responseElement = response.body();

                        //从JsonElement解析响应
                        String modelText = parseModelTextFromJsonElement(responseElement);
                        if(modelText != null)
                            callBack.backRes(modelText);
                        else
                            callBack.backErr("解析模型响应失败。响应体: " + responseElement.toString());
                    }else{
                        String errBody = "";
                        if(response.errorBody()!=null){
                            try{
                                errBody = response.errorBody().string();
                            } catch (IOException e){
                                Log.e(TAG,"读取错误结构体出错:",e);
                            }
                        }
                        String errMsg = "API请求失败,代码:"+response.code()+
                                (!errBody.isEmpty() ? " - " + errBody : "");
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "API调用失败:", t);
                new Handler(Looper.getMainLooper()).post(()->{
                    callBack.backErr("网络错误或处理失败:"+t.getMessage());
                });
            }
        });
    }

    private String parseModelTextFromJsonElement(JsonElement responseElement) {
        try {
            if (responseElement.isJsonObject()) {
                JsonObject responseObject = responseElement.getAsJsonObject();
                if (responseObject.has("choices") && responseObject.get("choices").isJsonArray()) {
                    JsonArray choicesArray = responseObject.getAsJsonArray("choices");
                    if (choicesArray.size() > 0 && choicesArray.get(0).isJsonObject()) {
                        JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
                        if (firstChoice.has("message") && firstChoice.get("message").isJsonObject()) {
                            JsonObject messageObject = firstChoice.getAsJsonObject("message");
                            if (messageObject.has("content") && messageObject.get("content").isJsonPrimitive()) {
                                return messageObject.get("content").getAsString();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) { // 包括 ClassCastException, IllegalStateException 等
            Log.e(TAG, "解析 JsonElement 出错", e);
        }
        Log.e(TAG, "无法从给定的 JsonElement 中解析出 'content'");
        return null;
    }
}
