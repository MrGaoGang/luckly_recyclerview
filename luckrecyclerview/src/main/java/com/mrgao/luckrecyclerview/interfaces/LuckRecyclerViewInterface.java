package com.mrgao.luckrecyclerview.interfaces;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mrgao.luckrecyclerview.LucklyRecyclerView;

import java.util.List;



/**
 * Created by mr.gao on 2018/1/16.
 * Package:    mrgao.com.recyclerviewtext.loadMore
 * Create Date:2018/1/16
 * Project Name:RecyclerViewText
 * Description:
 */

public interface LuckRecyclerViewInterface {
    void setLayoutManager(RecyclerView.LayoutManager layoutManager);

    void setItemAnimator(RecyclerView.ItemAnimator animator);

    RecyclerView getOriginalRecyclerView();

    void  setLoadMoreListener(LucklyRecyclerView.OnLoadMoreListener loadMore);

    void setOnScrollCallback(LucklyRecyclerView.OnScrollCallback onScrollCallback);

    void setAdapter(RecyclerView.Adapter adapter);

    int getHeaderViewCount();

    List<View> getHeaderViews();

    void addHeaderView(View view);

    void addHeaderView(int id);

    void setEmptyView(View view);

    void setEmptyView(int id);

    void setErrorView(View view);

    void setErrorView(int id);

    void showError(boolean errorShow);

    View getEmptyView();

    View getErrorView();

    int getLoadingState();

    void setLoading();

    void setLoading(String loading);

    void setLoadingComplete();

    void setLoadingComplete(String loading);

    void setLoadingNoMore();

    void setLoadingNoMore(String loading);

    void addGridDivider(int color, int dividerHeight);

    void addGridDivider();

    void addLinearDivider(int oritation);

    void addLinearDivider(int oritation, int color, int lineWidth);

    void setLoadingTextColor(int color);

    void setLoadingProgressColor(int progressColor);

    void setFooterVisiable(boolean visiable);

    void setOnItemClickListener(LucklyRecyclerView.OnItemClickListener onItemClickListener);

    void setOnItemHeaderClickListener(LucklyRecyclerView.OnItemHeaderClickListener onItemHeaderClickListener);

    void setOnRefreshListener(LucklyRecyclerView.OnRefreshListener onRefreshListener);



    void setRefreshEnable(boolean enable);

    void setRefreshComplete();

    void setDuration(int duration);

    void setRefreshColor(int color);

    void setOnClickEmptyOrErrorToRefresh(boolean emptyToRefresh);


    void setRefreshBackground(Drawable drawable);

    void setRefreshBackgroundColor(int color);

    void setFooterBackground(Drawable drawable);

    void setFooterBackgroundColor(int color);

    void setRefreshBackgroundResource(int resource);

    void setFooterBackgroundResource(int resource);

    int getOffsetCount();
}
