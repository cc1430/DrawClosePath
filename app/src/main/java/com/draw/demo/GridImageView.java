package com.draw.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

public class GridImageView extends AppCompatImageView {

    int row, column;
    float rectW, rectH;
    Paint linePaint = new Paint();
    Paint rectPaint = new Paint();
    Paint pathPaint = new Paint();
    Context context;
    int mode = MODE_DRAW;

    Path path = new Path();

    public static int MODE_DRAW = 0x1000;
    public static int MODE_ERASE = 0x1001;

    List<RectF> fillRectList = new ArrayList<>();
    int[][] integerArray;

    public GridImageView(@NonNull Context context) {
        super(context);
        this.context = context;
        setClickable(true);
    }

    public GridImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setClickable(true);
    }

    public GridImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setClickable(true);
    }

    RectF rectF = new RectF();
    Region region = new Region();
//    boolean bDrawPath = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(1f);
        linePaint.setStyle(Paint.Style.STROKE);

        rectPaint.setColor(context.getResources().getColor(R.color.red_paint));
        rectPaint.setStyle(Paint.Style.FILL);

        pathPaint.setColor(Color.RED);
        pathPaint.setStrokeWidth(2f);
        pathPaint.setStyle(Paint.Style.STROKE);

        if (column > 0) {
            rectW = 1.0f * getWidth() / column;
            for (int i = 0; i < column; i++) {
                canvas.drawLine(i * rectW, 0, i * rectW, getHeight(), linePaint);
            }
        }
        if (row > 0) {
            rectH = 1.0f * getHeight() / row;
            for (int i = 0; i < row; i++) {
                canvas.drawLine(0, i * rectH, getWidth(), i * rectH, linePaint);
            }
        }

        for (int i = 0; i < fillRectList.size(); i++) {
            canvas.drawRect(fillRectList.get(i), rectPaint);
        }

//        if (bDrawPath) {
//            Log.d("chenchen", "onDraw: draw path!");
//            canvas.drawPath(path, pathPaint);
//            bDrawPath = false;
//        }
    }

    float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (row <= 0 || column <= 0) {
            return false;
        }
        float pointX = event.getX();
        float pointY = event.getY();
        if (pointX > getWidth()) {
            pointX = getWidth() - 1;
        }
        if (pointY > getHeight()) {
            pointY = getHeight() - 1;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.d("chenchen", "onTouchEvent: down x = " + pointX + ", y = " + pointY);
                downX = pointX;
                downY = pointY;
                path.reset();
                path.moveTo(downX, downY);
                int indexColumn = (int) (pointX / rectW);
                int indexRow = (int) (pointY / rectH);
//                Log.d("chenchen", "onTouchEvent: indexRow = " + indexRow + ", indexColumn = " + indexColumn);

                RectF rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (mode == MODE_DRAW) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        integerArray[indexColumn][indexRow] = 1;
                    }
                } else {
                    fillRectList.remove(rectf);
                    integerArray[indexColumn][indexRow] = 0;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("chenchen", "onTouchEvent: move x = " + pointX + ", y = " + pointY);
                indexColumn = (int) (pointX / rectW);
                indexRow = (int) (pointY / rectH);
//                Log.d("chenchen", "onTouchEvent: indexRow = " + indexRow + ", indexColumn = " + indexColumn);
                path.lineTo(pointX, pointY);

                rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (mode == MODE_DRAW) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        integerArray[indexColumn][indexRow] = 1;
                    }
                } else {
                    fillRectList.remove(rectf);
                    integerArray[indexColumn][indexRow] = 0;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("chenchen", "onTouchEvent: cancel");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("chenchen", "onTouchEvent: up");
//                bDrawPath = true;
                path.lineTo(pointX, pointY);
                path.close();
                path.computeBounds(rectF, true);
                region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

                for (int i = 0; i < column; i++) {
                    for (int j = 0; j < row; j++) {
                        RectF tempRectF = new RectF(i * rectW, j * rectH, (i + 1) * rectW, (j + 1) * rectH);
                        if (region.contains((int) tempRectF.centerX(), (int) tempRectF.centerY())) {
                            if (mode == MODE_DRAW) {
                                if (!fillRectList.contains(tempRectF)) {
                                    fillRectList.add(tempRectF);
                                    integerArray[i][j] = 1;
                                }
                            } else {
                                fillRectList.remove(tempRectF);
                                integerArray[i][j] = 0;
                            }
                        }
                    }
                }
                invalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void clearAll() {
        path.reset();
        fillRectList.clear();
        invalidate();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void drawGrid(int row, int column) {
        this.row = row;
        this.column = column;
        this.integerArray = new int[column][row];
        invalidate();
    }

    public int[][] getIntegerArray() {
        return integerArray;
    }
}
