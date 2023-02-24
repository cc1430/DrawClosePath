package com.draw.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GridImageView gridImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridImageView = findViewById(R.id.iv_grid);
        gridImageView.drawGrid(12, 20);

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
    }
}