package com.blablaing.android.popular_movies.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.blablaing.android.popular_movies.ui.RecyclerView.OnLoadMoreListener;

/**
 * Created by tuandt on 10/6/16.
 */

public class RecycleViewLoadMore extends RecyclerView {

    public Boolean loadMore = false;
    int previousTotal = 0; // The total number of items in the dataset after the last load
    boolean loading = true; // True if we are still waiting for the last set of data to load.
    int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;
    OnLoadMoreListener onLoadMoreListener;

    public RecycleViewLoadMore(Context context) {
        super(context);
    }

    public RecycleViewLoadMore(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleViewLoadMore(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initLoadMore() {
        if (this.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this
                    .getLayoutManager();


            this.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold) && loadMore) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        super.addOnScrollListener(listener);
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void refresh() {
        previousTotal = 0;
    }

    public Boolean getLoadMore() {
        return loadMore;
    }

    public void setLoadMore(Boolean loadMore) {
        this.loadMore = loadMore;
    }
}
