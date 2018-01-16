package com.mrgao.lucklyrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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


        mLRecyclerView.setLoadMoreListener(this);
        mLRecyclerView.setOnRefreshListener(this);
        mLRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //封装好了的线性分割线
        mLRecyclerView.addLinearDivider(LRecyclerView.VERTICAL_LIST);
        mLRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加错误ss的View
        mLRecyclerView.setErrorView(R.layout.error_view);

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

        mLRecyclerView.setEmptyView(R.layout.view_empty);

        mLRecyclerView.addHeaderView(R.layout.header_view);
    }


    public <T extends View> T $(int id) {
        return (T) findViewById(id);
    }


    @Override
    public void onLoadMore() {
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
                    mLRecyclerView.setLoadingComplete();
                } else {

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
                mLRecyclerView.setRefreshing(false);
            }
        }, 2000);
    }
}
