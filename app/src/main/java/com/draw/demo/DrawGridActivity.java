package com.draw.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cc.draw.view.GridImageView;

public class DrawGridActivity extends AppCompatActivity {

    GridImageView gridImageView;
    int row = 12, column = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_grid);

        gridImageView = findViewById(R.id.iv_grid);
        gridImageView.drawGrid(row, column);
//        gridImageView.showDrawPath(true);
        gridImageView.post(new Runnable() {
            @Override
            public void run() {
                gridImageView.drawArea("0,0,0,0,4032,8160,8160,8064,7680,2048,0,0");
            }
        });


        TextView tv = findViewById(R.id.tv_mode);

        Button clearBtn = findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridImageView.clearAll();
            }
        });

        Button eraseBtn = findViewById(R.id.btn_eraser);
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridImageView.setMode(GridImageView.MODE_ERASE);
                tv.setText(eraseBtn.getText());
            }
        });

        Button drawBtn = findViewById(R.id.btn_draw);
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridImageView.setMode(GridImageView.MODE_DRAW);
                tv.setText(drawBtn.getText());
            }
        });

        tv.setOnClickListener(v->{
            Log.d("chenchen", "area = " + gridImageView.getArea());
        });
    }
}