package com.mrgao.lucklyrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mrgao.lucklyrecyclerview.adapter.DataAdapter;
import com.mrgao.luckrecyclerview.LucklyRecyclerView;
import com.mrgao.luckrecyclerview.recyclerview.LRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LoadingActivity extends AppCompatActivity implements LucklyRecyclerView.OnLoadMoreListener, LucklyRecyclerView.OnRefreshListener, View.OnClickListener {
    String TAG = "LoadingActivity";
    LucklyRecyclerView mLRecyclerView;

    DataAdapter dataAdapter;
    Button mEmptyBtn, mErrorBtn, group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mLRecyclerView = $(R.id.yRecyclerView);
        mEmptyBtn = $(R.id.showEmpty);
        mErrorBtn = $(R.id.showError);
        group = $(R.id.group);
        mErrorBtn.setOnClickListener(this);
        mEmptyBtn.setOnClickListener(this);
        group.setOnClickListener(this);

        //添加加载更多监听
        mLRecyclerView.setLoadMoreListener(this);
        //添加下拉刷新监听
        mLRecyclerView.setOnRefreshListener(this);

        mLRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //封装好了的线性分割线,也可以使用setGridDivider()；使用以及封装好的网格式布局，在使用这句话之前，请先设置好LayoutManager
        mLRecyclerView.addLinearDivider(LRecyclerView.VERTICAL_LIST);
       // mLRecyclerView.addGridDivider();
        mLRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mLRecyclerView.setRefreshColor(getResources().getColor(R.color.colorAccent));

        dataAdapter = new DataAdapter();


        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            strings.add("数据" + i);
        }
        dataAdapter.addAll(strings);
        mLRecyclerView.setAdapter(dataAdapter);

        /*//这个是添加空View
        View empty = LayoutInflater.from(this).inflate(R.layout.view_empty, null, false);
        mYRecyclerView.setEmptyView(empty);*/

        //设置下拉刷新的时长
        mLRecyclerView.setDuration(4000);
        //添加错误的View
        mLRecyclerView.setErrorView(R.layout.error_view);
        //添加空View
        mLRecyclerView.setEmptyView(R.layout.view_empty);
        //添加headerView
        View head = LayoutInflater.from(this).inflate(R.layout.header_view, mLRecyclerView, false);
        mLRecyclerView.addHeaderView(head);
        //mLRecyclerView.addHeaderView(R.layout.header_view);
        //改变下方加载进度的字体颜色,注意在设置颜色的时候有，mainColor,要在设置了Adapter之后使用
        mLRecyclerView.setLoadingTextColor(getResources().getColor(R.color.colorAccent));
        //改变下方加载进度条的颜色
        mLRecyclerView.setLoadingProgressColor(getResources().getColor(R.color.colorAccent));
        //设置点击事件，注意此处返回的position是包括了headerView和下拉加载的视图的
        mLRecyclerView.setOnItemClickListener(new LucklyRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "点击--->" + position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.item);
                textView.setText("长按"+position);
                Log.i(TAG, "长按--->" + position);
            }
        });


    }


    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }


    @Override
    public void onLoadMore() {
        //设置处于正在加载状态
        mLRecyclerView.setLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = dataAdapter.getItemCount() + 1;
                if (count < 50) {
                    List<String> strings = new ArrayList<>();
                    for (int i = count - 1; i < 5 + count; i++) {
                        strings.add("数据" + i);
                    }
                    dataAdapter.addAll(strings);
                    //设置数据已经加载完成状态
                    mLRecyclerView.setLoadingComplete();
                } else {
                    //设置没有更多数据状态，可以自定义现实的文字，上述的两个状态也都可以自定义文字
                    mLRecyclerView.setLoadingNoMore("唉呀妈呀，没数据咯");
                }


            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showEmpty:
                dataAdapter.clearAll();
                break;

            case R.id.showError:
                mLRecyclerView.showError(true);
                break;
            case R.id.group:
                startActivity(new Intent(LoadingActivity.this, GroupActivity.class));
                break;

        }
    }


    @Override
    public void onRefresh() {
       mLRecyclerView.setRefreshEnable(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataAdapter.clearAll();
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    strings.add("数据" + i);
                }
                dataAdapter.addAll(strings);

                mLRecyclerView.setRefreshComplete();
            }
        }, 6000);
    }
}
