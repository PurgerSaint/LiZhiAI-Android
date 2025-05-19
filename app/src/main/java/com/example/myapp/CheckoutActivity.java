package com.example.myapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // 获取传递的数据
        double totalPrice = getIntent().getDoubleExtra("total_price", 0.0);
        List<FoodBean> selectedList = (List<FoodBean>) getIntent().getSerializableExtra("selected_list");

        // 显示总金额
        TextView tvTotal = findViewById(R.id.tv_checkout_total);
        tvTotal.setText(String.format("总金额：¥%.2f", totalPrice));

        // 显示已选菜品列表
        ListView listView = findViewById(R.id.lv_selected_foods);
        CheckoutAdapter adapter = new CheckoutAdapter(this, selectedList);
        listView.setAdapter(adapter);
    }

    // 结账页面适配器
    static class CheckoutAdapter extends android.widget.BaseAdapter {
        private android.content.Context context;
        private List<FoodBean> data;

        public CheckoutAdapter(android.content.Context context, List<FoodBean> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(context).inflate(R.layout.list_item_checkout, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.tv_checkout_name);
                holder.tvPrice = convertView.findViewById(R.id.tv_checkout_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FoodBean food = data.get(position);
            holder.tvName.setText(food.getName());
            holder.tvPrice.setText(food.getPrice());
            return convertView;
        }

        static class ViewHolder {
            TextView tvName;
            TextView tvPrice;
        }
    }
}