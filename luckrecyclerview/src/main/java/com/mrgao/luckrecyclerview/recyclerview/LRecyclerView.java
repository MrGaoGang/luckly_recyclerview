package com.mrgao.luckrecyclerview.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mrgao.luckrecyclerview.LucklyRecyclerView;
import com.mrgao.luckrecyclerview.R;
import com.mrgao.luckrecyclerview.decoration.LRecyclerViewItemDivider;
import com.mrgao.luckrecyclerview.header.HeaderView;
import com.mrgao.luckrecyclerview.interfaces.LuckRecyclerViewInterface;
import com.mrgao.luckrecyclerview.utils.ScreenUtils;
import com.mrgao.luckrecyclerview.views.TwoFishView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mr.gao on 2018/1/14.
 * Package:    mrgao.com.recyclerviewtext.loadMore.recyclerview
 * Create Date:2018/1/14
 * Project Name:RecyclerViewText
 * Description:
 */

public class LRecyclerView extends RecyclerView implements LuckRecyclerViewInterface {
    public String TAG = "LRecyclerView";

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    //正在加载
    public static final int LOADING = -1;
    //加载完成
    public static final int LOADING_COMPLETE = -2;
    //无更多数据
    public static final int LOADING_END = -3;
    //默认情况是加载完成
    int mFooterState = LOADING_COMPLETE;
    //滑动监听
    private LucklyRecyclerView.OnScrollCallback mOnScrollCallback;
    //是否是向上滑动
    private boolean mIsSlowUp = false;
    //加载更多监听
    private LucklyRecyclerView.OnLoadMoreListener mLoadMoreListener;
    //下拉加载更多监听
    private LucklyRecyclerView.OnRefreshListener mOnRefreshListener;
    //添加headerView和footerView的包装适配器
    private LoadMoreWrapAdapter mWrapAdapter;
    //适配器的数据监听
    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
    //空视图View
    private View mEmptyView;
    //错误视图View
    private View mErrorView;
    //是否显示错误视图
    private boolean mErrorShow = false;
    //正在加载
    private String mMoreLoading = "正在加载...";
    //加载更多处于完成状态
    private String mMoreLoadingComplete = "上拉加载更多";
    //没有更多数据可以加载
    private String mMoreLoadingEnd = "无更多数据";
    //顶部的下拉刷新
    private HeaderView mHeaderView;
    //下拉刷新的最近触摸点的Y轴的值
    private float mLatesY = -1;
    //表示是否可以下拉刷新
    private boolean mCanRefresh = true;
    //空视图和错误视图显示的点击事件
    private boolean mOnClickErrorToRefresh = false;

    /**
     * @param context
     */
    public LRecyclerView(Context context) {
        this(context, null);

    }

