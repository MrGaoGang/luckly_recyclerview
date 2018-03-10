package com.mrgao.lucklyrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.mrgao.lucklyrecyclerview.adapter.GroupAdapter;
import com.mrgao.lucklyrecyclerview.beans.AccoutBean;
import com.mrgao.lucklyrecyclerview.beans.GroupBean;
import com.mrgao.luckrecyclerview.LucklyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupActivity extends AppCompatActivity {

    @BindView(R.id.luckRecyclerView)
    LucklyRecyclerView luckRecyclerView;
    GroupAdapter mGroupAdapter;
    Unbinder mUnbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        mUnbinder=ButterKnife.bind(this);
        initView();
        initData();
        listener();
    }

    private void initView(){
        luckRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        luckRecyclerView.setItemAnimator(new DefaultItemAnimator());

        luckRecyclerView.setRecyclerViewType(LucklyRecyclerView.GROUP);
        luckRecyclerView.addLinearDivider(LucklyRecyclerView.VERTICAL,getResources().getColor(R.color.grey_200),1);
        mGroupAdapter=new GroupAdapter(this);
        luckRecyclerView.setAdapter(mGroupAdapter);

    }

    private void initData(){
        GroupBean groupBean=new GroupBean();
        groupBean.groupId="0";
        groupBean.groupTime="2018-1";
        groupBean.totalInput="2000.00";
        groupBean.totalOut="1000.00";

        AccoutBean accoutBean=new AccoutBean();
        accoutBean.setImageResourceId(R.mipmap.b_jiangjin);
        accoutBean.setMoney("2000.00");
        accoutBean.setTime("2018-1-1");
        accoutBean.setAccountType("储蓄卡");
        accoutBean.setMoneyType("现金");
        accoutBean.setDescript("给别人借的钱");
        accoutBean.setPeople("自己");

        AccoutBean accoutBean1=new AccoutBean();
        accoutBean1.setImageResourceId(R.mipmap.b_jiangjin);
        accoutBean1.setMoney("-1000.00");
        accoutBean1.setTime("2018-1-1");
        accoutBean1.setDescript("今天去武汉的飞机票");
        accoutBean1.setPeople("母亲");
        accoutBean.setAccountType("储蓄卡");
        accoutBean1.setMoneyType("交通");

        List<AccoutBean> list=new ArrayList<>();
        groupBean.childList=list;
        groupBean.childList.add(accoutBean);
        groupBean.childList.add(accoutBean1);

        GroupBean groupBean1=new GroupBean();
        groupBean1.groupId="1";
        groupBean1.groupTime="2018-2";
        groupBean1.totalInput="2000.00";
        groupBean1.totalOut="1000.00";

        List<AccoutBean> list2=new ArrayList<>();
        groupBean1.childList=list2;
        groupBean1.childList.add(accoutBean);
        groupBean1.childList.add(accoutBean1);

        List<GroupBean> list1=new ArrayList<>();
        list1.add(groupBean);
        list1.add(groupBean1);
        list1.add(groupBean);
        list1.add(groupBean1);
        list1.add(groupBean);
        list1.add(groupBean1);
        list1.add(groupBean);
        list1.add(groupBean1);

        mGroupAdapter.addAll(list1);
    }

    private void listener(){
        luckRecyclerView.setOnItemClickListener(new LucklyRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View rootView, int position) {
                if (mGroupAdapter.isParentView(position)){
                    mGroupAdapter.showChild(rootView);
                }else {
                    Toast.makeText(getApplicationContext(),"点击了第"+mGroupAdapter.getParentIndexFromChild(position)+"个parent的"+mGroupAdapter.getChildIndexForParent(position),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View rootView, int position) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
