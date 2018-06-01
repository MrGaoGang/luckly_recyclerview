package com.mrgao.luckrecyclerview.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mrgao.luckrecyclerview.beans.Circle;


/**
 * Created by mr.gao on 2018/1/9.
 * Package:    mrgao.com.mrgaoviews.views
 * Create Date:2018/1/9
 * Project Name:SelfDefineViews
 * Description:两条鱼循环旋转
 */

public class TwoFishView extends View {
    private Circle[] circles;
    private int numberOfCircle = 10;
    private float[] rotates;


    private Paint mPaint;
    private int mPointColor = Color.RED;
    private int mHeight;
    private int mWidth;
    ValueAnimator fadeAnimator;

    public TwoFishView(Context context) {
        this(context, null);
    }

    public TwoFishView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoFishView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mPointColor);
        mPaint.setAntiAlias(true);
        rotates = new float[numberOfCircle];
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
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int size = Math.min(w, h);
        initCircle(size);
        startAnimation();
    }

    /**
     * 初始化圆和其数据
     *
     * @param size
     */
    private void initCircle(int size) {

        final float circleRadius = size / numberOfCircle;
        circles = new Circle[numberOfCircle];
        for (int i = 0; i < numberOfCircle / 2; i++) {
            circles[i] = new Circle();
            circles[i].setCenter(mWidth / 2, circleRadius);
            circles[i].setColor(mPointColor);
            circles[i].setRadius(circleRadius - circleRadius * i / 6);
        }

        for (int i = numberOfCircle / 2; i < numberOfCircle; i++) {
            circles[i] = new Circle();
            circles[i].setCenter(mWidth / 2, size - circleRadius);
            circles[i].setColor(mPointColor);
            circles[i].setRadius(circleRadius - circleRadius * (i - 5) / 6);
        }
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        for (int i = 0; i < numberOfCircle; i++) {
            final int index = i;
            fadeAnimator = ValueAnimator.ofFloat(0, 360);
            fadeAnimator.setRepeatCount(ValueAnimator.INFINITE);
            fadeAnimator.setDuration(1700);
            fadeAnimator.setStartDelay((index >= 5 ? index - 5 : index) * 100);
            fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rotates[index] = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            fadeAnimator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mPointColor);
        for (int i = 0; i < numberOfCircle; i++) {
            canvas.save();
            canvas.rotate(rotates[i], mWidth / 2, mHeight / 2);
            canvas.drawCircle(circles[i].getCenter().x, circles[i].getCenter().y,
                    circles[i].getRadius(), mPaint);
            canvas.restore();
        }
    }


    public void setProgressColor(int progressColor) {
        mPointColor = progressColor;
        postInvalidate();
    }

}

