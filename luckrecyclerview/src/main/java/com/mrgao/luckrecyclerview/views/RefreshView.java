package com.mrgao.luckrecyclerview.views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by mr.gao on 2018/3/9.
 * Package:    mrgao.com.mrgaoviews.views
 * Create Date:2018/3/9
 * Project Name:SelfDefineViews
 * Description:
 */

public class RefreshView extends View {
    private Paint mPaint;
    private int mHeight;
    private int mWidth;
    private int mPointColor = Color.RED;
    private int mProgress = 0;

    private int mStrokeWidth = 3;
    private ValueAnimator mValueAnimator;
    private int mRadius = 10;
    private RectF mRectF;

    //上下箭头的边界
    private int mTopVertex;
    private int mBottomVertex;
    private int mLeftVertex;
    private int mRightVertex;

    private int mTriangleHeight = 30;
    private int mTriangleWidth = 50;

    private Path mPath;

    private OnProgressEndListener mOnProgressEndListener;
    private int mDuration = 2000;


    public RefreshView(Context context) {
        this(context, null);
    }


    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mPointColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mRectF = new RectF();
        mPath = new Path();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = 80;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = Math.min(heightSize, mWidth);
        } else {
            mHeight = mWidth;//保证宽高一致
        }
        mRadius = mWidth / 2 - mStrokeWidth;
        mTopVertex = mStrokeWidth + mHeight / 10;
        mBottomVertex = mHeight - mStrokeWidth - mHeight / 10;

        mLeftVertex = mWidth / 3;
        mRightVertex = mWidth - mStrokeWidth - mHeight / 10;

        mTriangleWidth = mWidth / 3;
        mTriangleHeight = mHeight / 4;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mPointColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        drawCircle(canvas);
    }


    private void drawCircle(Canvas canvas) {

        if (mProgress != 100) {
            mRectF.set(mStrokeWidth, mStrokeWidth, mWidth - mStrokeWidth, mHeight - mStrokeWidth);
            canvas.drawArc(mRectF, 270, (int) (mProgress * 3.6), false, mPaint);
            if (mProgress >= 50) {
                drawDownArrow(canvas);
            }

            if (mOnProgressEndListener != null) {
                mOnProgressEndListener.progress();
            }

        } else {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);
            drawComplete(canvas);
            if (mOnProgressEndListener != null) {
                mOnProgressEndListener.end();
            }
        }
    }


    /**
     * 绘制向下的箭头
     *
     * @param canvas
     */
    private void drawDownArrow(Canvas canvas) {
        mPath.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mStrokeWidth - 1);
        canvas.drawLine(mWidth / 2, mTopVertex, mWidth / 2, mBottomVertex - mTriangleHeight, mPaint);
        mPath.moveTo(mWidth / 2, mBottomVertex);
        mPath.lineTo(mWidth / 2 - mTriangleWidth / 2, mBottomVertex - mTriangleHeight);
        mPath.lineTo(mWidth / 2 + mTriangleWidth / 2, mBottomVertex - mTriangleHeight);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制向下的箭头
     *
     * @param canvas
     */
    private void drawComplete(Canvas canvas) {
        mPath.reset();
        mPaint.setStyle(Paint.Style.STROKE);

        mPath.moveTo(mLeftVertex, mHeight / 2);
        mPath.lineTo(mWidth / 2, mHeight * 6 / 9);
        mPath.lineTo(mWidth * 3 / 4, mHeight / 4);

        canvas.drawPath(mPath, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 更新进度的
     */
    public void start() {

        if (mValueAnimator != null) {
            restart();
        }

        mValueAnimator = ValueAnimator.ofInt(0, 100);

        mValueAnimator.setStartDelay(100);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mProgress = (int) valueAnimator.getAnimatedValue();
                postInvalidate();

            }
        });


        mValueAnimator.start();
    }


    public void setStrokeWidth(int strokeWidth) {
        if (mStrokeWidth > 1) {
            mStrokeWidth = strokeWidth;
        }

    }


    public void pause() {
        mValueAnimator.pause();
        if (mOnProgressEndListener != null) {
            mOnProgressEndListener.pause();
        }
    }


    public void restart() {
        mValueAnimator.resume();
        if (mOnProgressEndListener != null) {
            mOnProgressEndListener.restart();
        }
    }


    public void setOnProgressEndListener(OnProgressEndListener onProgressEndListener) {
        mOnProgressEndListener = onProgressEndListener;
    }

    @SuppressLint("WrongConstant")
    public void setDuration(int duration) {
        mDuration = duration;
        if (getVisibility() == View.VISIBLE) {
            postInvalidate();
        }


    }

    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
        mPaint.setColor(mPointColor);
    }

    public interface OnProgressEndListener {
        void end();

        void pause();

        void restart();

        void progress();
    }
}
