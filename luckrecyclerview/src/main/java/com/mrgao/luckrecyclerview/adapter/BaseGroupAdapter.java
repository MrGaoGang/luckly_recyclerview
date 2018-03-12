package com.mrgao.luckrecyclerview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mr.gao on 2018/3/8.
 * Package:    com.mrgao.luckrecyclerview.adapter
 * Create Date:2018/3/8
 * Project Name:LucklyRecyclerView
 * Description:
 */

public abstract class BaseGroupAdapter<A extends RecyclerView.ViewHolder, B extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //第一层
    private static final int TYPE_PARENTE = -10001;
    //第二层
    private static final int TYPE_CHILD = -10002;
    //记录parent的position
    private List<Integer> mParentPosition = new ArrayList<>();

    /**
     * 是否为头布局
     *
     * @param position
     * @return
     */
    public boolean isParentView(int position) {
        return mParentPosition.contains(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PARENTE) {
            return onCreateParentViewHolder(parent, viewType);
        } else {
            return onCreateChildViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getParentCount() != 0) {
            if (isParentView(position)) {
                onBindParentViewHolder((A) holder, mParentPosition.indexOf(position));
                return;
            } else {
                onBindChildViewHolder((B) holder, getParentIndexFromChild(position), getChildIndexForParent(position));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isParentView(position)) {
            return TYPE_PARENTE;
        } else {
            return TYPE_CHILD;
        }
    }

    /**
     * 条目的总数量=header(第一层的数量)+每一层的条目的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        mParentPosition.clear();
        int count = 0;
        int headSize = getParentCount();
        for (int i = 0; i < headSize; i++) {
            if (i != 0) {
                count++;
            }
            mParentPosition.add(new Integer(count));

            count += getChildCountForParent(i);
        }
        if (getParentCount() != 0) {
            return count + 1;
        } else {
            return 0;
        }
    }


    /**
     * 通过child的position获取到parent的index
     *
     * @param position
     * @return
     */
    public int getParentIndexFromChild(int position) {
        if (mParentPosition.size() != 0) {
            for (int i = 0; i < mParentPosition.size(); i++) {
                if (mParentPosition.get(i) > position) {
                    return i - 1;
                }
            }
            return mParentPosition.size() - 1;
        } else {
            return 0;
        }

    }

    /**
     * 获取到child在parent的index
     *
     * @param position
     * @return
     */
    public int getChildIndexForParent(int position) {
        if (mParentPosition.size() != 0) {
            int parentPosition = mParentPosition.get(getParentIndexFromChild(position));
            int childIndex = position - parentPosition - 1;
            return childIndex;
        } else {
            return 0;
        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (viewType == TYPE_PARENTE) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isParentView(position)) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p =
                        (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * 第一层的数量
     *
     * @return
     */
    public abstract int getParentCount();

    /**
     * 每一个parent下的child的数量
     *
     * @param parentPosition
     * @return
     */
    public abstract int getChildCountForParent(int parentPosition);

    public abstract A onCreateParentViewHolder(ViewGroup parent, int viewType);

    public abstract B onCreateChildViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindParentViewHolder(A holder, int position);

    /**
     * 分别是hoder,parent的位置（全局的位置）
     * child在parent中的index(不是position)
     *
     * @param holder
     * @param parentPosition
     * @param childIndexForParent
     */
    public abstract void onBindChildViewHolder(B holder, int parentPosition, int childIndexForParent);

}