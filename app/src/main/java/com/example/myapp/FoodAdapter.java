package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context mContext;
    private List<FoodBean> foodList;
    private OnSelectedListener onSelectedListener;

    public FoodAdapter(Context context, List<FoodBean> list) {
        this.mContext = context;
        this.foodList = list;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.ivImg = convertView.findViewById(R.id.iv_img);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvSale = convertView.findViewById(R.id.tv_sale);
            holder.tvPrice = convertView.findViewById(R.id.tv_price);
            holder.cbSelected = convertView.findViewById(R.id.cb_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FoodBean food = foodList.get(position);
        holder.ivImg.setBackgroundResource(food.getImg());
        holder.tvName.setText(food.getName());
        holder.tvSale.setText(food.getSales());
        holder.tvPrice.setText(food.getPrice());
        holder.cbSelected.setChecked(food.isSelected());

        holder.cbSelected.setOnClickListener(v -> {
            food.setSelected(holder.cbSelected.isChecked());
            if (onSelectedListener != null) {
                onSelectedListener.onSelectedChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView ivImg;
        TextView tvName, tvSale, tvPrice;
        CheckBox cbSelected;
    }

    public interface OnSelectedListener {
        void onSelectedChanged();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
    }
}
