package com.mrgao.luckrecyclerview.interfaces;

import android.support.v4.widget.SwipeRefreshLayout;

import com.mrgao.luckrecyclerview.LucklyRecyclerView;


/**
 * Created by mr.gao on 2018/1/16.
 * Package:    mrgao.com.recyclerviewtext.loadMore.views
 * Create Date:2018/1/16
 * Project Name:RecyclerViewText
 * Description:
 */

public interface LucklyRecyclerSwpieInterface {
    boolean isRefreshing();

    void setColorSchemeResources(int... colorResIds);

    void setColorSchemeColors(int... colors);

    void setRefreshing(boolean refreshing);

    void setSwipeRefreshLayoutEnable(boolean enable);

    SwipeRefreshLayout getSwipeRefreshLayout();

    void setOnRefreshListener(LucklyRecyclerView.OnRefreshListener listener);

    void setProgressViewOffset(boolean scale, int start, int end);

    void setProgressViewEndTarget(boolean scale, int end);

    void setProgressBackgroundColor(int colorRes);
}
