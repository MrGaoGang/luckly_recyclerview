# LucklyRecyclerView
使用RecyclerView封装headerview,footerView,并实现上拉加载更多，下拉刷新，分组功能(添加上拉加载和下拉刷新设置背景)
====
**界面可能有点丑，😁，但是是为了展示所有效果，请大家见谅。（所有东西都是可自己设置的哦）😊** <br>
效果图：
![](https://github.com/MrGaoGang/LucklyRecyclerView/blob/master/images/main.gif) 
![](https://github.com/MrGaoGang/LucklyRecyclerView/blob/master/images/group.gif) 
<br>
如何获取:[![](https://jitpack.io/v/mrgaogang/luckly_recyclerview.svg)](https://jitpack.io/#mrgaogang/luckly_recyclerview)
<br>
第一步：在项目的build.gradle中添加<br>
```Java
 allprojects {
	repositories {
	...
	maven { url 'https://jitpack.io' }
	}
}
```
第二步：添加依赖<br>
```Java
 dependencies {
	 compile 'com.github.mrgaogang:luckly_recyclerview:v2.1.1'
}
```
<br>

## 一、部分方法介绍<br>
#### 1、设置加载更多的监听事件<br>
```Java
	mLRecyclerView.setLoadMoreListener(this);
```
并重写onLoadMore()方法。
<br>

#### 2、设置下拉刷新监听事件<br>
```Java
	mLRecyclerView.setOnRefreshListener(this);
```
并重写onRefresh()方法。
<br>

#### 3、添加分割线<br>
	已经封装好了线性布局的分割线和网格式布局的分割线、流式布局的分割线。
```Java
	//线性布局
 	mLRecyclerView.addLinearDivider(LRecyclerView.VERTICAL_LIST);
	//网格式布局
   	 mLRecyclerView.addGridDivider();
	//可以指定颜色和宽度
	addGridDivider(int color, int dividerHeight)
	addLinearDivider(int oritation, int color, int lineWidth)
```
<br>

#### 4、添加错误视图<br>
	当网络连接失败等情况的时候，需要显示错误视图。
```Java
	//添加错误的View
   	 mLRecyclerView.setErrorView(R.layout.error_view);
	//添加错误的View
	View error = LayoutInflater.from(this).inflate(R.layout.view_error, null, false);
   	 mLRecyclerView.setErrorView(error);
```
<br>
使用getErrorView()得到错误视图。<br>

#### 5、添加空视图<br>
	当数据为空的时候，需要显示。
```Java
	//添加空的View
  	  mLRecyclerView.setEmptyView(R.layout.empty_view);
	//添加空的View
	View empty = LayoutInflater.from(this).inflate(R.layout.view_empty, null, false);
   	 mLRecyclerView.setEmptyView(error);
```
<br>
使用getErrorView()得到空视图。<br>

#### 6、添加headerView<br>
```Java
	//添加headerView
   	 mLRecyclerView.addHeaderView(R.layout.header_view);
	//添加headerView，需要设置父类为mLRecyclerView
	View headerView = LayoutInflater.from(this).inflate(R.layout.header_view, mLRecyclerView, false);
   	 mLRecyclerView.addHeaderView(headerView);
```
```Java
	//得到所有headerView视图。
	List<View> getHeaderViews();
	//得到所有headerView的个数。
	int getHeaderViewCount();
```
<br>

#### 7、设置下拉刷新进度条的颜色和字体的颜色<br>
```Java
	//改变下方加载进度的字体颜色
	mLRecyclerView.setLoadingTextColor(Color.BLUE);
	//改变下方加载进度条的颜色
	mLRecyclerView.setLoadingProgressColor(Color.BLUE);
	//修改下拉刷新颜色
	mLRecyclerView.setRefreshColor(getResources().getColor(R.color.colorAccent));
```
#### 8、设置监听事件<br>
```Java
 	//设置点击事件，注意此处返回的position是包括了headerView和下拉加载的视图的
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
```

### 9、设置上拉加载和下拉刷新在不同的状态<br>
```Java
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
```



```Java
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
        }, 5000);
    }
```

### 10、设置是否空白视图和错误视图点击刷新<br>

```Java
 mLRecyclerView.setOnClickEmptyOrErrorToRefresh(true);
```

### 11、可设置下拉刷新和上拉加载的背景图片(可用于广告的放置哦)

**注意:**
 1. 如果在初始化的时候 直接设置了背景图片可不用刷新adapter。</br>
 2. 如果通过网络获取到背景图片之后可以使用如下方法设置背景，但是需要添加一步：notifyItemChanged()

```Java
    //设置下拉刷新的背景图片（可放广告图片哦）
    mLRecyclerView.setRefreshBackground(getResources().getDrawable(R.drawable.headerback));
    //设置上拉加载部分设置背景图片（也可放广告哦）
    mLRecyclerView.setFooterBackground(getResources().getDrawable(R.drawable.footerback));
  	
    //如果通过网络获取的footer图片，则需要调用以下：（如果是设置刷新部分的背景直接调用setRefreshBackground）
     mLRecyclerView.getOriginalRecyclerView().getAdapter()
		.notifyItemChanged(mLRecyclerView.getOriginalRecyclerView().getAdapter().getItemCount() - 1);


```

## 二、如何实现分组

### 1、luckRecyclerView.setRecyclerViewType(LucklyRecyclerView.GROUP);

### 2、重写Adapter继承基类BaseGroupAdapter
<br>需要重写的几个方法：
```Java
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

```
<br>


在使用点击事件的时候要注意判断是否为Parent：<br>


```Java
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
```


### 3、常用的几个方法
1、获取child在parent下的index <br>

```Java
mGroupAdapter.getChildIndexForParent(position);
```
2、获取parent的index <br>

```Java
mGroupAdapter.getParentIndexFromChild(position);
```
3、判断当前position是否为parentView <br>

```Java
mGroupAdapter.isParentView(position);
```

## 三、具体如何使用请看例子
[LucklyRecyclerView](https://github.com/MrGaoGang/LucklyRecyclerView/tree/master/app/src/main/java/com/mrgao/lucklyrecyclerview)

欢迎关注我的微信公众号：
![](https://img-blog.csdn.net/2018100922484657?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzMyNDAwODIx/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
