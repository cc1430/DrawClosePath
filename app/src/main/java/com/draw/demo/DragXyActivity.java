package com.draw.demo;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.cc.draw.view.DragXyView;

import java.lang.reflect.Field;

public class DragXyActivity extends AppCompatActivity {

    private DragXyView dragXyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_xy);
        dragXyView = findViewById(R.id.drag_xy_view);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        Log.d("chenchen", "onCreateView: w = " + metric.widthPixels + ", h = " + metric.heightPixels);

        Log.d("chenchen", "statusBarHeight = " + getStatusBarHeight(this));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dragXyView.getLayoutParams();
        int height = metric.heightPixels - getStatusBarHeight(this) - 60;
        int width = height * 16 / 9;
        params.width = width;
        params.height = height;
        dragXyView.setLayoutParams(params);

        dragXyView.addXyView(
                new PointF(0f, 0f),
                new PointF(0f, height / 2.0f),
                new PointF(0f, height),
                new PointF(width, height),
                new PointF(width, height / 2.0f),
                new PointF(width, 0f));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            Log.v("@@@@@@", "the status bar height is : " + statusBarHeight);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}