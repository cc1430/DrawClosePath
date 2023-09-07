package com.cc.draw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class DragXyView extends View {


    private static final int NONE = -1;

    /**
     * 多边形信息
     */
    private List<XyPoint> mXyPointList;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 画笔描边的宽度
     */
    private float mStrokeWidth = 6f;

    /**
     * 绘制点坐标时基于画笔描边的宽度倍数
     */
    private float mPointStrokeWidthMultiplier = 4.0f;

    /**
     * 多边形点的颜色
     */
    private int mPointNormalColor = 0x00E46F00;

    /**
     * 多边形点按下状态时的颜色
     */
    private int mPointPressedColor = 0xFFE46F00;

    /**
     * 多边形点选中状态时的颜色
     */
    private int mPointSelectedColor = 0xFFE46F00;

    /**
     * 多边形边线的颜色
     */
    private int mLineNormalColor = 0xFFFFFFFF;
    /**
     * 多边形边线按下状态的颜色
     */
    private int mLinePressedColor = 0xFFE46F00;

    /**
     * 多边形边线选中状态的颜色
     */
    private int mLineSelectedColor = 0xFFE46F00;
    /**
     * 多边形填充的颜色
     */
    private int mFillNormalColor = 0x4DF57200;
    /**
     * 多边形填充按下状态时的颜色
     */
    private int mFillPressedColor = 0x9CF57200;

    /**
     * 多边形填充选中状态时的颜色
     */
    private int mFillSelectedColor = 0x9CF57200;

    /**
     * 触摸多边形的当前索引
     */
    private int mXyViewPosition = NONE;

    /**
     * 触摸点的当前索引
     */
    private int mEventPointIndex = NONE;

    /**
     * 触摸事件点的X坐标
     */
    private float mEventX;
    /**
     * 触摸事件点的Y坐标
     */
    private float mEventY;

    /**
     * 最近一次点的X坐标
     */
    private float mLastX;
    /**
     * 最近一次点的Y坐标
     */
    private float mLastY;

    /**
     * 按下时点的X坐标
     */
    private float mDownX;
    /**
     * 按下时点的Y坐标
     */
    private float mDownY;

    /**
     * 触点允许的误差偏移量
     */
    private float mAllowableOffsets;
    /**
     * 是否是拖动事件
     */
    private boolean isDragEvent;

    /**
     * 是否启用拖动多边形
     */
    private boolean isDragEnabled = true;

    /**
     * 是否启用多边形的各个角的角度支持可变
     */
    private boolean isChangeAngleEnabled = true;

    /**
     * 是否拦截Touch事件
     */
    private boolean isIntercept;

    /**
     * 改变监听器
     */
    private OnChangeListener mOnChangeListener;
    /**
     * 点击监听器
     */
    private OnXyViewClickListener mOnXyViewClickListener;
    /**
     * 长按监听器
     */
    private OnXyViewLongClickListener mOnXyViewLongClickListener;

    /**
     * 是否运行长按事件
     */
    private boolean isLongClickRunnable;

    /**
     * 触摸可倾斜的距离，用于区分点击和滑动事件的临界点
     */
    private int mTouchSlop;

    /**
     * 长按超时时间（用于判定触发长按事件）
     */
    private int mLongPressTimeout;

    /**
     * 是否是点击事件
     */
    private boolean isClickEvent;

    /**
     * 是否是多选模式
     */
    private boolean isMultipleSelection;

    /**
     * 多边形选中位置
     */
    private int mXyViewSelectPosition = NONE;

    /**
     * 是否点击切换选中状态
     */
    private boolean isClickToggleSelected;

    /**
     * 是否允许多边形拖出视图范围
     */
    private boolean isAllowDragOutView;

    /**
     * 文本字体大小
     */
    private float mTextSize;

    /**
     * 文本的颜色
     */
    private int mTextNormalColor = 0xFFE5574C;

    /**
     * 文本按下状态的颜色
     */
    private int mTextPressedColor;

    /**
     * 文本选中状态的颜色
     */
    private int mTextSelectedColor;

    /**
     * 是否显示多边形的Text
     */
    private boolean isShowText = true;

    /**
     * 多边形Text的字体是否为粗体
     */
    private boolean isFakeBoldText = false;


    public DragXyView(Context context) {
        this(context, null);
    }

    public DragXyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragXyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragXyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initDefaultValue(Context context) {
        mXyPointList = new ArrayList<>();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        mAllowableOffsets = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        initDefaultValue(context);

//        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragXyView, defStyleAttr, defStyleRes);
//        final int count = a.getIndexCount();
//        for (int i = 0; i < count; i++) {
//            int attr = a.getIndex(i);
//            if (attr == R.styleable.DragXyView_dpvStrokeWidth) {
//                mStrokeWidth = a.getFloat(attr, mStrokeWidth);
//            } else if (attr == R.styleable.DragXyView_dpvPointStrokeWidthMultiplier) {
//                mPointStrokeWidthMultiplier = a.getFloat(attr, mPointStrokeWidthMultiplier);
//            } else if (attr == R.styleable.DragXyView_dpvPointNormalColor) {
//                mPointNormalColor = a.getColor(attr, mPointNormalColor);
//            } else if (attr == R.styleable.DragXyView_dpvPointPressedColor) {
//                mPointPressedColor = a.getColor(attr, mPointPressedColor);
//            } else if (attr == R.styleable.DragXyView_dpvPointSelectedColor) {
//                mPointSelectedColor = a.getColor(attr, mPointSelectedColor);
//            } else if (attr == R.styleable.DragXyView_dpvLineNormalColor) {
//                mLineNormalColor = a.getColor(attr, mLineNormalColor);
//            } else if (attr == R.styleable.DragXyView_dpvLinePressedColor) {
//                mLinePressedColor = a.getColor(attr, mLinePressedColor);
//            } else if (attr == R.styleable.DragXyView_dpvLineSelectedColor) {
//                mLineSelectedColor = a.getColor(attr, mLineSelectedColor);
//            } else if (attr == R.styleable.DragXyView_dpvFillNormalColor) {
//                mFillNormalColor = a.getColor(attr, mFillNormalColor);
//            } else if (attr == R.styleable.DragXyView_dpvFillPressedColor) {
//                mFillPressedColor = a.getColor(attr, mFillPressedColor);
//            } else if (attr == R.styleable.DragXyView_dpvFillSelectedColor) {
//                mFillSelectedColor = a.getColor(attr, mFillSelectedColor);
//            } else if (attr == R.styleable.DragXyView_dpvAllowableOffsets) {
//                mAllowableOffsets = a.getDimension(attr, mAllowableOffsets);
//            } else if (attr == R.styleable.DragXyView_dpvDragEnabled) {
//                isDragEnabled = a.getBoolean(attr, isDragEnabled);
//            } else if (attr == R.styleable.DragXyView_dpvChangeAngleEnabled) {
//                isChangeAngleEnabled = a.getBoolean(attr, isChangeAngleEnabled);
//            } else if (attr == R.styleable.DragXyView_dpvMultipleSelection) {
//                isMultipleSelection = a.getBoolean(attr, isMultipleSelection);
//            } else if (attr == R.styleable.DragXyView_dpvClickToggleSelected) {
//                isClickToggleSelected = a.getBoolean(attr, isClickToggleSelected);
//            } else if (attr == R.styleable.DragXyView_dpvAllowDragOutView) {
//                isAllowDragOutView = a.getBoolean(attr, isAllowDragOutView);
//            } else if (attr == R.styleable.DragXyView_dpvTextSize) {
//                mTextSize = a.getDimension(attr, mTextSize);
//            } else if (attr == R.styleable.DragXyView_dpvTextNormalColor) {
//                mTextNormalColor = a.getColor(attr, mTextNormalColor);
//            } else if (attr == R.styleable.DragXyView_dpvTextPressedColor) {
//                mTextPressedColor = a.getColor(attr, mTextPressedColor);
//            } else if (attr == R.styleable.DragXyView_dpvTextSelectedColor) {
//                mTextSelectedColor = a.getColor(attr, mTextSelectedColor);
//            } else if (attr == R.styleable.DragXyView_dpvShowText) {
//                isShowText = a.getBoolean(attr, isShowText);
//            } else if (attr == R.styleable.DragXyView_dpvFakeBoldText) {
//                isFakeBoldText = a.getBoolean(attr, isFakeBoldText);
//            }
//        }
//        a.recycle();

        mPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawXyViews(canvas);
    }

    /**
     * 绘制多个多边形
     *
     * @param canvas
     */
    private void drawXyViews(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        int size = mXyPointList.size();
        boolean isSelected;
        for (int i = 0; i < size; i++) {
            isDrag = i == mXyViewPosition;
            isSelected = isMultipleSelection ? mXyPointList.get(i).isSelected : i == mXyViewSelectPosition;
            drawXyView(canvas, mXyPointList.get(i), i == mXyViewPosition, isSelected);
        }
    }

    boolean isDrag;

    /**
     * 绘制多边形
     *
     * @param canvas
     */
    private void drawXyView(Canvas canvas, XyPoint xyPoint, boolean isPressed, boolean isSelected) {
        PointF[] points = xyPoint.getPoints();
        if (points != null && points.length > 0) {
            int size = points.length;
            float[] lines = new float[size << 1];
            Path path = new Path();
            path.moveTo(points[0].x, points[0].y);
            for (int i = 0, j = 0; i < size; i++) {
                j = i << 1;
                PointF pointF = points[i];
                lines[j] = pointF.x;
                lines[j + 1] = pointF.y;
                //将点连线
                if (i > 0) {
                    path.lineTo(pointF.x, pointF.y);
                }
            }
            path.close();
            //根据路径绘制区域
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setPathEffect(new DashPathEffect(new float[]{15, 15}, 0));
            mPaint.setColor(obtainColor(isPressed, isSelected, mLineNormalColor, mLinePressedColor, mLineSelectedColor));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, mPaint);
            //根据路径绘制区域填充
            mPaint.setColor(obtainColor(isPressed, isSelected, mFillNormalColor, mFillPressedColor, mFillSelectedColor));
            if (mPaint.getColor() != 0) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, mPaint);
            }

            //绘制点
            if (mPointStrokeWidthMultiplier > 0) {
                mPaint.setStrokeWidth(mStrokeWidth * mPointStrokeWidthMultiplier);
                mPaint.setColor(obtainColor(isPressed, isSelected, mPointNormalColor, mPointPressedColor, mPointSelectedColor));
                canvas.drawPoints(lines, mPaint);

                if (mEventPointIndex >= 0 && isDrag) {
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setColor(Color.WHITE);
                    mPaint.setStrokeWidth(10);
                    mPaint.setPathEffect(null);
                    canvas.drawCircle(points[mEventPointIndex].x, points[mEventPointIndex].y, 18, mPaint);
                }
            }

            //绘制Text
            if (isShowText && !TextUtils.isEmpty(xyPoint.getText())) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setFakeBoldText(isFakeBoldText);
                mPaint.setTextSize(mTextSize);
                mPaint.setColor(obtainColor(isPressed, isSelected, mTextNormalColor, mTextPressedColor, mTextSelectedColor));
                mPaint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
                float centerX = (xyPoint.getLeftMostPoint().x + xyPoint.getRightMostPoint().x) / 2;
                float centerY = (xyPoint.getTopMostPoint().y + xyPoint.getBottomMostPoint().y) / 2 + distance;
                canvas.drawText(xyPoint.getText(), centerX, centerY, mPaint);
            }

        }
    }

    /**
     * 获得颜色
     *
     * @param isPressed
     * @param isSelected
     * @param normalColor
     * @param pressedColor
     * @param selectedColor
     * @return
     */
    private int obtainColor(boolean isPressed, boolean isSelected, int normalColor, int pressedColor, int selectedColor) {
        if (isPressed && pressedColor != 0) {
            return pressedColor;
        }
        if (isSelected && selectedColor != 0) {
            return selectedColor;
        }
        return normalColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEventX = event.getX();
        mEventY = event.getY();
//        Log.d("chenchen",mEventX +" , " + mEventY);
        isIntercept = event.getPointerCount() == 1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = handleDownEvent(mEventX, mEventY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isIntercept) {
                    isIntercept = handleMoveEvent(mEventX, mEventY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleUpEvent(mEventX, mEventY);
                break;
        }
        mLastX = mEventX;
        mLastY = mEventY;
        return isIntercept || super.onTouchEvent(event);
    }

    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            //如果执行了长按事件，则将点击事件忽略掉
            isClickEvent = false;
            if (mOnXyViewLongClickListener != null) {
                mOnXyViewLongClickListener.onXyViewLongClick(mXyViewPosition);
            }
        }
    };

    /**
     * 处理手指触摸抬起事件
     *
     * @param eventX
     * @param eventY
     */
    private void handleUpEvent(float eventX, float eventY) {
        if (mXyViewPosition >= 0) {
            if (isClickEvent) {
                double distance = Math.sqrt(Math.abs(Math.pow(eventX - mDownX, 2) + Math.pow(eventY - mDownY, 2)));
                if (distance <= mTouchSlop) {
                    if (isClickToggleSelected) {//如果点击自动切换选中状态
                        if (isMultipleSelection) {
                            mXyPointList.get(mXyViewPosition).isSelected = !mXyPointList.get(mXyViewPosition).isSelected;
                        } else {
//                            /**
//                             * 单点选中区域可以取消选中
//                             */
//                            mXyViewSelectPosition = mXyViewSelectPosition != mXyViewPosition ? mXyViewPosition : NONE;
                            /**
                             * 选中区域自定区域
                             */
                            mXyViewSelectPosition = mXyViewPosition;
                        }
                    }
                    if (mOnXyViewClickListener != null) {
                        mOnXyViewClickListener.onXyViewClick(mXyViewPosition);
                    }
                }
                isClickEvent = false;
            }
            if (isLongClickRunnable) {
                removeCallbacks(mLongClickRunnable);
                isLongClickRunnable = false;
            }
            if (mOnChangeListener != null) {
                mOnChangeListener.onStopTrackingTouch(mXyViewPosition);
            }
        }

        mXyViewPosition = NONE;
        mEventPointIndex = NONE;
        isDragEvent = false;
        isIntercept = false;
        invalidate();
    }

    /**
     * 处理手指触摸移动事件
     *
     * @param eventX
     * @param eventY
     * @return
     */
    private boolean handleMoveEvent(float eventX, float eventY) {
        if (isClickEvent || isLongClickRunnable) {
            double distance = Math.sqrt(Math.abs(Math.pow(eventX - mDownX, 2) + Math.pow(eventY - mDownY, 2)));
            if (distance > mTouchSlop) {//当触摸点与最初按下的点的距离超过一定的距离，则认为是移动事件，即非点击或长按事件
                if (isClickEvent) {
                    isClickEvent = false;
                }
                if (isLongClickRunnable) {
                    removeCallbacks(mLongClickRunnable);
                    isLongClickRunnable = false;
                }
            }
        }

        if (Math.sqrt(Math.abs(Math.pow(eventX - mLastX, 2) + Math.pow(eventY - mLastY, 2))) > 100) {
            //如果两点之间的距离超过100则过滤掉，因为这可能是多点触控导致的触摸点坐标突变
            return false;
        }
        if (mXyViewPosition >= 0) {
            if (mEventPointIndex >= 0) {//拖动点，改变多边形当前拖动的点坐标信息
                if (isClickToggleSelected && mXyViewPosition == mXyViewSelectPosition) {
                    if (isChangeAngleEnabled) {
                        //根据触摸的点坐标更新X轴坐标信息
                        float newX = eventX;
                        float newY = eventY;
                        if (!isAllowDragOutView) {
                            if (eventX < 0) {
                                newX = 0;
                            } else if (eventX > getWidth()) {
                                newX = getWidth();
                            }
                            //根据触摸的点坐标更新Y轴坐标信息
                            if (eventY < 0) {
                                newY = 0;
                            } else if (eventY > getHeight()) {
                                newY = getHeight();
                            }
                        }

                        mXyPointList.get(mXyViewPosition).updatePoint(newX, newY, mEventPointIndex);
                        invalidate();
                        if (mOnChangeListener != null) {
                            mOnChangeListener.onChanged(mXyViewPosition, true);
                        }
                        return true;
                    }
                }

            } else if (isDragEvent && (Math.abs(mLastX - eventX) > 1 || Math.abs(mLastY - eventY) > 1)) {//拖动多边形
                if (isClickToggleSelected && mXyViewPosition == mXyViewSelectPosition) {
                    updateDragPoints(mXyPointList.get(mXyViewPosition));
                    if (mOnChangeListener != null) {
                        mOnChangeListener.onChanged(mXyViewPosition, true);
                    }
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 处理手指触摸按下事件
     *
     * @param eventX
     * @param eventY
     */
    private boolean handleDownEvent(float eventX, float eventY) {
        mDownX = mLastX = eventX;
        mDownY = mLastY = eventY;
        int size = mXyPointList.size();
        for (int i = size - 1; i >= 0; i--) {
            if (isChangeAngleEnabled) {
                mEventPointIndex = obtainCurrentPointIndex(mXyPointList.get(i).getPoints(), eventX, eventY);
                if (mEventPointIndex >= 0) {
                    mXyViewPosition = i;
                    if (mOnChangeListener != null) {
                        mOnChangeListener.onStartTrackingTouch(i);
                    }
                    return true;
                }
            }

            if (mEventPointIndex < 0) {//如果触摸点小于0，则去检测是否符合拖动事件
                isDragEvent = canDragEvent(mXyPointList.get(i).getPoints(), eventX, eventY);
                if (isDragEvent) {
                    mXyViewPosition = i;
                    if (mOnChangeListener != null) {
                        mOnChangeListener.onStartTrackingTouch(i);
                    }
                    isClickEvent = true;
                    if (mOnXyViewLongClickListener != null) {
                        postDelayed(mLongClickRunnable, mLongPressTimeout);
                        isLongClickRunnable = true;
                    }
                    return isDragEnabled;
                }
            }
        }
        return false;
    }

    /**
     * 根据触摸点坐标获得当前触摸点的索引
     *
     * @return 返回当前触摸点的索引，当触摸点附近没有符合的坐标点时，索引返回{@code -1}
     */
    private int obtainCurrentPointIndex(PointF[] point, float eventX, float eventY) {
        if (point != null && point.length > 0) {
            int size = point.length;
            for (int i = 0; i < size; i++) {
                //通过遍历触摸点坐标是否在点坐标附近，用于判定是否是触摸当前点坐标
                if (isPointRange(point[i].x, point[i].y, eventX, eventY)) {
                    return i;
                }
            }
        }

        return NONE;
    }

    /**
     * 触摸点坐标是否在点坐标附近，用于判定是否是触摸当前点坐标
     *
     * @param x
     * @param y
     * @param eventX
     * @param eventY
     * @return
     */
    private boolean isPointRange(float x, float y, float eventX, float eventY) {
        //通过触摸的eventX,eventY坐标与坐标点x，y来做对比，因为触摸没那么精准，所以这里扩大范围，允许一定的误差偏移量mAllowableOffsets，判断触摸点是否在坐标点的范围附近
        return eventX > x - mAllowableOffsets && eventX < x + mAllowableOffsets && eventY > y - mAllowableOffsets && eventY < y + mAllowableOffsets;
    }

    /**
     * 根据判断点坐标是否在区域内，来决定是否允许拖动
     *
     * @param point
     * @param eventX
     * @param eventY
     * @return
     */
    private boolean canDragEvent(PointF[] point, float eventX, float eventY) {
        int cross = 0;
        if (point != null && point.length > 0) {
            int size = point.length;
            for (int i = 0; i < size; i++) {
                PointF p1 = point[i];
                PointF p2 = point[(i + 1) % size];
                //求解 y=p.y 与 p1 p2 的交点
                if (p1.y == p2.y) // p1 p2 与 y=p0.y平行
                    continue;
                if (eventY < Math.min(p1.y, p2.y)) //交点在p1 p2延长线上
                    continue;
                if (eventY >= Math.max(p1.y, p2.y)) //交点在p1 p2延长线上
                    continue;
                //求交点的x坐标
                float x = (eventY - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;
                if (x > eventX)
                    cross++; //只统计单边交点
            }
        }

        return (cross % 2 == 1);
    }

    /**
     * 更新拖拽点信息
     */
    private synchronized void updateDragPoints(XyPoint xyPoint) {
        float moveX = mEventX - mLastX;
        float moveY = mEventY - mLastY;
        PointF[] points = xyPoint.getPoints();
        if (points != null && points.length > 0) {
            int size = points.length;
            boolean isMoveX = true;
            boolean isMoveY = true;
            if (!isAllowDragOutView) {//如果允许拖出视图以外，则进行判断是否有点超出了四个边界
                for (int i = 0; i < size; i++) {
                    //通过遍历判定X轴坐标点是否碰碰撞的左右的边界
                    if (moveX > 0 && xyPoint.getRightMostPoint().x + moveX > getWidth()) { //向右边移动
                        isMoveX = false;
                    } else if (moveX < 0 && xyPoint.getLeftMostPoint().x + moveX < 0) {//向左移动
                        isMoveX = false;
                    }
                    //通过遍历判定Y轴坐标点是否碰碰撞到上下的边界
                    if (moveY > 0 && xyPoint.getBottomMostPoint().y + moveY > getHeight()) {//向下移动
                        isMoveY = false;
                    } else if (moveY < 0 && xyPoint.getTopMostPoint().y + moveY < 0) {//向上移动
                        isMoveY = false;
                    }
                    //如果经过判定X,Y轴都无法移动，则直接返回
                    if (!(isMoveX || isMoveY)) {
                        return;
                    }
                }
            }
            //只要有一边没有碰撞到边界，就可以拖动
            if (isMoveX || isMoveY) {
                for (int i = 0; i < size; i++) {
                    if (isMoveX)
                        points[i].x += moveX;
                    if (isMoveY)
                        points[i].y += moveY;
                    xyPoint.updatePoint(points[i], i);
                }
                invalidate();
            }
        }
    }

    /**
     * 添加多边形
     *
     * @param points
     */
    public void addXyView(PointF... points) {
        addXyView(new XyPoint(points));
    }

    /**
     * 添加多边形
     *
     * @param points
     */
    public void addXyView(List<PointF> points) {
        addXyView(new XyPoint(points));
    }

    /**
     * 添加多边形
     *
     * @param xyPoint
     */
    public void addXyView(XyPoint xyPoint) {
        mXyPointList.add(xyPoint);
        invalidate();
    }

    /**
     * 添加多边形
     *
     * @param xyPoints
     */
    public void addXyView(Collection<XyPoint> xyPoints) {
        mXyPointList.addAll(xyPoints);
        invalidate();
    }

    /**
     * 移除多边形
     *
     * @param position
     */
    public void removeXyViews(int position) {
        if (position >= 0 && position < mXyPointList.size()) {
            mXyPointList.remove(position);
            if (mXyViewSelectPosition == position) {
                mXyViewSelectPosition = NONE;
            } else if (mXyViewSelectPosition > position) {
                mXyViewSelectPosition--;
            }
            invalidate();
        }
    }

    /**
     * 移除多边形
     *
     * @param xyPoint
     */
    public void removeXyViews(XyPoint xyPoint) {
        if (mXyViewSelectPosition >= 0) {//如果有选中状态的多边形，在移除多边形的同时，需同步多边形选中的位置
            int size = mXyPointList.size();
            for (int i = 0; i < size; i++) {
                if (mXyPointList.get(i).equals(xyPoint)) {
                    removeXyViews(i);
                    break;
                }
            }
        } else {
            mXyPointList.remove(xyPoint);
            invalidate();
        }
    }

    /**
     * 移除多边形
     *
     * @param xyPoints
     */
    public void removeXyViews(Collection<XyPoint> xyPoints) {
        if (mXyViewSelectPosition >= 0) {//如果有选中状态的多边形，在移除多边形的同时，需同步多边形选中的位置
            int size = mXyPointList.size();
            for (XyPoint xyPoint : xyPoints) {
                for (int i = 0; i < size; i++) {
                    if (mXyPointList.get(i).equals(xyPoint)) {
                        if (mXyViewSelectPosition == i) {
                            mXyViewSelectPosition = NONE;
                        } else if (mXyViewSelectPosition > i) {
                            mXyViewSelectPosition--;
                        }
                        break;
                    }
                }
            }
        } else {
            mXyPointList.remove(xyPoints);
        }
        invalidate();
    }

    /**
     * 清空多边形
     */
    public void clearXyView() {
        mXyPointList.clear();
        mXyViewSelectPosition = NONE;
        invalidate();
    }

    /**
     * 获取多边形
     *
     * @param position
     * @return
     */
    public XyPoint getXyView(int position) {
        if (position < mXyPointList.size()) {
            return mXyPointList.get(position);
        }
        return null;
    }

    /**
     * 获取多边形信息s
     *
     * @return
     */
    public List<XyPoint> getXyView() {
        return mXyPointList;
    }

    /**
     * 设置多边形
     *
     * @param position
     * @param xyPoint
     */
    public void setXyView(int position, XyPoint xyPoint) {
        if (position < mXyPointList.size()) {
            xyPoint.isSelected = mXyPointList.get(position).isSelected;
            mXyPointList.set(position, xyPoint);
            invalidate();
            if (mOnChangeListener != null) {
                mOnChangeListener.onChanged(position, false);
            }
        }
    }

    /**
     * 根据位置判断当前多边形是否是选中状态
     *
     * @param position
     * @return
     */
    public boolean isXyViewSelected(int position) {
        if (position >= 0 && position < mXyPointList.size()) {
            if (isMultipleSelection) {
                return mXyPointList.get(position).isSelected;
            }
            return mXyViewSelectPosition == position;
        }
        return false;
    }

    /**
     * 根据位置设置选中的多边形，这个方法主要用于单选模式
     * 同时也兼容多选模式，如果当前是多选模式，则相当于{@link #addXyViewSelected(int...)}
     *
     * @param position
     */
    public void setXyViewSelected(int position) {
        if (position >= 0 && position < mXyPointList.size()) {
            if (isMultipleSelection) {
                mXyPointList.get(position).isSelected = true;
            } else {
                mXyViewSelectPosition = position;
            }
            invalidate();
        }
    }

    /**
     * 添加多边形的选中状态，这个方法主要用于多选模式时使用
     * 同时也兼容单选模式，如果当前是单选模式，则相当于选中当前位置，当前位置取{@code positions}的第0位
     *
     * @param positions
     */
    public void addXyViewSelected(int... positions) {
        updateXyViewSelectedPosition(true, positions);
    }

    /**
     * 移除多边形的选中状态，这个方法主要用于多选模式时使用
     * 同时也兼容单选模式，如果当前是单选模式，则相当于选中当前位置，当前位置取{@code positions}的第0位
     *
     * @param positions
     */
    public void removeXyViewSelected(int... positions) {
        updateXyViewSelectedPosition(false, positions);
    }


    /**
     * 设置所有多边形的选中状态
     * 不管是单选模式还是多选模式，都可以使用此方法来改变多边形的选中状态
     *
     * @param selected 是否选中
     */
    public void setXyViewSelectedAll(boolean selected) {
        int size = mXyPointList.size();
        if (size > 0) {
            if (isMultipleSelection) {
                for (int i = 0; i < size; i++) {
                    mXyPointList.get(i).isSelected = selected;
                }
            } else {
                //如果是单选，当状态为选中时，则只能选中第0位多边形
                mXyViewSelectPosition = selected ? 0 : NONE;
            }
            invalidate();
        }

    }

    /**
     * 根据多边形的位置更新多边形是否选中状态
     *
     * @param selected  是否选中
     * @param positions
     */
    private void updateXyViewSelectedPosition(boolean selected, int... positions) {
        int length = positions.length;
        if (length > 0) {
            if (isMultipleSelection) {
                int size = mXyPointList.size();
                if (size > 0) {
                    int position;
                    for (int i = 0; i < length; i++) {
                        position = positions[i];
                        if (position >= 0 && position < size) {
                            mXyPointList.get(position).isSelected = selected;
                        }
                    }
                }
            } else if (positions[0] >= 0 && positions[0] < mXyPointList.size()) {//如果是单选，则只取第0个
                mXyViewSelectPosition = selected ? positions[0] : NONE;
            }
            invalidate();
        }
    }

    /**
     * 获取选中的位置，这个方法主要用于单选模式
     * 同时也兼容多选模式，如果当前是多选模式，则遍历返回选中的位置索引最小的
     *
     * @return
     */
    public int getXyViewSelectedPosition() {
        if (isMultipleSelection) {
            int size = mXyPointList.size();
            for (int i = 0; i < size; i++) {
                if (mXyPointList.get(i).isSelected) {
                    return i;
                }
            }
        }
        return mXyViewSelectPosition;
    }

    /**
     * 获取选中的位置集合，这个方法主要用于多选模式
     * 同时也兼容单选模式，如果当前是单选模式，如果有选中的多边形，则取返回集合的第0个
     *
     * @return
     */
    public List<Integer> getXyViewSelectPositions() {
        List<Integer> list = new ArrayList<>();
        if (isMultipleSelection) {//如果是多选模式
            int size = mXyPointList.size();
            for (int i = 0; i < size; i++) {
                if (mXyPointList.get(i).isSelected) {
                    list.add(i);
                }
            }
        } else if (mXyViewSelectPosition >= 0) {//如果是单选模式
            list.add(mXyViewSelectPosition);
        }

        return list;
    }

    /**
     * 获取画笔描边的宽度
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * 设置画笔描边的宽度
     */
    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 获取绘制点坐标时基于画笔描边的宽度倍数
     */
    public float getPointStrokeWidthMultiplier() {
        return mPointStrokeWidthMultiplier;
    }

    /**
     * 设置绘制点坐标时基于画笔描边的宽度倍数
     */
    public void setPointStrokeWidthMultiplier(float pointStrokeWidthMultiplier) {
        this.mPointStrokeWidthMultiplier = pointStrokeWidthMultiplier;
        invalidate();
    }

    /**
     * 设置多边形点的颜色
     */
    public void setPointNormalColor(int pointNormalColor) {
        this.mPointNormalColor = pointNormalColor;
        invalidate();
    }

    /**
     * 设置多边形点按下状态时的颜色
     */
    public void setPointPressedColor(int pointPressedColor) {
        this.mPointPressedColor = pointPressedColor;
    }

    /**
     * 设置多边形点选中状态时的颜色
     */
    public void setPointSelectedColor(int pointSelectedColor) {
        this.mPointPressedColor = pointSelectedColor;
        invalidate();
    }

    /**
     * 设置多边形边线的颜色
     */
    public void setLineNormalColor(int lineNormalColor) {
        this.mLineNormalColor = lineNormalColor;
        invalidate();
    }

    /**
     * 设置多边形边线按下状态的颜色
     */
    public void setLinePressedColor(int linePressedColor) {
        this.mLinePressedColor = linePressedColor;
    }

    /**
     * 设置多边形边线选中状态的颜色
     */
    public void setLineSelectedColor(int lineSelectedColor) {
        this.mLineSelectedColor = lineSelectedColor;
        invalidate();
    }

    /**
     * 设置多边形填充的颜色
     */
    public void setFillNormalColor(int fillNormalColor) {
        this.mFillNormalColor = fillNormalColor;
        invalidate();
    }

    /**
     * 设置多边形填充按下状态时的颜色
     */
    public void setFillPressedColor(int fillPressedColor) {
        this.mFillPressedColor = fillPressedColor;
    }

    /**
     * 设置多边形填充按下状态时的颜色
     */
    public void setFillSelectedColor(int fillSelectedColor) {
        this.mFillSelectedColor = fillSelectedColor;
        invalidate();
    }

    /**
     * 设置文本字体大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        invalidate();
    }

    /**
     * 设置文本的颜色
     *
     * @param textNormalColor
     */
    public void setTextNormalColor(int textNormalColor) {
        this.mTextNormalColor = textNormalColor;
        invalidate();
    }

    /**
     * 设置文本按下状态的颜色
     *
     * @param textPressedColor
     */
    public void setTextPressedColor(int textPressedColor) {
        this.mTextPressedColor = textPressedColor;
    }

    /**
     * 设置文本选中状态的颜色
     *
     * @param textSelectedColor
     */
    public void setTextSelectedColor(int textSelectedColor) {
        this.mTextSelectedColor = textSelectedColor;
        invalidate();
    }

    /**
     * 设置是否显示多边形的Text
     *
     * @param showText
     */
    public void setShowText(boolean showText) {
        isShowText = showText;
        invalidate();
    }

    /**
     * 设置多边形Text的字体是否为粗体
     *
     * @param fakeBoldText
     */
    public void setFakeBoldText(boolean fakeBoldText) {
        isFakeBoldText = fakeBoldText;
        invalidate();
    }

    /**
     * 设置允许的触点误差偏移量
     */
    public void setAllowableOffsets(float allowableOffsets) {
        this.mAllowableOffsets = allowableOffsets;
    }

    /**
     * 是否是拖动多边形事件
     */
    public boolean isDragEvent() {
        return isDragEvent;
    }

    /**
     * 是否启用拖动多边形
     */
    public boolean isDragEnabled() {
        return isDragEnabled;
    }

    /**
     * 设置是否启用拖动多边形
     */
    public void setDragEnabled(boolean dragEnabled) {
        isDragEnabled = dragEnabled;
    }

    /**
     * 是否启用多边形的各个角的角度支持可变
     */
    public boolean isChangeAngleEnabled() {
        return isChangeAngleEnabled;
    }

    /**
     * 设置是否启用多边形的各个角的角度支持可变
     */
    public void setChangeAngleEnabled(boolean changeAngleEnabled) {
        isChangeAngleEnabled = changeAngleEnabled;
    }

    /**
     * 是否是多选模式
     *
     * @return
     */
    public boolean isMultipleSelection() {
        return isMultipleSelection;
    }

    /**
     * 设置是否是多选模式
     *
     * @param multipleSelection
     */
    public void setMultipleSelection(boolean multipleSelection) {
        isMultipleSelection = multipleSelection;
    }

    /**
     * 是否点击改变选择状态
     */
    public boolean isClickToggleSelected() {
        return isClickToggleSelected;
    }

    /**
     * 设置是否点击改变选择状态
     *
     * @param clickToggleSelected
     */
    public void setClickToggleSelected(boolean clickToggleSelected) {
        isClickToggleSelected = clickToggleSelected;
    }

    /**
     * 是否允许多边形拖出视图范围
     *
     * @return
     */
    public boolean isAllowDragOutView() {
        return isAllowDragOutView;
    }

    /**
     * 设置是否允许多边形拖出视图范围
     *
     * @param allowDragOutView
     */
    public void setAllowDragOutView(boolean allowDragOutView) {
        isAllowDragOutView = allowDragOutView;
    }

    /**
     * 设置改变监听
     *
     * @param listener
     */
    public void setOnChangeListener(OnChangeListener listener) {
        this.mOnChangeListener = listener;
    }

    /**
     * 改变接口
     */
    public interface OnChangeListener {
        void onStartTrackingTouch(int position);

        void onChanged(int position, boolean fromUser);

        void onStopTrackingTouch(int position);
    }

    /**
     * 设置点击多边形监听
     *
     * @param listener
     */
    public void setOnXyViewClickListener(OnXyViewClickListener listener) {
        this.mOnXyViewClickListener = listener;
    }

    /**
     * 多边形点击监听
     */
    public interface OnXyViewClickListener {
        void onXyViewClick(int position);
    }


    /**
     * 设置长按多边形监听
     *
     * @param listener
     */
    public void setOnXyViewLongClickListener(OnXyViewLongClickListener listener) {
        this.mOnXyViewLongClickListener = listener;
    }

    /**
     * 多边形长按监听
     */
    public interface OnXyViewLongClickListener {
        void onXyViewLongClick(int position);
    }


    /**
     * 多边形对象
     */
    public static class XyPoint implements Parcelable {

        /**
         * 多边形的点坐标数组
         */
        private PointF[] mPoints;

        /**
         * 多边形的点的数量
         */
        private int size;

        /**
         * 最左边的点坐标信息
         */
        private PointF mLeftMostPoint;
        /**
         * 最上边的点坐标信息
         */
        private PointF mTopMostPoint;
        /**
         * 最右边的点坐标信息
         */
        private PointF mRightMostPoint;
        /**
         * 最下边的点坐标信息
         */
        private PointF mBottomMostPoint;

        /**
         * 最左边的点坐标信息索引
         */
        private int mLeftMostPointIndex = NONE;
        /**
         * 最上边的点坐标信息索引
         */
        private int mTopMostPointIndex = NONE;
        /**
         * 最右边的点坐标信息索引
         */
        private int mRightMostPointIndex = NONE;
        /**
         * 最下边的点坐标信息索引
         */
        private int mBottomMostPointIndex = NONE;

        /**
         * 多选时，用于标记是否选中，仅供内部使用
         */
        boolean isSelected;

        private String mText;

        /**
         * 构造
         *
         * @param left   矩形左边的X坐标
         * @param top    矩形上边的Y坐标
         * @param right  矩形右边的X坐标
         * @param bottom 矩形下边的Y坐标
         */
        public XyPoint(float left, float top, float right, float bottom) {
            PointF point0 = new PointF(left, top);
            PointF point1 = new PointF(right, top);
            PointF point2 = new PointF(right, bottom);
            PointF point3 = new PointF(left, bottom);
            setPoints(point0, point1, point2, point3);
        }

        /**
         * 构造
         *
         * @param points 多边形的点坐标
         */
        public XyPoint(PointF... points) {
            setPoints(points);
        }

        /**
         * 构造
         *
         * @param points 多边形的点坐标
         */
        public XyPoint(List<PointF> points) {
            setPoints(points);
        }

        /**
         * 获取最左边的点坐标信息
         */
        public PointF getLeftMostPoint() {
            return mLeftMostPoint;
        }

        /**
         * 获取最上边的点坐标信息
         */
        public PointF getTopMostPoint() {
            return mTopMostPoint;
        }

        /**
         * 获取最右边的点坐标信息
         */
        public PointF getRightMostPoint() {
            return mRightMostPoint;
        }

        /**
         * 获取最下边的点坐标信息
         */
        public PointF getBottomMostPoint() {
            return mBottomMostPoint;
        }

        /**
         * 获取最左边的点坐标信息索引
         */
        public int getLeftMostPointIndex() {
            return mLeftMostPointIndex;
        }

        /**
         * 获取最上边的点坐标信息索引
         */
        public int getTopMostPointIndex() {
            return mTopMostPointIndex;
        }

        /**
         * 获取最右边的点坐标信息索引
         */
        public int getRightMostPointIndex() {
            return mRightMostPointIndex;
        }

        /**
         * 获取最下边的点坐标信息索引
         */
        public int getBottomMostPointIndex() {
            return mBottomMostPointIndex;
        }

        /**
         * 更新边界点（分别是最左，最右，最上，最下的点坐标信息）
         */
        private void updateBoundaryPoints() {
            updateBoundaryPoints(mPoints);
        }

        /**
         * 更新边界点（分别是最左，最右，最上，最下的点坐标信息）
         *
         * @param points
         */
        private synchronized void updateBoundaryPoints(PointF[] points) {
            if (points != null && size > 0) {
                mLeftMostPoint = mRightMostPoint = mTopMostPoint = mBottomMostPoint = points[0];
                mLeftMostPointIndex = mRightMostPointIndex = mTopMostPointIndex = mBottomMostPointIndex = 0;
                for (int i = 1; i < size; i++) {
                    updateBoundaryPoints(points[i], i);
                }
            } else {
                mLeftMostPointIndex = mRightMostPointIndex = mTopMostPointIndex = mBottomMostPointIndex = NONE;
            }
        }

        /**
         * 更新边界点（分别是最左，最右，最上，最下的点坐标信息）
         *
         * @param point
         */
        private synchronized void updateBoundaryPoints(PointF point, int position) {
            if (mLeftMostPoint.x > point.x) {
                mLeftMostPoint = point;
                mLeftMostPointIndex = position;
            }
            if (mRightMostPoint.x < point.x) {
                mRightMostPoint = point;
                mRightMostPointIndex = position;
            }
            if (mTopMostPoint.y > point.y) {
                mTopMostPoint = point;
                mTopMostPointIndex = position;
            }
            if (mBottomMostPoint.y < point.y) {
                mBottomMostPoint = point;
                mBottomMostPointIndex = position;
            }
        }


        /**
         * 设置多边形坐标点信息，设置时，请确保至少三个点以上才能组成一个多边形
         *
         * @param points
         */
        public void setPoints(PointF... points) {
            mPoints = points;
            size = mPoints.length;
            updateBoundaryPoints();
        }

        /**
         * 设置多边形坐标点信息，设置时，请确保至少三个点以上才能组成一个多边形
         *
         * @param points
         */
        public void setPoints(List<PointF> points) {
            setPoints(points.toArray(new PointF[points.size()]));
        }

        /**
         * 获取多边形坐标点信息
         *
         * @return
         */
        public PointF[] getPoints() {
            return mPoints;
        }

        /**
         * 更新某个点的坐标信息
         *
         * @param point
         * @param position
         */
        public void updatePoint(PointF point, int position) {
            if (position < size) {
                mPoints[position] = point;
                updateBoundaryPoints();
            }
        }

        /**
         * 更新某个点的坐标信息
         *
         * @param x
         * @param y
         * @param position
         */
        public void updatePoint(float x, float y, int position) {
            if (position < size) {
                mPoints[position].x = x;
                mPoints[position].y = y;
                updateBoundaryPoints();
            }
        }

        public void setText(String text) {
            this.mText = text;
        }

        public String getText() {
            return mText;
        }


        @Override
        public String toString() {
            return "XyView{" +
                    "points=" + Arrays.toString(mPoints) +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedArray(this.mPoints, flags);
            dest.writeInt(this.size);
            dest.writeParcelable(this.mLeftMostPoint, flags);
            dest.writeParcelable(this.mTopMostPoint, flags);
            dest.writeParcelable(this.mRightMostPoint, flags);
            dest.writeParcelable(this.mBottomMostPoint, flags);
            dest.writeInt(this.mLeftMostPointIndex);
            dest.writeInt(this.mTopMostPointIndex);
            dest.writeInt(this.mRightMostPointIndex);
            dest.writeInt(this.mBottomMostPointIndex);
            dest.writeString(this.mText);
        }

        protected XyPoint(Parcel in) {
            this.mPoints = in.createTypedArray(PointF.CREATOR);
            this.size = in.readInt();
            this.mLeftMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mTopMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mRightMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mBottomMostPoint = in.readParcelable(PointF.class.getClassLoader());
            this.mLeftMostPointIndex = in.readInt();
            this.mTopMostPointIndex = in.readInt();
            this.mRightMostPointIndex = in.readInt();
            this.mBottomMostPointIndex = in.readInt();
            this.mText = in.readString();
        }

        public static final Creator<XyPoint> CREATOR = new Creator<XyPoint>() {
            @Override
            public XyPoint createFromParcel(Parcel source) {
                return new XyPoint(source);
            }

            @Override
            public XyPoint[] newArray(int size) {
                return new XyPoint[size];
            }
        };
    }

}