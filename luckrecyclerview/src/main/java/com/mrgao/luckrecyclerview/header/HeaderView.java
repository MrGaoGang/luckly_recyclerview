package com.mrgao.luckrecyclerview.header;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrgao.luckrecyclerview.R;
import com.mrgao.luckrecyclerview.views.RefreshView;

/**
 * Created by mr.gao on 2018/3/10.
 * Package:    com.mrgao.luckrecyclerview.header
 * Create Date:2018/3/10
 * Project Name:LucklyRecyclerView
 * Description:
 */

public class HeaderView extends LinearLayout implements BaseHeaderView, RefreshView.OnProgressEndListener {
    private Context mContext;
    private RelativeLayout mContainer;
    private int mState = STATE_NORMAL;
    private int mOriginalHeight;
    private RefreshView mRefreshView;
    private TextView mTextView;
    private ImageView mImageView;

    public HeaderView(Context context) {
        this(context, null);
        mContext = context;
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @SuppressLint("WrongConstant")
    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.header_view_layout, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);
        mRefreshView = (RefreshView) mContainer.findViewById(R.id.refreshView);
        mTextView = (TextView) mContainer.findViewById(R.id.stateTv);
        mImageView = (ImageView) mContainer.findViewById(R.id.downImage);

        mRefreshView.setPointColor(getResources().getColor(R.color.main_color));
        mRefreshView.setVisibility(INVISIBLE);
        mImageView.setVisibility(VISIBLE);

        mRefreshView.setOnProgressEndListener(this);
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mOriginalHeight = getMeasuredHeight();
    }

    public View getContainer() {
        return mContainer;
    }

    @Override
    public void onAcionMove(float delta) {
        if (delta > 0) {
            setVisibleHeight((int) (delta + getVisibleHeight()));
            //如果下拉刷新的高度大于起初设置的高度，那么久改变状态
            if (getVisibleHeight() > mOriginalHeight) {
                setState(STATE_RELEASE_TO_REFRESH);
            } else {
                setState(STATE_NORMAL);
            }
        }
    }

    @Override
    public boolean releaseAction() {
        boolean refresh = false;

        if (getVisibleHeight() == 0) {
            refresh = false;
        }

        if (getVisibleHeight() >= mOriginalHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            refresh = true;
            smoothScrollTo(mOriginalHeight);
        }

        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }
        return refresh;
    }

    @Override
    public void refreshComplete() {
        setState(STATE_COMPLETE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);

    }

    @SuppressLint("WrongConstant")
    public void setState(int state) {
        //如果当前的状态和设置的状态一样就不需要在设置了
        if (mState == state) {
            return;
        }
        if (state < STATE_REFRESHING) {
            mRefreshView.setVisibility(INVISIBLE);
            mImageView.setVisibility(VISIBLE);
        } else {
            mRefreshView.setVisibility(VISIBLE);
            mImageView.setVisibility(INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                mTextView.setText(R.string.normal);
                break;
            case STATE_RELEASE_TO_REFRESH:

                mTextView.setText(R.string.refresh_to_release);
                break;

            case STATE_REFRESHING:
                smoothScrollTo(mOriginalHeight);
                mTextView.setText(R.string.refreshing);
                mRefreshView.start();

                break;

            case STATE_COMPLETE:

                break;
        }
        mState = state;
    }

    /**
     * 刷新完成回到初始状态
     */
    @SuppressLint("WrongConstant")
    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public void setDuration(int duration) {
        mRefreshView.setDuration(duration);
    }

    public int getState() {
        return mState;
    }

    @Override
    public void end() {
        mTextView.setText(R.string.refresh_done);
    }

    @Override
    public void pause() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void progress() {
        mTextView.setText(R.string.refreshing);
    }

    public void setRefreshColor(int color) {
        mRefreshView.setPointColor(color);
        mTextView.setTextColor(color);
    }
}
