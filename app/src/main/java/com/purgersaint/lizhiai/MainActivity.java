package com.purgersaint.lizhiai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<ChatDay> All; //聊天内容
    RecyclerView chatDay;           //侧边栏
    private llm modelUse;           //正在使用的模型

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
    }

    //关闭侧边栏
    public void offLab(View t){
        LinearLayout lab = findViewById(R.id.lab);
        lab.setVisibility(View.GONE);
    }

    //打开编辑菜单
    public void openEdit(View t) {
        LinearLayout editMenu = findViewById(R.id.edit_menu);
        editMenu.setVisibility(View.VISIBLE);

        EditText llmTitle = findViewById(R.id.llm_title);
        EditText llmApi = findViewById(R.id.llm_api);
        EditText llmKey = findViewById(R.id.llm_key);
        EditText llmModel = findViewById(R.id.llm_model);
        SeekBar llmTemS = findViewById(R.id.llm_tem_s);
        EditText llmTemInput = findViewById(R.id.llm_tem_input);

        llmTitle.setText(modelUse.name);
        llmApi.setText(modelUse.url);
        llmKey.setText(modelUse.key);
        llmModel.setText(modelUse.modelName);
        llmTemInput.setText(Double.toString(modelUse.tem));
        llmTemS.setProgress((int)(modelUse.tem * 10));
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
    public void saveEdit(View t){
        offEdit(t);

        EditText llmTitle = findViewById(R.id.llm_title);
        EditText llmApi = findViewById(R.id.llm_api);
        EditText llmKey = findViewById(R.id.llm_key);
        EditText llmModel = findViewById(R.id.llm_model);
        EditText llmTemInput = findViewById(R.id.llm_tem_input);

        String title = llmTitle.getText().toString();
        String api = llmApi.getText().toString();
        String key = llmKey.getText().toString();
        String model = llmModel.getText().toString();
        double tem;
        try {
            tem = Double.parseDouble(llmTemInput.getText().toString());
            // 使用 tem
        } catch (NumberFormatException e) {
            // 用户输入的不是有效数字，设置默认值或提示错误
            tem = modelUse.tem;  // 默认值
        }

        if(title == ""||api == ""||key == ""||model == "") return;
        if(modelUse.name == title && modelUse.key == api && modelUse.key == key && modelUse.modelName == model && Double.compare(modelUse.tem, tem) != 0)
            return;

        if(modelUse.name != title) modelUse.name = title;
        if(modelUse.url != api) modelUse.url = api;
        if(modelUse.key != key) modelUse.key = key;
        if(modelUse.modelName != model) modelUse.modelName = model;
        if(modelUse.tem != tem) modelUse.tem = tem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //12345678910

        modelUse = new llm("1","1","1","1",1.2);
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

        setLab();

    }
}