package com.mrgao.luckrecyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static com.mrgao.luckrecyclerview.recyclerview.LRecyclerView.HORIZONTAL_LIST;
import static com.mrgao.luckrecyclerview.recyclerview.LRecyclerView.VERTICAL_LIST;


/**
 * Created by mr.gao on 2018/1/14.
 * Package:    mrgao.com.recyclerviewtext.loadMore.views
 * Create Date:2018/1/14
 * Project Name:RecyclerViewText
 * Description:线性分割线
 */

public class LRecyclerViewItemDivider extends RecyclerView.ItemDecoration {

    private final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };


    private Drawable mDivider;
    private int mOrientation;
    private int mLineHeight;
    private Paint mPaint;

    /**
     * 使用系统默认的分割线的颜色
     *
     * @param context
     * @param orientation
     */
    public LRecyclerViewItemDivider(Context context, int orientation) {
        TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        mDivider = typedArray.getDrawable(0);
        typedArray.recycle();

        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("IllegalArgument in LucklyRecyclerView");
        }

        mOrientation = orientation;
    }

    /**
     * 自定义分割线的颜色和高
     *
     * @param orientation
     * @param color
     * @param lineHeight
     */
    public LRecyclerViewItemDivider(int orientation, int color, int lineHeight) {
        mOrientation = orientation;
        mPaint = new Paint();
        mPaint.setColor(color);
        mLineHeight = lineHeight;
        mPaint.setStrokeWidth(lineHeight);
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == HORIZONTAL_LIST) {
            drawHo(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    /**
     * @param canvas
     * @param recyclerView
     */
    private void drawVertical(Canvas canvas, RecyclerView recyclerView) {
        int left = recyclerView.getPaddingLeft();
        int right = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int count = recyclerView.getChildCount();
        for (int i = 0; i < count - 2; i++) {
            View view = recyclerView.getChildAt(i);
            //获取到每一个item的margin的值

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view
                    .getLayoutParams();

            int top = view.getBottom() + params.bottomMargin;

            if (mDivider != null) {
                int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            } else if (mPaint != null) {
                int bottom = top + mLineHeight;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }

        }
    }


    /**
     * @param canvas
     * @param recyclerView
     */
    private void drawHo(Canvas canvas, RecyclerView recyclerView) {
        int top = recyclerView.getPaddingTop();
        int bottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
        int count = recyclerView.getChildCount();
        for (int i = 0; i < count; i++) {//是减1是因为最后的footer
            View child = recyclerView.getChildAt(i);
            //获取到每一个item的margin的值
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;

            if (mDivider != null) {
                int right = left + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            } else if (mPaint != null) {
                int right = left + mLineHeight;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }

        }
    }

    /**
     * 调用getItemOffset方法来计算每个Item的Decoration合适的尺寸
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (mDivider != null) {
            if (mOrientation == HORIZONTAL_LIST) {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        } else {
            if (mOrientation == HORIZONTAL_LIST) {
                outRect.set(0, 0, mLineHeight, 0);
            } else {
                outRect.set(0, 0, 0, mLineHeight);
            }
        }

    }
}