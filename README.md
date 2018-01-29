# LucklyRecyclerView
结合了SwipeRefreshLayout和RecyclerView,将其封装在一起。方便使用；上拉加载更多，下拉刷新
====

效果图：
![](https://github.com/MrGaoGang/LucklyRecyclerView/blob/master/images/main.gif) 
<br>
如何获取:[![](https://jitpack.io/v/MrGaoGang/LucklyRecyclerView.svg)](https://jitpack.io/#MrGaoGang/LucklyRecyclerView)<br>
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
	compile 'com.github.MrGaoGang:LucklyRecyclerView:v1.2.0'
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

### 9、设置下拉刷新在不同的状态<br>
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

## 二、具体如何使用请看例子
[LucklyRecyclerView](https://github.com/MrGaoGang/LucklyRecyclerView/tree/master/app/src/main/java/com/mrgao/lucklyrecyclerview)
