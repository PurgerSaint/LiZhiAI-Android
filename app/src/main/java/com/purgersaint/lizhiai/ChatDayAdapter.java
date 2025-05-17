package com.purgersaint.lizhiai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatDayAdapter extends RecyclerView.Adapter<ChatDayAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatDay> chatList;

    public ChatDayAdapter(Context context, ArrayList<ChatDay> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatDayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.chat_list_day,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDayAdapter.MyViewHolder holder, int position) {
        holder.dayTittle.setText(chatList.get(position).getTime());
        if(holder.title.getLayoutManager() == null) {
            holder.title.setLayoutManager(new LinearLayoutManager(context));
        }
        ChatEachAdapter chatEachAdapter = new ChatEachAdapter(context,chatList.get(position).day);
        holder.title.setAdapter(chatEachAdapter);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dayTittle;
        RecyclerView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dayTittle = itemView.findViewById(R.id.day_title);
            this.title = itemView.findViewById(R.id.day_each);
        }

    }
}
