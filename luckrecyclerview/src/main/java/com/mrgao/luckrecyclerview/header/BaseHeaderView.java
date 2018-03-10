package com.mrgao.luckrecyclerview.header;

/**
 * Created by mr.gao on 2018/3/10.
 * Package:    com.mrgao.luckrecyclerview.header
 * Create Date:2018/3/10
 * Project Name:LucklyRecyclerView
 * Description:
 */

public interface BaseHeaderView {
    int STATE_NORMAL = 0;//正常情况
    int STATE_RELEASE_TO_REFRESH = 1;//下拉但是位松开
    int STATE_REFRESHING = 2;//下拉松开，开始刷新
    int STATE_COMPLETE = 3;//刷新完成

    void onAcionMove(float delta);

    //返回的Boolean值 表示是否可以刷新
    boolean releaseAction();

    void refreshComplete();

}
