package com.purgersaint.lizhiai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatEachAdapter extends RecyclerView.Adapter<ChatEachAdapter.MyViewHolder> {
    Context context;
    ArrayList<chatEach> day;    //数据源

    public ChatEachAdapter(Context context, ArrayList<chatEach> day) {
        this.context = context;
        this.day = day;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.chat_list_eachday,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.chatTitle.setText(day.get(position).title);
    }

    @Override
    public int getItemCount() {
        return day.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView chatTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.chatTitle = itemView.findViewById(R.id.chat_title);
        }
    }
}
