package com.purgersaint.lizhiai;

import java.util.ArrayList;

//每次
class chat{
    llm model;          //使用的模型
    String userText;    //用户发送的文字
    String modelText;   //模型回答的结果

    public chat(llm m,String u,String mt){
        this.model = m;
        this.userText = u;
        this.modelText = mt;
    }
}
//一轮聊天（会话）
class chatEach{
    String title;           //会话标题
    ArrayList<chat> each;   //对话列表

    public chatEach(String t){
        this.title = t;
        this.each = new ArrayList<>();
    }

    public void addChat(chat word){
        this.each.add(word);
    }
}
//每天的聊天
public class ChatDay{
    int time;   //时间（年-月-日）
    ArrayList<chatEach> day;

    public ChatDay(int t){
        this.time=t;
        this.day=new ArrayList<>();
    }

    public String getTime(){
        if(this.time==1) return "今天";
        else return "昨天";
    }
}
