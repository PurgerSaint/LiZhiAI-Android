package com.purgersaint.lizhiai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<ChatDay> All; //聊天内容
    RecyclerView chatDay;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //12345678910

        llm test = new llm("1","1","1","1",1.2);
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