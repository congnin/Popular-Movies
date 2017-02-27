package com.blablaing.android.popular_movies.ui.RecyclerView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by tuandt on 11/21/16.
 */

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
