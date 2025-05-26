package com.purgersaint.lizhiai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<ChatDay> All; //聊天内容
    RecyclerView chatDay;           //侧边栏
    private LLMData model;          //模型数据
    private ChatService chatService;    //模型服务

    //初始化侧边栏
    private void setLab(){
        chatDay = findViewById(R.id.chat_each);

        ChatDayAdapter chatDayAdapter = new ChatDayAdapter(MainActivity.this,All);

        chatDay.setAdapter(chatDayAdapter);
        chatDay.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    //打开侧边栏
    public void openLab(View t){
        LinearLayout lab = findViewById(R.id.lab);
        lab.setVisibility(View.VISIBLE);
        setLab();
    }

    //关闭侧边栏
    public void offLab(View t){
        LinearLayout lab = findViewById(R.id.lab);
        lab.setVisibility(View.GONE);
    }

    //打开编辑菜单
    public void openEdit(LLM now) {
        LinearLayout editMenu = findViewById(R.id.edit_menu);
        editMenu.setVisibility(View.VISIBLE);

        EditText llmTitle = findViewById(R.id.llm_title);
        EditText llmApi = findViewById(R.id.llm_api);
        EditText llmKey = findViewById(R.id.llm_key);
        EditText llmModel = findViewById(R.id.llm_model);
        SeekBar llmTemS = findViewById(R.id.llm_tem_s);
        EditText llmTemInput = findViewById(R.id.llm_tem_input);

        llmTitle.setText(now.name);
        llmApi.setText(now.url);
        llmKey.setText(now.key);
        llmModel.setText(now.modelName);
        llmTemInput.setText(Double.toString(now.tem));
        llmTemS.setProgress((int)(now.tem * 10));
        llmTemS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    float actualValue = i / 10.0f;
                    llmTemInput.setText(String.format("%.1f", actualValue));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //关闭编辑菜单
    public void offEdit(View t){
        LinearLayout editMenu = findViewById(R.id.edit_menu);
        editMenu.setVisibility(View.GONE);
    }
    public void saveEdit(View t,LLM use,boolean flag){
        offEdit(t);

        EditText llmTitle = findViewById(R.id.llm_title);
        EditText llmApi = findViewById(R.id.llm_api);
        EditText llmKey = findViewById(R.id.llm_key);
        EditText llmModel = findViewById(R.id.llm_model);
        EditText llmTemInput = findViewById(R.id.llm_tem_input);

        String title = llmTitle.getText().toString();
        String api = llmApi.getText().toString();
        String key = llmKey.getText().toString();
        String modelName = llmModel.getText().toString();
        double tem;
        try {
            tem = Double.parseDouble(llmTemInput.getText().toString());
            // 使用 tem
        } catch (NumberFormatException e) {
            // 用户输入的不是有效数字，设置默认值或提示错误
            tem = use.tem;  // 默认值
        }

        //相同或为空就退出
        if(title.isEmpty() || api.isEmpty() || key.isEmpty() || modelName.isEmpty()) {
            Toast.makeText(MainActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tem < 0 || tem > 2) {  // 温度值应该在0-2之间
            Toast.makeText(MainActivity.this, "温度值应在0-2之间", Toast.LENGTH_SHORT).show();
            return;
        }
        if(flag){
            model.add(new LLM(title,api,key,modelName,tem),()->{
                setModel();
            });
            return;
        }
        if(use.name.equals(title) && use.url.equals(api) &&
                use.key.equals(key) && use.modelName.equals(modelName) &&
                Double.compare(use.tem, tem) == 0)
            return;

        if(use.name != title) use.name = title;
        if(use.url != api) use.url = api;
        if(use.key != key) use.key = key;
        if(use.modelName != modelName) use.modelName = modelName;
        if(use.tem != tem) use.tem = tem;

        model.change(use,()->{
            setModel();
        });
    }

    //初始化模型菜单
    public void setModel(){
        RecyclerView ModelList = findViewById(R.id.model_list);
        ModelList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        modelList ModelListAdapter = new modelList(MainActivity.this,model.LLMlist);
        ModelList.setAdapter(ModelListAdapter);

        TextView modelSize = findViewById(R.id.model_size);
        modelSize.setText("当前数量:"+model.LLMlist.size());
        //编辑菜单中的保存
        TextView modelEnter = findViewById(R.id.model_enter);
        //新增按钮
        ImageButton modelAdd = findViewById(R.id.model_add);

        modelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLM now = new LLM("","","","",0);
                openEdit(now);
                modelEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveEdit(view,now,true);
                    }
                });
            }
        });

        ModelListAdapter.setOnItemClickListener(new modelList.OnItemClickListener() {
            //选中模型
            @Override
            public void modelEach(int position) {
                model.user = model.LLMlist.get(position);
                updataName();
                showModel(false);
            }

            //编辑当前模型
            @Override
            public void modelEdit(int position) {
                LLM now = model.LLMlist.get(position);
                openEdit(now);
                modelEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //发送当前模型
                        saveEdit(view,now,false);
                    }
                });
            }

            //删除模型
            @Override
            public void modelDel(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除此模型?")
                        .setPositiveButton("确定", (dialog, id) -> {
                            model.del(model.LLMlist.get(position));
                            setModel(); //重新加载
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }

    //模型菜单展现
    public void showModel(boolean flag){
        LinearLayout modelMenu = findViewById(R.id.model_menu);

        if(flag){
            modelMenu.setVisibility(View.VISIBLE);
            setModel();
        }else{
            modelMenu.setVisibility(View.GONE);
        }
    }

    //更新名称
    private void updataName(){
        TextView modelOpen = findViewById(R.id.model_open);
        if(model.user!=null)
            modelOpen.setText(model.user.name);
        else modelOpen.setText("未设置");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //12345678910
        TextView modelOpen = findViewById(R.id.model_open);

        //初始化模型
        model = new LLMData(getApplicationContext(), () -> {
            if (model.LLMlist.isEmpty()) {
                model.add(new LLM("1", "1", "1", "1", 1.2),()->{
                    if(!model.LLMlist.isEmpty())
                        model.user = model.LLMlist.get(model.LLMlist.size()-1);
                    updataName();
                    if (findViewById(R.id.model_menu).getVisibility() == View.VISIBLE)
                        setModel();
                });
            } else {
                model.user = model.LLMlist.get(model.LLMlist.size()-1);
                updataName();
                if (findViewById(R.id.model_menu).getVisibility() == View.VISIBLE)
                    setModel();
            }
        });

        //打开模型菜单
        modelOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModel(true);
            }
        });
        findViewById(R.id.model_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModel(false);
            }
        });

        All = new ArrayList<>();
        All.add(new ChatDay(1));
        All.get(0).day.add(new chatEach("测试A"));
        All.get(0).day.add(new chatEach("测试B"));
        All.get(0).day.add(new chatEach("测试C"));
        All.add(new ChatDay(2));
        All.get(1).day.add(new chatEach("轻轻的风如吹过"));
        All.add(new ChatDay(1));
        All.get(2).day.add(new chatEach("桂豪的性情分析"));
        All.get(1).day.add(new chatEach("宝宝和彬彬的异同性"));

        TextView modelRes = findViewById(R.id.model_res);
        EditText chatEdit = findViewById(R.id.chat_text);
        ImageButton chatEnter = findViewById(R.id.chat_enter);
        chatService = new ChatService();
        chatEnter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String userText = chatEdit.getText().toString().trim();
                if(model.user == null){
                    modelRes.setText("请先选择或添加一个模型");
                    Log.e("MainActivity", "用户模型设置缺失");
                    return;
                }
                if(userText.isEmpty()){
                    modelRes.setText("请输入问题");
                    Log.e("MainActivity", "用户未输入内容");
                    return;
                }

                modelRes.setText("正在请求...");

                chatService.getBackRes(model.user, userText, new ChatService.ChatCallBack() {
                    @Override
                    public void backRes(String modelMsg) {
                        modelRes.setText(modelMsg);
                        Log.i("MainActivity", "Model Response: " + modelMsg);
                        chatEdit.setText("");
                    }

                    @Override
                    public void backErr(String errorMsg) {
                        modelRes.setText(errorMsg);
                        Log.e("MainActivity", "API Error: " + errorMsg);
                    }
                });
            }
        });
    }
}