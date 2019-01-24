package com.mrgao.luckrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mrgao.luckrecyclerview.adapter.BaseGroupAdapter;
import com.mrgao.luckrecyclerview.interfaces.LuckRecyclerViewInterface;
import com.mrgao.luckrecyclerview.recyclerview.LRecyclerView;

import java.util.List;


/**
 * Created by mr.gao on 2018/1/16.
 * Package:    mrgao.com.recyclerviewtext.loadMore.recyclerview
 * Create Date:2018/1/16
 * Project Name:RecyclerViewText
 * Description:
 */

public class LucklyRecyclerView extends LinearLayout implements LuckRecyclerViewInterface {

    private LRecyclerView mLRecyclerView;
    public static final int NORMAL = 0;//正常
    public static final int GROUP = 1;//分组情况
    private int mRecyclerViewType = NORMAL;

    public LucklyRecyclerView(Context context) {
        super(context);
        initView();
    }

    public LucklyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LucklyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setOrientation(VERTICAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.luck_recyclerview_layout, this, false);
        this.addView(view);


        mLRecyclerView = (LRecyclerView) view.findViewById(R.id.lrecyclerview);
    }


    @Override
    public void setOnRefreshListener(final OnRefreshListener listener) {
        if (listener != null) {
            mLRecyclerView.setRefreshEnable(true);
            //包装了一下，是为了将LRecyvlerView还原
            mLRecyclerView.reset();
            mLRecyclerView.setOnRefreshListener(listener);
        }
    }


    @Override
    public void setRefreshEnable(boolean enable) {
        mLRecyclerView.setRefreshEnable(true);
    }

    @Override
    public void setRefreshComplete() {
        mLRecyclerView.setRefreshComplete();
    }

    @Override
    public void setDuration(int duration) {
        mLRecyclerView.setDuration(duration);
    }

    @Override
    public void setRefreshColor(int color) {
        mLRecyclerView.setRefreshColor(color);
    }


    @Override
    public void setOnClickEmptyOrErrorToRefresh(boolean emptyToRefresh) {
        mLRecyclerView.setOnClickEmptyOrErrorToRefresh(emptyToRefresh);
    }

    @Override
    public void setRefreshBackground(Drawable drawable) {
        mLRecyclerView.setRefreshBackground(drawable);
    }

    @Override
    public void setRefreshBackgroundColor(int color) {
        mLRecyclerView.setRefreshBackgroundColor(color);
    }

    @Override
    public void setFooterBackground(Drawable drawable) {
        mLRecyclerView.setFooterBackground(drawable);
    }

    @Override
    public void setFooterBackgroundColor(int color) {
        mLRecyclerView.setFooterBackgroundColor(color);
    }

    @Override
    public void setRefreshBackgroundResource(int resource) {
        mLRecyclerView.setRefreshBackgroundResource(resource);
    }

    @Override
    public void setFooterBackgroundResource(int resource) {
        mLRecyclerView.setFooterBackgroundResource(resource);

    }

    @Override
    public int getOffsetCount() {
        return mLRecyclerView.getOffsetCount();
    }

    /*下面是关于RecyckerView的*/
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mLRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mLRecyclerView.setItemAnimator(animator);
    }

    @Override
    public RecyclerView getOriginalRecyclerView() {
        return mLRecyclerView.getOriginalRecyclerView();
    }

    @Override
    public void setLoadMoreListener(OnLoadMoreListener loadMore) {
        mLRecyclerView.setLoadMoreListener(loadMore);
    }

    @Override
    public void setOnScrollCallback(OnScrollCallback onScrollCallback) {
        mLRecyclerView.setOnScrollCallback(onScrollCallback);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {

        if (mRecyclerViewType == NORMAL) {
            if (mLRecyclerView.getAdapter() == null) {
                mLRecyclerView.setAdapter(adapter);
            }

        } else if (mRecyclerViewType == GROUP) {
            if (adapter instanceof BaseGroupAdapter) {
                if (mLRecyclerView.getAdapter() == null) {
                    mLRecyclerView.setAdapter(adapter);
                }
            }
        }

    }

    @Override
    public int getHeaderViewCount() {
        return mLRecyclerView.getHeaderViewCount();
    }

    @Override
    public List<View> getHeaderViews() {
        return mLRecyclerView.getHeaderViews();
    }

    @Override
    public void addHeaderView(View view) {
        mLRecyclerView.addHeaderView(view);
    }

    @Override
    public void addHeaderView(int id) {
        mLRecyclerView.addHeaderView(id);
    }

    @Override
    public void setEmptyView(View view) {
        mLRecyclerView.setEmptyView(view);
    }

    @Override
    public void setEmptyView(int id) {
        mLRecyclerView.setEmptyView(id);
    }

    @Override
    public void setErrorView(View view) {
        mLRecyclerView.setErrorView(view);
    }

    @Override
    public void setErrorView(int id) {
        mLRecyclerView.setErrorView(id);
    }

    @Override
    public void showError(boolean errorShow) {
        mLRecyclerView.showError(errorShow);
    }

    @Override
    public View getEmptyView() {
        return mLRecyclerView.getEmptyView();
    }

    @Override
    public View getErrorView() {
        return mLRecyclerView.getErrorView();
    }

    @Override
    public int getLoadingState() {
        return mLRecyclerView.getLoadingState();
    }

    @Override
    public void setLoading() {
        mLRecyclerView.setLoading();
    }

    @Override
    public void setLoading(String loading) {
        mLRecyclerView.setLoading(loading);
    }

    @Override
    public void setLoadingComplete() {
        mLRecyclerView.setLoadingComplete();
    }

    @Override
    public void setLoadingComplete(String loading) {
        mLRecyclerView.setLoadingComplete(loading);
    }

    @Override
    public void setLoadingNoMore() {
        mLRecyclerView.setLoadingNoMore();
    }

    @Override
    public void setLoadingNoMore(String loading) {
        mLRecyclerView.setLoadingNoMore(loading);
    }

    @Override
    public void addGridDivider(int color, int dividerHeight) {
        mLRecyclerView.addGridDivider(color, dividerHeight);
    }

    @Override
    public void setFooterVisiable(boolean visiable) {
        mLRecyclerView.setFooterVisiable(visiable);

    }

    @Override
    public void addGridDivider() {
        mLRecyclerView.addGridDivider();
    }

    @Override
    public void addLinearDivider(int oritation) {
        mLRecyclerView.addLinearDivider(oritation);
    }

    @Override
    public void addLinearDivider(int oritation, int color, int lineWidth) {
        mLRecyclerView.addLinearDivider(oritation, color, lineWidth);
    }

    @Override
    public void setLoadingTextColor(int color) {
        mLRecyclerView.setLoadingTextColor(color);
    }

    @Override
    public void setLoadingProgressColor(int progressColor) {
        mLRecyclerView.setLoadingProgressColor(progressColor);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mLRecyclerView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setOnItemHeaderClickListener(OnItemHeaderClickListener onItemHeaderClickListener) {
        mLRecyclerView.setOnItemHeaderClickListener(onItemHeaderClickListener);
    }


    /**
     * 设置显示的类型：正常或者分组
     *
     * @param recyclerViewType
     */
    public void setRecyclerViewType(int recyclerViewType) {
        mRecyclerViewType = recyclerViewType;
    }

    /**
     * 设置加载更多的接口回调
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * 滑动监听回调
     */
    public interface OnScrollCallback {

        void onStateChanged(LRecyclerView recycler, int state);

        void onScrollUp(LRecyclerView recycler, int dy);

        void onScrollDown(LRecyclerView recycler, int dy);
    }

    /**
     * 点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(View rootView, int position);

        void onItemLongClick(View rootView, int position);

    }

    public interface  OnItemHeaderClickListener{
        void onHeaderClick(View rootView,int position);
    }

    public interface OnErrorEmptyClickListener {
        void onErrorClick(View errorView);

        void onEmptyClick(View emptyView);
    }
}
