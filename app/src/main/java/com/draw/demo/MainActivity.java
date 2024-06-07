package com.draw.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cc.draw.sdk.CCSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CCSdk.getInstance().init();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> dataList = new ArrayList<>();
        dataList.add("绘制闭合区域示例");
        dataList.add("可拖动多边形示例");
        adapter = new MyAdapter(this, dataList);

        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new MyAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, DrawGridActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, DragXyActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

//        Intent intent = new Intent(MainActivity.this, DragXyActivity.class);
//        startActivity(intent);
    }
}