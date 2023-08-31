package com.draw.demo;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cc.draw.view.DragXyView;

public class DragXyFragment extends Fragment {

    private DragXyView dragXyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_drag_xy, container, false);
        dragXyView = root.findViewById(R.id.drag_xy_view);

        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        Log.d("chenchen", "onCreateView: w = " + metric.widthPixels);

        float x = 500f;
        dragXyView.addXyView(
                new PointF(0f, 0f),
                new PointF(0f, x),
                new PointF(0f, 2*x),
                new PointF(2*x, 2*x),
                new PointF(2*x, x),
                new PointF(2*x, 0f));

        return root;
    }
}
