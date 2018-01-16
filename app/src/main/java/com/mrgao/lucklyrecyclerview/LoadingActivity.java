package com.mrgao.lucklyrecyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mrgao.lucklyrecyclerview.adapter.DataAdapter;
import com.mrgao.luckrecyclerview.LucklyRecyclerView;
import com.mrgao.luckrecyclerview.recyclerview.LRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LoadingActivity extends AppCompatActivity implements LucklyRecyclerView.OnLoadMoreListener, LucklyRecyclerView.OnRefreshListener, View.OnClickListener {
    String TAG = "LoadingActivity";
    LucklyRecyclerView mLRecyclerView;

    DataAdapter dataAdapter;
    Button mEmptyBtn, mErrorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mLRecyclerView = $(R.id.yRecyclerView);
        mEmptyBtn = $(R.id.showEmpty);
        mErrorBtn = $(R.id.showError);
        mErrorBtn.setOnClickListener(this);
        mEmptyBtn.setOnClickListener(this);

        //添加加载更多监听
        mLRecyclerView.setLoadMoreListener(this);
        //添加下拉刷新监听
        mLRecyclerView.setOnRefreshListener(this);

        mLRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //封装好了的线性分割线,也可以使用setGridDivider()；使用以及封装好的网格式布局，在使用这句话之前，请先设置好LayoutManager
        mLRecyclerView.addLinearDivider(LRecyclerView.VERTICAL_LIST);
        mLRecyclerView.setItemAnimator(new DefaultItemAnimator());


        dataAdapter = new DataAdapter();


        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            strings.add("数据" + i);
        }
        dataAdapter.addAll(strings);
        mLRecyclerView.setAdapter(dataAdapter);

        /*//这个是添加空View
        View empty = LayoutInflater.from(this).inflate(R.layout.view_empty, null, false);
        mYRecyclerView.setEmptyView(empty);*/

        //添加错误的View
        mLRecyclerView.setErrorView(R.layout.error_view);
        //添加空View
        mLRecyclerView.setEmptyView(R.layout.view_empty);
        //添加headerView
        mLRecyclerView.addHeaderView(R.layout.header_view);
        //改变下方加载进度的字体颜色
        mLRecyclerView.setLoadingTextColor(Color.BLUE);
        //改变下方加载进度条的颜色
        mLRecyclerView.setLoadingProgressColor(Color.BLUE);

        mLRecyclerView.setOnItemClickListener(new LucklyRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG,"点击--->"+position);
            }

            @Override
            public void onItemLongClick(int position) {
                Log.i(TAG,"长按--->"+position);
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

        }
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataAdapter.clearAll();
                List<String> strings = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    strings.add("数据" + i);
                }
                dataAdapter.addAll(strings);
                //在刷新之后要设置刷新不可见
                mLRecyclerView.setRefreshing(false);
            }
        }, 2000);
    }
}