    /**
     * @param context
     * @param attrs
     */
    public LRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);


    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public LRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHeaderView = new HeaderView(getContext());

    }


    /**
     *
     */
    private void addOnScrollListener() {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastItemPosition = -1;
                int itemCount = 0;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LayoutManager manager = recyclerView.getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;

                        lastItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        itemCount = linearLayoutManager.getItemCount();


                    } else if (manager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                        //GridLayoutManager判断上拉加载还没有做
                        lastItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                        itemCount = gridLayoutManager.getItemCount();

                    } else if (manager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
                        int[] into = new int[((StaggeredGridLayoutManager) staggeredGridLayoutManager).getSpanCount()];
                        staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(into);
                        lastItemPosition = findMax(into);
                        itemCount = staggeredGridLayoutManager.getItemCount();
                    }


                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1) && mIsSlowUp) {
                        //加载更多
                        if (mLoadMoreListener != null) {
                            mLoadMoreListener.onLoadMore();
                        }

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mIsSlowUp = dy > 0;

            }
        });


    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mLatesY == -1) {
            mLatesY = e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLatesY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceY = e.getRawY() - mLatesY;
                mLatesY = e.getRawY();
                if (isItemOnTop() && mCanRefresh) {
                    mHeaderView.onAcionMove(distanceY);
                    //下拉状态的时候，就不调用下面的方法
                    if (mHeaderView.getVisibleHeight() > 0 && mHeaderView.getState() < HeaderView.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;

            default:
                mLatesY = -1;
                if (mHeaderView.releaseAction() && mCanRefresh && isItemOnTop()) {
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                }

                break;
        }
        return super.onTouchEvent(e);
    }


    /**
     * 判断是否已经到达了顶部
     *
     * @return
     */
    private boolean isItemOnTop() {
        boolean isTop = false;
        int position;
        LayoutManager manager = getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (position == 0 || position == 1) {
                isTop = true;
            } else {
                isTop = false;
            }
        } else if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            position = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (position == 0 || position == 1) {
                isTop = true;
            } else {
                isTop = false;
            }
        } else {
            //是否还可以往下滑动，如果不可以表示已经到达了顶部
            if (!canScrollVertically(-1)) {
                isTop = true;
            } else {
                isTop = false;
            }
        }


        return isTop;
    }

    /**
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 滑动监听事件
     */
    private void addScrollListener() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mOnScrollCallback.onStateChanged(LRecyclerView.this, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy >= 0) {
                    mOnScrollCallback.onScrollDown(LRecyclerView.this, dy);
                } else {
                    mOnScrollCallback.onScrollUp(LRecyclerView.this, dy);
                }
            }

        });
    }

    /**
     * @return
     */
    @Override
    public RecyclerView getOriginalRecyclerView() {
        return this;
    }

    /**
     * 设置监听事件
     *
     * @param loadMore
     */

    @Override
    public void setLoadMoreListener(LucklyRecyclerView.OnLoadMoreListener loadMore) {
        if (loadMore != null) {
            mLoadMoreListener = loadMore;
            addOnScrollListener();
        }
    }

    /**
     * 监听滑动状态
     *
     * @param onScrollCallback
     */
    @Override
    public void setOnScrollCallback(LucklyRecyclerView.OnScrollCallback onScrollCallback) {
        if (onScrollCallback != null) {
            this.mOnScrollCallback = onScrollCallback;
            addScrollListener();
        }

    }

    /**
     * 设置适配器
     *
     * @param adapter
     */

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new LoadMoreWrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();

    }

    /**
     * 获取头部的数量
     *
     * @return
     */
    @Override
    public int getHeaderViewCount() {
        if (mWrapAdapter != null) {
            return mWrapAdapter.getHeaderCount();
        } else {
            return 0;
        }
    }

    /**
     * @return
     */
    @Override
    public List<View> getHeaderViews() {
        if (mWrapAdapter != null) {
            return mWrapAdapter.getHeaderViews();
        }
        return null;
    }

    /**
     * 添加头部
     *
     * @param view
     */
    @Override
    public void addHeaderView(View view) {
        if (mWrapAdapter != null) {
            mWrapAdapter.addHeaderView(view);
        }
    }

    /**
     * 添加头部
     *
     * @param id
     */
    @Override
    public void addHeaderView(int id) {
        if (mWrapAdapter != null) {
            View view = LayoutInflater.from(getContext()).inflate(id, this, false);
            mWrapAdapter.addHeaderView(view);
        }
    }

    /**
     * 比如显示空视图或者是错误视图后需要恢复之前的状态
     */
    public void reset() {
        showError(false);
        setLoadingComplete();
        setRefreshComplete();
        mDataObserver.onChanged();
    }

    /**
     * 添加为空的时候的方法
     *
     * @param view
     */
    @Override
    public void setEmptyView(View view) {
        if (view != null) {
            mEmptyView = view;
            mEmptyView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickToRefresh();
                }
            });
            addViewToRoot(mEmptyView);

        }

    }

    /**
     * 添加为空的时候的方法
     *
     * @param id
     */
    @Override
    public void setEmptyView(int id) {
        mEmptyView = LayoutInflater.from(getContext()).inflate(id, (ViewGroup) getParent(), false);
        setEmptyView(mEmptyView);

    }

    /**
     * 添加错误的时候视图
     *
     * @param view
     */
    @Override
    public void setErrorView(View view) {
        if (view != null) {
            mErrorView = view;
            mErrorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickToRefresh();
                }
            });
            addViewToRoot(mErrorView);
        }
    }

    private void addViewToRoot(View view) {
        //一定要记得加这句话，这样才会在RecyclerView中添加此布局

        ViewGroup rootView = (ViewGroup) this.getParent();
        int childCount = rootView.getChildCount();

        if (view.getTag() == null) {//避免重复添加EmptyView
            view.setTag(childCount + 1);
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity= Gravity.CENTER;
            rootView.addView(view,layoutParams);
            mDataObserver.onChanged();
        }
    }

    /**
     * 添加错误的时候视图
     *
     * @param id
     */
    @Override
    public void setErrorView(int id) {
        mErrorView = LayoutInflater.from(getContext()).inflate(id, (ViewGroup) getParent(), false);
        setErrorView(mErrorView);
    }


    /**
     * 显示错误
     *
     * @param errorShow
     */
    @Override
    public void showError(boolean errorShow) {
        mErrorShow = errorShow;
        mDataObserver.onChanged();

    }

    /**
     * 返回空视图
     *
     * @return
     */
    @Override
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 返回错误的视图
     *
     * @return
     */
    @Override
    public View getErrorView() {
        return mErrorView;
    }

    /**
     * 获取到当前footerView处于的状态
     *
     * @return
     */
    @Override
    public int getLoadingState() {
        return mFooterState;
    }

    /**
     * 设置加载更多处于加载状态
     */
    @Override
    public void setLoading() {
        if (mWrapAdapter != null) {
            mFooterState = LOADING;
            mWrapAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 设置加载更多处于加载状态
     *
     * @param loading
     */
    @Override
    public void setLoading(String loading) {
        if (mWrapAdapter != null) {
            mFooterState = LOADING;
            mMoreLoading = loading;
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置加载更多处于加载状态为完成
     */
    @Override
    public void setLoadingComplete() {
        if (mWrapAdapter != null) {
            mFooterState = LOADING_COMPLETE;
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置加载更多处于加载状态为完成
     */
    @Override
    public void setLoadingComplete(String loading) {
        if (mWrapAdapter != null) {
            mFooterState = LOADING_COMPLETE;
            mMoreLoadingComplete = loading;
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置无更多数据
     */
    @Override
    public void setLoadingNoMore() {
        if (mWrapAdapter != null) {
            mFooterState = LOADING_END;
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置无更多数据
     *
     * @param loading
     */
    @Override
    public void setLoadingNoMore(String loading) {
        if (mWrapAdapter != null) {
            mFooterState = LOADING_END;
            mMoreLoadingEnd = loading;
            mWrapAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 使用以及封装好的的GridDivider；
     * 设置分割线以及颜色
     *
     * @param color
     * @param dividerHeight
     */
    @Override
    public void addGridDivider(int color, int dividerHeight) {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                this.addItemDecoration(new LRecyclerViewGridStaggerDivider(color, dividerHeight));
            }
        } else {
            throw new IllegalArgumentException("Please set up the manager before setting the divider");
        }
    }

    /**
     * 使用以及封装好的的GridDivider
     */
    @Override
    public void addGridDivider() {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                this.addItemDecoration(new LRecyclerViewGridStaggerDivider(getContext()));
            }
        } else {
            throw new IllegalArgumentException("Please set up the manager before setting the divider");
        }

    }

    /**
     * 设置线性布局的分割线
     *
     * @param oritation
     */
    @Override
    public void addLinearDivider(int oritation) {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                this.addItemDecoration(new LRecyclerViewItemDivider(getContext(), oritation));
            }
        } else {
            throw new IllegalArgumentException("Please set up the manager before setting the divider");
        }
    }

    /**
     * 设置线性布局的分割线
     *
     * @param oritation
     * @param color
     * @param lineWidth
     */
    @Override
    public void addLinearDivider(int oritation, int color, int lineWidth) {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            if (manager instanceof LinearLayoutManager) {
                this.addItemDecoration(new LRecyclerViewItemDivider(oritation, color, lineWidth));
            }
        } else {
            throw new IllegalArgumentException("Please set up the manager before setting the divider");
        }
    }

    /**
     * @param color
     */
    @Override
    public void setLoadingTextColor(int color) {
        if (mWrapAdapter != null) {
            mWrapAdapter.setLoadingTextColor(color);
        }
    }

    /**
     * @param progressColor
     */
    @Override
    public void setLoadingProgressColor(int progressColor) {
        if (mWrapAdapter != null) {
            mWrapAdapter.setProgressColor(progressColor);
        }
    }


    @Override
    public void setFooterVisiable(boolean visiable) {
        if (mWrapAdapter != null) {
            mWrapAdapter.setFooterVisiable(visiable);
        }
    }


    /**
     * @param onItemClickListener
     */
    @Override
    public void setOnItemClickListener(LucklyRecyclerView.OnItemClickListener onItemClickListener) {
        if (mWrapAdapter != null) {
            mWrapAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    @Override
    public void setOnRefreshListener(LucklyRecyclerView.OnRefreshListener onRefreshListener) {
        if (onRefreshListener != null) {
            mOnRefreshListener = onRefreshListener;
        }
    }

    @Override
    public void refresh() {
        if (mCanRefresh && mOnRefreshListener != null) {
            mHeaderView.setState(HeaderView.STATE_REFRESHING);
            mOnRefreshListener.onRefresh();
        }
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        mCanRefresh = enable;
        mHeaderView.setVisibleHeight(0);


    }

    @Override
    public void setRefreshComplete() {
        mHeaderView.refreshComplete();
    }

    @Override
    public void setDuration(int duration) {
        mHeaderView.setDuration(duration);
    }

    @Override
    public void setRefreshColor(int color) {
        mHeaderView.setRefreshColor(color);
    }

    @Override
    public void setOnClickEmptyOrErrorToRefresh(boolean emptyToRefresh) {
        mOnClickErrorToRefresh = emptyToRefresh;
    }

    /**
     * 检查是否应该为空
     */
    @SuppressLint("WrongConstant")
    private void checkIfEmpty() {
        if (mEmptyView != null && mWrapAdapter != null) {
            final boolean emptyViewVisible =
                    mWrapAdapter.getItemCount() == mWrapAdapter.getHeaderCount() + 2;
            mEmptyView.setVisibility(emptyViewVisible ? View.VISIBLE : INVISIBLE);
            setVisibility(emptyViewVisible ? INVISIBLE : VISIBLE);
        }
    }


    /**
     * 监听适配器的数据变化的
     */
    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @SuppressLint("WrongConstant")
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {

                mWrapAdapter.notifyDataSetChanged();
            }
            checkIfEmpty();
            if (mErrorView != null) {
                if (mErrorShow) {
                    if (mEmptyView != null) {
                        mEmptyView.setVisibility(INVISIBLE);
                    }
                    LRecyclerView.this.setVisibility(INVISIBLE);
                    mErrorView.setVisibility(VISIBLE);

                } else {
                    mErrorView.setVisibility(INVISIBLE);
                    checkIfEmpty();
                }
            }


        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    @SuppressLint("WrongConstant")
    private void onClickToRefresh() {
        if (mOnClickErrorToRefresh && mErrorView != null && mErrorView.getVisibility() == VISIBLE && mErrorShow == true) {
            if (mOnRefreshListener != null) {
                mErrorShow = false;
                mDataObserver.onChanged();
                refresh();
            }

        }
        if (mOnClickErrorToRefresh && mEmptyView != null && mEmptyView.getVisibility() == VISIBLE) {
            if (mOnRefreshListener != null) {
                mEmptyView.setVisibility(INVISIBLE);
                LRecyclerView.this.setVisibility(VISIBLE);
                if (mErrorView != null) {
                    mErrorView.setVisibility(INVISIBLE);
                }
                refresh();
            }

        }
    }


    /**
     * Created by mr.gao on 2018/1/14.
     * Package:    mrgao.com.recyclerviewtext.loadMore.adapter
     * Create Date:2018/1/14
     * Project Name:RecyclerViewText
     * Description:
     */

    public class LoadMoreWrapAdapter extends Adapter<ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        //下拉刷新
        private final int HEADER_REFRESH_TYPE = -2;
        //底部的Item
        public final int FOOTER_TYPE = -1;
        //HeaderView的其实下标，这个的目的是，防止用户在自定义的时候Type和这个headerType相同
        private final int HEADER_TYPE_SIZE = 20000;
        //每个header必须有不同的type,不然滚动的时候顺序会变化
        private List<Integer> mHeaderTypes = new ArrayList<>();
        //底部的view
        private View mFooterView;
        //头部的view
        private List<View> mHeaderViews;

        private int mLoadingTextColor = Color.RED;

        private int mProgressColor = Color.RED;

        private boolean isFooterVisiable = true;
        private LucklyRecyclerView.OnItemClickListener mOnItemClickListener;

        public LoadMoreWrapAdapter(Adapter adapter) {
            mAdapter = adapter;
            mHeaderViews = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_REFRESH_TYPE) {
                return new HeaderViewHolder(mHeaderView);

            } else if (viewType == FOOTER_TYPE) {
                mFooterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view, parent, false);
                return new FooterHolder(mFooterView);
            } else if (isHeaderType(viewType)) {
                return new HeaderViewHolder(getHeaderViewByType(viewType));
            } else {
                return mAdapter.onCreateViewHolder(parent, viewType);
            }

        }

        @SuppressLint("WrongConstant")
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (holder instanceof FooterHolder) {
                FooterHolder footerHolder = (FooterHolder) holder;
                if (isFooterVisiable) {
                    if (!isLittleDataHideFooter(position, holder.itemView)) {
                        footerHolder.itemView.setVisibility(VISIBLE);
                        footerHolder.mProgressBar.setProgressColor(mProgressColor);
                        footerHolder.mTextView.setTextColor(mLoadingTextColor);
                        if (mFooterState == LOADING_COMPLETE) {
                            footerHolder.mProgressBar.setVisibility(GONE);
                            footerHolder.mTextView.setText(mMoreLoadingComplete);
                        } else if (mFooterState == LOADING) {
                            footerHolder.mProgressBar.setVisibility(VISIBLE);
                            footerHolder.mTextView.setText(mMoreLoading);

                        } else if (mFooterState == LOADING_END) {
                            footerHolder.mProgressBar.setVisibility(GONE);
                            footerHolder.mTextView.setText(mMoreLoadingEnd);
                        }
                    }

                } else {
                    footerHolder.itemView.setVisibility(INVISIBLE);
                }


            } else if (holder instanceof HeaderViewHolder) {

            } else {
                //注意，此处必须要讲position减去头部的个数
                if (mAdapter != null) {
                    if (position - mHeaderViews.size() - 1 < mAdapter.getItemCount()) {
                        mAdapter.onBindViewHolder(holder, position - mHeaderViews.size() - 1);
                    }
                }

            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        //减掉1是为了减掉顶部下拉刷新，设置为不可显示
                        mOnItemClickListener.onItemClick(holder.itemView, position - 1);

                    }
                }
            });

            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemLongClick(holder.itemView, position - 1);
                    }
                    return false;
                }
            });

        }

        /**
         * +2的原因是：一个为顶部下拉刷新，一个为底部上拉加载
         *
         * @return
         */
        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                if (mAdapter.getItemCount() > 0) {
                    return mAdapter.getItemCount() + 2 + mHeaderViews.size();
                } else {
                    return mHeaderViews.size() + 2;
                }
            } else {
                return mHeaderViews.size() + 2;
            }


        }


        @Override
        public long getItemId(int position) {
            if (mAdapter != null && position >= getHeaderCount() + 1) {
                int adjPosition = position - getHeaderCount() - 1;
                if (adjPosition < mAdapter.getItemCount()) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {//最开始的为下拉刷新的布局
                return HEADER_REFRESH_TYPE;
            }
            if (position >= 1 && position < mHeaderViews.size() + 1) {
                return getHeaderTypeByPosition(position - 1);
            }
            if (position == getItemCount() - 1) {
                return FOOTER_TYPE;
            }
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                int realPosition = position - getHeaderCount() - 1;
                if (realPosition < adapterCount) {
                    int type = mAdapter.getItemViewType(realPosition);
                    if (type >= HEADER_TYPE_SIZE) {
                        throw new IndexOutOfBoundsException("LucklyRecyclerView require itemType below 20000 ");
                    }
                    return type;
                }
            }
            return 0;

        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            mAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            mAdapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return mAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                final int spanCount = gridLayoutManager.getSpanCount();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        // 如果当前是footer的位置，那么该item占据一行的个单元格，正常情况下占据1个单元格
                        if (getItemViewType(position) == FOOTER_TYPE) {
                            return spanCount;
                        } else if (isRefreshHeader(position) || getItemViewType(position) == getHeaderTypeByPosition(position - 1)) {
                            return spanCount;
                        } else {
                            return 1;
                        }
                    }
                });
            }
            mAdapter.onAttachedToRecyclerView(recyclerView);
        }


        private boolean isRefreshHeader(int position) {
            if (getItemViewType(position) == HEADER_REFRESH_TYPE) {
                return true;
            }
            return false;
        }

        /**
         * 当数据比较少的时候，没有到达屏幕的最下方，那么久不显示footer
         *
         * @param position
         * @param lastVisiableView
         */
        @SuppressLint("WrongConstant")
        private boolean isLittleDataHideFooter(int position, View lastVisiableView) {
            if (position == getItemCount() - 1) {

                if (lastVisiableView != null) {
                    if (computeVerticalScrollRange() < ScreenUtils.getScreenHeight(getContext())) {
                        lastVisiableView.setVisibility(INVISIBLE);
                        return true;
                    } else {
                        lastVisiableView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }
            }

            return false;

        }

        /**
         * 根据header的ViewType判断是哪个header
         *
         * @param itemType
         * @return
         */
        private View getHeaderViewByType(int itemType) {
            if (!isHeaderType(itemType)) {
                return null;
            }
            return mHeaderViews.get(itemType - HEADER_TYPE_SIZE);
        }

        /**
         * 判断一个type是否为HeaderType
         *
         * @param itemViewType
         * @return
         */
        private boolean isHeaderType(int itemViewType) {
            return mHeaderViews.size() > 0 && mHeaderTypes.contains(itemViewType);
        }

        /**
         * 根据位置得到type
         *
         * @param position
         * @return
         */
        private int getHeaderTypeByPosition(int position) {
            if (position < mHeaderTypes.size()) {
                return mHeaderTypes.get(position);
            } else {
                return -10000;
            }
        }

        /**
         * 设置加载更多的字体的颜色
         *
         * @param color
         */
        public void setLoadingTextColor(int color) {
            this.mLoadingTextColor = color;
            notifyItemRangeChanged(getItemCount() - 1, 1);
        }

        /**
         * 设置进度条的颜色
         *
         * @param color
         */
        public void setProgressColor(int color) {
            this.mProgressColor = color;
            notifyItemRangeChanged(getItemCount() - 1, 1);
        }

        public void setFooterVisiable(boolean visiable) {
            isFooterVisiable = visiable;
            notifyItemRangeChanged(getItemCount() - 1, 1);
        }

        /**
         * 获取到头部的数量
         *
         * @return
         */
        public int getHeaderCount() {
            return mHeaderViews.size();
        }

        /**
         * 添加头部
         *
         * @param view
         */
        public void addHeaderView(View view) {
            if (view != null) {
                mHeaderTypes.add(HEADER_TYPE_SIZE + mHeaderViews.size());
                mHeaderViews.add(view);

                notifyItemInserted(0);
            }
        }

        /**
         * 返回所以的头部View
         *
         * @return
         */
        public List<View> getHeaderViews() {
            return mHeaderViews;
        }

        /**
         * 获取到最原始的适配器
         *
         * @return
         */
        public Adapter getOriginalAdapter() {
            return mAdapter;
        }

        /**
         * 点击事件
         *
         * @param onItemClickListener
         */
        public void setOnItemClickListener(LucklyRecyclerView.OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public class FooterHolder extends ViewHolder {
            TwoFishView mProgressBar;
            TextView mTextView;

            public FooterHolder(View itemView) {
                super(itemView);
                mProgressBar = getView(itemView, R.id.progressBar);
                mTextView = getView(itemView, R.id.footer);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        private <T extends View> T getView(View view, int id) {
            return (T) view.findViewById(id);
        }


    }

    /**
     * Created by mr.gao on 2018/1/13.
     * Package:    mrgao.com.recyclerviewtext.dividerModule.divider
     * Create Date:2018/1/13
     * Project Name:RecyclerViewText
     * Description:网格式布局和流式布局的分割线。
     * 流程：
     * 其实在每一个Item绘制分割线的时候，都会调用getItemOffsets()方法，来获取每一个Item的的偏移量。
     * 为什么要获取偏移量呢？那是因为获取偏移量，那个偏移量的值就是用来绘制分割线的。
     * 当在最后一列的时候，只需要下方的偏移量（同上，其实也是绘制了的，只是没有显示出来罢了）
     * 当时在最后一行的时候：只需要右边的偏移量。（同上）
     * 其余的情况：只需要下方和右边的偏移量即可。
     */

    public class LRecyclerViewGridStaggerDivider extends ItemDecoration {

        private String TAG = "LRecyclerViewGridStaggerDivider";
        private int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable mDrawable;
        private Paint mPaint;
        private int lineHeight = 1;


        public LRecyclerViewGridStaggerDivider(Context context) {
            TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
            mDrawable = typedArray.getDrawable(0);
            typedArray.recycle();
        }

        public LRecyclerViewGridStaggerDivider(int color, int lineHeight) {
            mPaint = new Paint();
            mPaint.setColor(color);
            mPaint.setStrokeWidth(lineHeight);
            this.lineHeight = lineHeight;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            super.onDraw(c, parent, state);
            drawHorizontal(c, parent);

            drawVertical(c, parent);
        }

        /**
         * 绘制竖着的线
         *
         * @param canvas
         * @param parent
         */
        private void drawVertical(Canvas canvas, RecyclerView parent) {

            LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                    int left = child.getRight() + layoutParams.rightMargin;
                    int top = child.getTop() - layoutParams.topMargin;
                    if (mDrawable != null) {
                        int right = left + mDrawable.getIntrinsicHeight();
                        int bottom = child.getBottom() + layoutParams.bottomMargin + mDrawable.getIntrinsicHeight();

                        mDrawable.setBounds(left, top, right, bottom);
                        mDrawable.draw(canvas);

                    } else if (mPaint != null) {
                        int right = left + lineHeight;
                        int bottom = child.getBottom() + layoutParams.bottomMargin + lineHeight;
                        canvas.drawRect(left, top, right, bottom, mPaint);
                    }

                }
            }

        }


        /**
         * 绘制横着的线
         *
         * @param canvas
         * @param parent
         */
        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                int count = parent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                    int left = child.getLeft() - layoutParams.leftMargin;
                    int top = child.getBottom() + layoutParams.bottomMargin;


                    if (mDrawable != null) {
                        int bottom = top + mDrawable.getIntrinsicHeight();
                        int right = child.getRight() + layoutParams.rightMargin;
                        mDrawable.setBounds(left, top, right, bottom);
                        mDrawable.draw(canvas);

                    } else if (mPaint != null) {
                        int bottom = top + lineHeight;
                        int right = child.getRight() + layoutParams.rightMargin;

                        canvas.drawRect(left, top, right, bottom, mPaint);
                    }

                }
            }
        }


        /**
         * 判断是否是最后一列
         * 为什么要判断StaggeredGridLayoutManager？
         * 这里需要注意的是StaggeredGridLayoutManager构造的第二个参数传一个orientation，
         * 如果传入的是StaggeredGridLayoutManager.VERTICAL那么前面那个参数就代表有多少列；
         * 如果传是StaggeredGridLayoutManager.HORIZONTAL那么前面那个参数就代表有多少行
         *
         * @param spanCount
         * @param position
         * @param childCount
         * @param recyclerView
         * @return
         */
        private boolean isLastColumn(int spanCount, int position, int childCount, RecyclerView recyclerView) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            //如果有headerView那就不一样咯
            if (position >= mWrapAdapter.getHeaderCount()) {
                if (layoutManager instanceof GridLayoutManager) {
                    if ((position - mWrapAdapter.getHeaderCount() + 1) % spanCount == 0) {
                        return true;
                    }
                }

            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                if (orientation == StaggeredGridLayoutManager.HORIZONTAL) {
                    //判断是否是最后一列
                    childCount = childCount - childCount % spanCount;
                    if (position >= childCount) {
                        return true;
                    }
                } else {
                    if ((position + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }

            return false;
        }


        /**
         * 判断是否是最后一行
         * 为什么要判断StaggeredGridLayoutManager？
         * 这里需要注意的是StaggeredGridLayoutManager构造的第二个参数传一个orientation，
         * 如果传入的是StaggeredGridLayoutManager.VERTICAL那么前面那个参数就代表有多少列；
         * 如果传是StaggeredGridLayoutManager.HORIZONTAL那么前面那个参数就代表有多少行
         *
         * @param spanCount
         * @param childCount
         * @param position
         * @param recyclerView
         * @return
         */
        private boolean isLastRow(int spanCount, int childCount, int position, RecyclerView recyclerView) {

            if (position >= mWrapAdapter.getHeaderCount()) {

                int tempPosition = position - mWrapAdapter.getHeaderCount();
                LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    //下面是一种方法判断是最后一行的，首先判断最特殊的末尾，看是否可以整除，如果可以整除的话，那么下面的公式就会成立
                    if (childCount % spanCount == 0) {
                        if ((tempPosition + spanCount) / spanCount == childCount / spanCount) {
                            return true;
                        }
                    } else {
                        //否则：下面的会成立
                        if (tempPosition / spanCount == childCount / spanCount) {
                            return true;
                        }
                    }

                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int oration = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                    //StaggeredGridLayoutManager纵向滑动
                    if (oration == StaggeredGridLayoutManager.VERTICAL) {
                        //判断是否是最后一行的第二种方式。这种方式更为简便
                        childCount = childCount - childCount % spanCount;
                        if (tempPosition >= childCount) {
                            return true;
                        }
                    } else {
                        //StaggeredGridLayoutManager横向滑动
                        if ((tempPosition + 1) % spanCount == 0) {
                            return true;
                        }

                    }
                }
            }

            return false;
        }

        /**
         * 通过这个方法在每一个Item绘制的时候回提前调用，那么首先必须要获取到这个view的position；
         * 获取位置的时候 切记不要使用for循环，不然会调用很多次；
         *
         * @param outRect
         * @param view
         * @param parent
         * @param state
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            LayoutManager layoutManager = parent.getLayoutManager();
            int spanCount = 0;
            if (layoutManager instanceof GridLayoutManager) {
                spanCount = ((GridLayoutManager) layoutManager).getSpanCount();

            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }

            int count = parent.getAdapter().getItemCount();


            //获取到对应表的位置很重要，切记不要在这个里面使用for(int i=0;i<parent.getchildCount;i++),
            //因为这个方法是每一个Item回执单额时候 调用的
            int i = parent.getChildAdapterPosition(view);
            //最后一行切实最后一列，设置item偏移量都为0，那么就不会显示边框
            if (isLastColumn(spanCount, i, count, parent) && isLastRow(spanCount, count, i, parent)) {
                outRect.set(0, 0, 0, 0);
            } else {
                //否则：如果是最后一行的话，设置item偏移量 右边都为itemHeight，那么其他的就不会显示边框
                //如果是最后一列：设置item偏移量 下方为itemHeight，那么其他的就不会显示边框
                //否则：设置右边和下边的偏移量
                if (isLastRow(spanCount, count, i, parent)) {
                    if (mDrawable != null) {
                        outRect.set(0, 0, mDrawable.getIntrinsicWidth(), 0);
                    } else if (mPaint != null) {
                        outRect.set(0, 0, lineHeight, 0);
                    }
                } else if (isLastColumn(spanCount, i, count, parent)) {
                    if (mDrawable != null) {
                        outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
                    } else if (mPaint != null) {
                        outRect.set(0, 0, 0, lineHeight);
                    }

                } else {
                    if (mDrawable != null) {
                        outRect.set(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
                    } else if (mPaint != null) {
                        outRect.set(0, 0, lineHeight, lineHeight);
                    }
                }
            }

        }


    }


}
