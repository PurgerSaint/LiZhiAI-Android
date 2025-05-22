package com.purgersaint.lizhiai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class modelList extends RecyclerView.Adapter<modelList.MyViewHolder> {
    List<LLM> LLMList;
    Context context;

    public modelList(Context context,List<LLM> LLMList) {
        this.LLMList = LLMList;
        this.context = context;
    }

    //初始化监听接口
    public interface OnItemClickListener{
        void modelEach(int position);
        void modelEdit(int position);
        void modelDel(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    //指定模版文件
    @NonNull
    @Override
    public modelList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.model_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull modelList.MyViewHolder holder, int position) {
        LLM now = LLMList.get(position);
        holder.modelId.setText(String.valueOf(now.id));
        holder.modelName.setText(now.name);
        holder.modelPla.setText(now.modelName);
        holder.modelTem.setText("温度值:"+String.format("%.2f",now.tem));

        holder.modelEach.setOnClickListener(v->{
            if(listener != null)
                listener.modelEach(position);
        });

        holder.modelEdit.setOnClickListener(v->{
            if(listener != null)
                listener.modelEdit(position);
        });

        holder.modelDel.setOnClickListener(v->{
            if(listener != null)
                listener.modelDel(position);
        });
    }

    @Override
    public int getItemCount() {
        return LLMList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView modelId;
        TextView modelName;
        TextView modelPla;
        TextView modelTem;
        LinearLayout modelEach;
        ImageButton modelDel;
        ImageButton modelEdit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.modelId = itemView.findViewById(R.id.model_id);
            this.modelName = itemView.findViewById(R.id.model_name);
            this.modelPla = itemView.findViewById(R.id.model_pla);
            this.modelTem = itemView.findViewById(R.id.model_tem);
            this.modelEach = itemView.findViewById(R.id.each_model);
            this.modelEdit = itemView.findViewById(R.id.model_edit);
            this.modelDel = itemView.findViewById(R.id.model_del);
        }
    }
}
