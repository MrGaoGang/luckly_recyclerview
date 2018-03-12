package com.mrgao.lucklyrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrgao.lucklyrecyclerview.R;
import com.mrgao.lucklyrecyclerview.beans.AccoutBean;
import com.mrgao.lucklyrecyclerview.beans.GroupBean;
import com.mrgao.luckrecyclerview.adapter.BaseGroupAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mr.gao on 2018/3/8.
 * Package:    com.mrgao.lucklyrecyclerview.adapter
 * Create Date:2018/3/8
 * Project Name:LucklyRecyclerView
 * Description:
 */

public class GroupAdapter extends BaseGroupAdapter<GroupAdapter.GroupViewHolder, GroupAdapter.ChildViewHolder> {
    private Context context;
    private List<GroupBean> mContent;
    //用于记录当前组是隐藏还是显示
    private SparseBooleanArray mBooleanMap;

    public GroupAdapter(Context context) {
        this.context = context;
        this.mContent = new ArrayList<>();

        mBooleanMap = new SparseBooleanArray();
    }

    @Override
    public int getParentCount() {
        return mContent.size();
    }

    @Override
    public int getChildCountForParent(int parentPosition) {
        if (getParentCount() == 0) {
            return 0;
        }
        int count = mContent.get(parentPosition).childList.size();

        if (!mBooleanMap.get(parentPosition)) {
            count = 0;
        }
        return count;
    }

    @Override
    public GroupViewHolder onCreateParentViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(context).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.child_item, parent, false));
    }

    @Override
    public void onBindParentViewHolder(GroupViewHolder holder, int position) {
        GroupBean groupBean = mContent.get(position);


        //传递过来的日期格式为:2018-3
        if (groupBean != null) {
            String[] date = groupBean.groupTime.split("-");
            holder.month.setText(date[1] + "月");
            holder.year.setText(date[0] + "年");

            ImageView imageView = holder.image;

            boolean isOpen = mBooleanMap.get(position);
            if (isOpen) {
                imageView.setImageResource(R.mipmap.to_top);
            } else {
                imageView.setImageResource(R.mipmap.to_bottom);
            }

            holder.totalInput.setText("流入: " + groupBean.totalInput);
            holder.totalOutput.setText("流入: " + groupBean.totalOut);

            float total = Float.parseFloat(groupBean.totalInput) - Float.parseFloat(groupBean.totalOut);
            if (total > 0f) {
                holder.total.setText("+" + total);
            } else {
                holder.total.setText("-" + total);
            }

            holder.itemView.setTag(position);

        }
    }

    /**
     * 显示或者隐藏child
     *
     * @param view
     */
    public void showChild(View view) {
        int position = (int) view.getTag();
        boolean isOpen = mBooleanMap.get(position);
        mBooleanMap.put(position, !isOpen);
        notifyDataSetChanged();
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, int parentPosition, int childIndexForParent) {
        final AccoutBean accoutBean = mContent.get(parentPosition).childList.get(childIndexForParent);
        holder.image.setImageResource(accoutBean.getImageResourceId());
        holder.money.setText(accoutBean.getMoney());
        holder.time.setText(accoutBean.getTime());
        holder.type.setText(accoutBean.getMoneyType());


    }

    public void addAll(List<GroupBean> list) {
        mContent.clear();
        mContent.addAll(list);
        notifyDataSetChanged();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.year)
        TextView year;
        @BindView(R.id.totalInput)
        TextView totalInput;
        @BindView(R.id.totalOutput)
        TextView totalOutput;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.total)
        TextView total;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.money)
        TextView money;

        @BindView(R.id.typeTv)
        TextView type;

        public ChildViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
