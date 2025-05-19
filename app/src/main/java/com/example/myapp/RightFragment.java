package com.example.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.List;

public class RightFragment extends Fragment {
    private ListView lv_list;
    private FoodAdapter adapter;
    private OnAdapterInitializedListener listener;

    public RightFragment() {
    }

    public RightFragment getInstance(List<FoodBean> list) {
        RightFragment rightFragment = new RightFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        rightFragment.setArguments(bundle);
        return rightFragment;
    }

    public void setOnAdapterInitializedListener(OnAdapterInitializedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_layout, container, false);
        lv_list = view.findViewById(R.id.lv_list);
        if (getArguments() != null) {
            List<FoodBean> list = (List<FoodBean>) getArguments().getSerializable("list");
            adapter = new FoodAdapter(getActivity(), list);
            lv_list.setAdapter(adapter);

            adapter.setOnSelectedListener(() -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateSelectedInfo();
                }
            });

            if (listener != null) {
                listener.onAdapterInitialized(adapter);
            }
        }
        return view;
    }

    public FoodAdapter getAdapter() {
        return adapter;
    }

    public interface OnAdapterInitializedListener {
        void onAdapterInitialized(FoodAdapter adapter);
    }
}