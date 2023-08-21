package com.cc.draw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

public class GridImageView extends AppCompatImageView {

    Context context;
    /**
     * 网格线行数和列数
     */
    int row, column;
    /**
     * 每个网格的宽高
     */
    float rectW, rectH;
    /**
     * 画笔
     */
    Paint linePaint = new Paint();
    Paint rectPaint = new Paint();
    Paint pathPaint = new Paint();

    /**
     * 绘制路径
     */
    Path drawPath = new Path();
    boolean showPath = false;
    private boolean bDrawPath;

    int mode = MODE_DRAW;
    /**
     * 模式：绘制
     */
    public static int MODE_DRAW = 0x1000;
    /**
     * 模式：擦除
     */
    public static int MODE_ERASE = 0x1001;

    /**
     * 存放需要填充区域的矩形
     */
    List<RectF> fillRectList = new ArrayList<>();

    /**
     * 二位数组，返回每个网格的填充情况：1代表填充，0代表未填充
     */
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        linePaint.setColor(Color.parseColor("#80D8D8D8"));
        linePaint.setStrokeWidth(2f);
        linePaint.setStyle(Paint.Style.STROKE);

        rectPaint.setColor(Color.parseColor("#66007FFF"));
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

        if (bDrawPath && showPath) {
//            Log.d("chenchen", "onDraw: draw path!");
            canvas.drawPath(drawPath, pathPaint);
            bDrawPath = false;
        }
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
        } else if (pointX < 0) {
            pointX = 0;
        }
        if (pointY > getHeight()) {
            pointY = getHeight() - 1;
        } else if (pointY < 0) {
            pointY = 0;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("chenchen", "onTouchEvent: down x = " + pointX + ", y = " + pointY);
                downX = pointX;
                downY = pointY;
                drawPath.reset();
                drawPath.moveTo(downX, downY);
                int indexColumn = (int) (pointX / rectW);
                int indexRow = (int) (pointY / rectH);
                Log.d("chenchen", "onTouchEvent: indexRow = " + indexRow + ", indexColumn = " + indexColumn);

                RectF rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (mode == MODE_DRAW) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        integerArray[indexRow][indexColumn] = 1;
                    }
                } else {
                    fillRectList.remove(rectf);
                    integerArray[indexRow][indexColumn] = 0;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("chenchen", "onTouchEvent: move x = " + pointX + ", y = " + pointY);
                indexColumn = (int) (pointX / rectW);
                indexRow = (int) (pointY / rectH);
                Log.d("chenchen", "onTouchEvent: indexRow = " + indexRow + ", indexColumn = " + indexColumn);
                drawPath.lineTo(pointX, pointY);

                rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (mode == MODE_DRAW) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        integerArray[indexRow][indexColumn] = 1;
                    }
                } else {
                    fillRectList.remove(rectf);
                    integerArray[indexRow][indexColumn] = 0;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.e("chenchen", "onTouchEvent: cancel");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("chenchen", "onTouchEvent: up");
                bDrawPath = true;
                drawPath.lineTo(pointX, pointY);
                drawPath.close();
                RectF rectF = new RectF();
                drawPath.computeBounds(rectF, true);
                Region region = new Region();
                region.setPath(drawPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        RectF tempRectF = new RectF(j * rectW, i * rectH, (j + 1) * rectW, (i + 1) * rectH);
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
        drawPath.reset();
        fillRectList.clear();
        this.integerArray = new int[row][column];
        invalidate();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void showDrawPath(boolean bShow) {
        this.showPath = bShow;
    }

    public void drawGrid(int row, int column) {
        this.row = row;
        this.column = column;
        this.integerArray = new int[row][column];
        invalidate();
    }

    public int[][] getIntegerArray() {
        return integerArray;
    }

    public String getArea() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < row; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < column; j++) {
                sb.append(integerArray[i][j]);
            }
            int digit = Integer.parseInt(String.valueOf(sb), 2);
            result.append(digit);
            if (i != row - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public void drawArea(String area) {
        if (TextUtils.isEmpty(area) && !area.contains(",")) {
            return;
        }
        try {
            String[] split  = area.split(",");
            for (int i = 0; i < split.length; i++) {
                String str = Integer.toBinaryString(Integer.parseInt(split[i]));
                if (column != str.length()) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < column - str.length(); j++) {
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(str);
                    str = stringBuffer.toString();
                }

                for (int j = 0; j < column; j++) {
                    if ("1".equals(String.valueOf(str.charAt(j)))) {
                        RectF rectf = new RectF(j * rectW, i * rectH, (j + 1) * rectW, (i + 1) * rectH);
                        if (!fillRectList.contains(rectf)) {
                            fillRectList.add(rectf);
                            integerArray[i][j] = 1;
                        }
                    }
                }
            }
            invalidate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String area = "8,3,0";
//        int row = 3, column = 4;
//        int[][] array = new int[row][column];
//        try {
//            String[] split  = area.split(",");
//            for (int i = 0; i < split.length; i++) {
//                String str = Integer.toBinaryString(Integer.parseInt(split[i]));
////                System.out.println(str);
//                if (column != str.length()) {
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int j = 0; j < column - str.length(); j++) {
//                        stringBuffer.append("0");
//                    }
//                    stringBuffer.append(str);
//                    str = stringBuffer.toString();
//                }
//
//                for (int j = 0; j < column; j++) {
//                    array[i][j] = Integer.parseInt(String.valueOf(str.charAt(j)));
//
//                    RectF rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
//                    if (mode == MODE_DRAW) {
//                        if (!fillRectList.contains(rectf)) {
//                            fillRectList.add(rectf);
//                            integerArray[indexRow][indexColumn] = 1;
//                        }
//                    }
//                }
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 0; i < row; i++) {
//            for (int j = 0; j < column; j++) {
//                System.out.println(array[i][j]);
//            }
//        }
//    }
}

