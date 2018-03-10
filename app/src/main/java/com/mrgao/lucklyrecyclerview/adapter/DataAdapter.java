package com.mrgao.lucklyrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrgao.lucklyrecyclerview.R;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by mr.gao on 2018/1/14.
 * Package:    mrgao.com.recyclerviewtext.loadMore.adapter
 * Create Date:2018/1/14
 * Project Name:RecyclerViewText
 * Description:
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ItemHolder> {

    List<String> mStringList;
    private OnItemLongClickListener listener;


    public DataAdapter() {
        mStringList=new ArrayList<>();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
        if (mStringList.size() != 0) {
            holder.mTextView.setText(mStringList.get(position));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                listener.onLongClick(view,position);
                return false;
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mStringList.size();
    }

    public void addAll(List<String> list){
        mStringList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mStringList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemLongClickListener {
        void onLongClick(View view, int position);
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            mTextView = getView(itemView, R.id.item);
        }
    }

    private <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

}
