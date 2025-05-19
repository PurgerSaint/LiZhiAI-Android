package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView tv_recommed, tv_must_buy;
    private TextView tvSelectedInfo; // 显示已选信息
    private String[] names1 = {"爆款*肥牛鱼豆腐骨肉相连三荤五素一份米饭", "豪华双人套餐", "【热销】双人套餐（含两份米饭）", "麻辣小龙虾", "经典烤鱼"};
    private String[] sales1 = {"月售545 好评度87%", "月售650 好评度90%", "月售456 好评度85%", "月售1622 好评度85%", "月售1232 好评度90%"};
    private String[] prices1 = {"¥23", "¥41", "¥32", "¥69", "¥78"};
    private int[] imgs1 = {R.drawable.recom_one, R.drawable.recom_two, R.drawable.recom_three, R.drawable.longxia, R.drawable.kaoyu};

    private String[] names2 = {"素菜主义一人套餐", "两人经典套套餐", "三人经典套餐", "小鸡炖汤", "芹菜水饺"};
    private String[] sales2 = {"月售56 好评度60%", "月售625 好评度84%", "月售650 好评度86%", "月售400 好评度92%", "月售350 好评度95%"};
    private String[] prices2 = {"¥23", "¥43", "¥50", "¥27", "¥15"};
    private int[] imgs2 = {R.drawable.must_buy_one, R.drawable.must_buy_two, R.drawable.must_buy_three, R.drawable.jitang, R.drawable.shuijiao};

    private Map<String, List<FoodBean>> map;
    private RightFragment rightFragment;
    private double totalPrice = 0.0; // 总金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedInfo = findViewById(R.id.tv_selected_info);
        Button btnCheckout = findViewById(R.id.btn_checkout);

        // 设置结账按钮点击事件
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
            // 传递已选菜品列表和总金额
            intent.putExtra("total_price", totalPrice);
            intent.putExtra("selected_list", (ArrayList<FoodBean>) getSelectedFoods());
            startActivity(intent);
        });

        setData();
        init();
        clickEvent();
    }

    private void init() {
        tv_recommed = findViewById(R.id.tv_recommend);
        tv_must_buy = findViewById(R.id.tv_must_buy);
    }

    private void setData() {
        map = new HashMap<>();
        List<FoodBean> list1 = new ArrayList<>();
        List<FoodBean> list2 = new ArrayList<>();
        // 确保数组长度一致
        if (names1.length == prices1.length && names1.length == imgs1.length && names1.length == sales1.length) {
            for (int i = 0; i < names1.length; i++) {
                FoodBean bean = new FoodBean();
                bean.setName(names1[i]);
                bean.setPrice(prices1[i]);
                bean.setImg(imgs1[i]);
                bean.setSales(sales1[i]);
                list1.add(bean);
            }
            map.put("1", list1);
        }
        if (names2.length == prices2.length && names2.length == imgs2.length && names2.length == sales2.length) {
            for (int i = 0; i < names2.length; i++) {
                FoodBean bean = new FoodBean();
                bean.setName(names2[i]);
                bean.setPrice(prices2[i]);
                bean.setImg(imgs2[i]);
                bean.setSales(sales2[i]);
                list2.add(bean);
            }
            map.put("2", list2);
        }
    }

    private void clickEvent() {
        tv_recommed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchData(map.get("1"));
                tv_recommed.setBackgroundColor(Color.WHITE);
            }
        });
        tv_must_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchData(map.get("2"));
                tv_must_buy.setBackgroundColor(Color.WHITE);
            }
        });
        switchData(map.get("1"));
    }

    public void switchData(List<FoodBean> list) {
        rightFragment = new RightFragment().getInstance(list);
        rightFragment.setOnAdapterInitializedListener(adapter -> {
            adapter.setOnSelectedListener(() -> {
                updateSelectedInfo(); // 选中状态变化时更新信息
            });
        });
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.right, rightFragment);
        fragmentTransaction.commit();
    }

    // 获取已选菜品列表
    private List<FoodBean> getSelectedFoods() {
        List<FoodBean> selectedList = new ArrayList<>();
        List<FoodBean> currentList = (List<FoodBean>) rightFragment.getArguments().getSerializable("list");
        for (FoodBean food : currentList) {
            if (food.isSelected()) {
                selectedList.add(food);
            }
        }
        return selectedList;
    }

    // 更新已选数量和总金额
    public void updateSelectedInfo() {
        int count = 0;
        totalPrice = 0.0;
        List<FoodBean> currentList = (List<FoodBean>) rightFragment.getArguments().getSerializable("list");
        for (FoodBean food : currentList) {
            if (food.isSelected()) {
                count++;
                // 解析价格（假设价格格式为"¥23"）
                String priceStr = food.getPrice().replace("¥", "");
                totalPrice += Double.parseDouble(priceStr);
            }
        }
        tvSelectedInfo.setText(String.format("已选 %d 件，合计：¥%.2f", count, totalPrice));
    }
}