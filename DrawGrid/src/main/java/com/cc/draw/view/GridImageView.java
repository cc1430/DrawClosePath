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
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.List;

public class GridImageView extends AppCompatImageView {

    private Context context;
    /**
     * 网格线行数和列数
     */
    private int row, column;
    /**
     * 每个网格的宽高
     */
    private float rectW, rectH;
    /**
     * 画笔
     */
    private Paint linePaint = new Paint();
    private Paint rectPaint = new Paint();
    private Paint pathPaint = new Paint();

    /**
     * 绘制路径
     */
    private Path drawPath = new Path();
    private boolean showPath;
    private boolean bDrawPath;

    private int mode;
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
    private List<RectF> fillRectList = new ArrayList<>();

    /**
     * 二位数组，返回每个网格的填充情况：1代表填充，0代表未填充
     */
    private int[][] integerArray;


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

        int col = getColumn();
        int ro = getRow();
        if (col > 0) {
            rectW = 1.0f * getWidth() / col;
            for (int i = 0; i < col; i++) {
                canvas.drawLine(i * rectW, 0, i * rectW, getHeight(), linePaint);
            }
        }
        if (ro > 0) {
            rectH = 1.0f * getHeight() / ro;
            for (int i = 0; i < ro; i++) {
                canvas.drawLine(0, i * rectH, getWidth(), i * rectH, linePaint);
            }
        }

        for (int i = 0; i < fillRectList.size(); i++) {
            canvas.drawRect(fillRectList.get(i), rectPaint);
        }

        if (bDrawPath && isShowPath()) {
            canvas.drawPath(drawPath, pathPaint);
            bDrawPath = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getRow() <= 0 || getColumn() <= 0) {
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
        int indexColumn = (int) (pointX / rectW);
        int indexRow = (int) (pointY / rectH);
        if (indexRow >= getRow() || indexColumn >= getColumn()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.reset();
                drawPath.moveTo(pointX, pointY);

                RectF rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (isDrawMode()) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        fillRect(indexRow, indexColumn, true);
                    }
                } else {
                    fillRectList.remove(rectf);
                    fillRect(indexRow, indexColumn, false);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(pointX, pointY);

                rectf = new RectF(indexColumn * rectW, indexRow * rectH, (indexColumn + 1) * rectW, (indexRow + 1) * rectH);
                if (isDrawMode()) {
                    if (!fillRectList.contains(rectf)) {
                        fillRectList.add(rectf);
                        fillRect(indexRow, indexColumn, true);
                    }
                } else {
                    fillRectList.remove(rectf);
                    fillRect(indexRow, indexColumn, false);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.e("chenchen", "onTouchEvent: cancel");
                break;
            case MotionEvent.ACTION_UP:
                bDrawPath = true;
                drawPath.lineTo(pointX, pointY);
                drawPath.close();
                RectF rectF = new RectF();
                drawPath.computeBounds(rectF, true);
                Region region = new Region();
                region.setPath(drawPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

                for (int i = 0; i < getRow(); i++) {
                    for (int j = 0; j < getColumn(); j++) {
                        RectF tempRectF = new RectF(j * rectW, i * rectH, (j + 1) * rectW, (i + 1) * rectH);
                        if (region.contains((int) tempRectF.centerX(), (int) tempRectF.centerY())) {
                            if (isDrawMode()) {
                                if (!fillRectList.contains(tempRectF)) {
                                    fillRectList.add(tempRectF);
                                    fillRect(i, j, true);
                                }
                            } else {
                                fillRectList.remove(tempRectF);
                                fillRect(i, j, false);
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
        init(getRow(), getColumn());
        invalidate();
    }

    public void drawGrid(int row, int column) {
        init(row, column);
        setMode(MODE_DRAW);
        invalidate();
    }

    public void drawArea(String area) {
        if (TextUtils.isEmpty(area) && !area.contains(",")) {
            return;
        }
        try {
            String[] split  = area.split(",");
            int col = getColumn();
            for (int i = 0; i < split.length; i++) {
                String str = Integer.toBinaryString(Integer.parseInt(split[i]));
                if (col != str.length()) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < col - str.length(); j++) {
                        stringBuffer.append("0");
                    }
                    stringBuffer.append(str);
                    str = stringBuffer.toString();
                }

                for (int j = 0; j < col; j++) {
                    if ("1".equals(String.valueOf(str.charAt(j)))) {
                        RectF rectf = new RectF(j * rectW, i * rectH, (j + 1) * rectW, (i + 1) * rectH);
                        if (!fillRectList.contains(rectf)) {
                            fillRectList.add(rectf);
                            fillRect(i, j, true);
                        }
                    }
                }
            }
            invalidate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private native int getRow();

    private native int getColumn();

    private native int[][] init(int row, int column);

    public native String getArea();

    public native void setMode(int mode);

    public native void setShowPath(boolean bShowPath);

    private native boolean isShowPath();

    public native boolean isDrawMode();

    public native boolean isEraseMode();

    private native void fillRect(int x, int y, boolean bFill);
}

