package com.blablaing.android.popular_movies.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blablaing.android.popular_movies.R;
import com.blablaing.android.popular_movies.data.MovieContract;
import com.bumptech.glide.Glide;

/**
 * Created by congnc on 2/22/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private Cursor mCursor;
    final private Context mContext;
    final private MovieAdapterOnClickHandler mClickHandler;

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
        view.setFocusable(true);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String posterUrl = mContext.getString(R.string.url_for_downloading_poster) +
                mCursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
        Glide.with(mContext)
                .load(posterUrl)
                .crossFade()
                .into(holder.mThumnail);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mThumnail;
        public final TextView mTitle;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mThumnail = (ImageView) view.findViewById(R.id.thumbnail);
            mTitle = (TextView) view.findViewById(R.id.title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public static interface MovieAdapterOnClickHandler {
        void onClick(Long movieId, MovieAdapterViewHolder vh);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler dh) {
        mContext = context;
        mClickHandler = dh;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
