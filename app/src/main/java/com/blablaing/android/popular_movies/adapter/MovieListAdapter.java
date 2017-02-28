package com.blablaing.android.popular_movies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blablaing.android.popular_movies.R;
import com.blablaing.android.popular_movies.data.MovieContract;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.ui.AspectLockedImageView;
import com.blablaing.android.popular_movies.ui.RecyclerView.RefreshView;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter {
    @SuppressWarnings("unused")
    private final static String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private final static int VIEW_ITEM = 0;
    private final static int VIEW_PROG = 1;
    public static final float POSTER_ASPECT_RATIO = 1.8f;

    private final ArrayList<Movie> mMovies;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void open(Movie movie, int position);
    }

    public MovieListAdapter(ArrayList<Movie> movies, Callbacks callbacks) {
        mMovies = movies;
        this.mCallbacks = callbacks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie, parent, false);

            final Context context = view.getContext();

            int gridColsNumber = context.getResources()
                    .getInteger(R.integer.grid_number_cols);

            view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                    POSTER_ASPECT_RATIO);
            vh = new MovieListAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progressbar, parent, false);
            vh = new ProgressViewHolder(view);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final Movie movie = mMovies.get(position);
            final Context context = ((ViewHolder) holder).mView.getContext();

            ((ViewHolder) holder).mTitleView.setText(movie.getTitle());

            String posterUrl = movie.getPosterUrl(context);

            Picasso.with(context)
                    .load(posterUrl)
                    .config(Bitmap.Config.RGB_565)
                    .into(((ViewHolder) holder).mThumbnailView,
                            PicassoPalette.with(posterUrl, ((ViewHolder) holder).mThumbnailView)
                                    .use(PicassoPalette.Profile.VIBRANT)
                                    .intoBackground(((ViewHolder) holder).mFooter,
                                            PicassoPalette.Swatch.RGB)
                                    .intoTextColor(((ViewHolder) holder).mTitleView,
                                            PicassoPalette.Swatch.TITLE_TEXT_COLOR));

            ((ViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.open(movie, ((ViewHolder) holder).getAdapterPosition());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMovies.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final AspectLockedImageView mThumbnailView;
        public final View mItemView;
        public final View mFooter;
        public final TextView mTitleView;

        public ViewHolder(View view) {
            super(view);
            mThumbnailView = (AspectLockedImageView) view.findViewById(R.id.movie_item_image);
            mTitleView = (TextView) view.findViewById(R.id.movie_item_title);
            mItemView = view.findViewById(R.id.movie_item_container);
            mFooter = view.findViewById(R.id.movie_item_footer);
            mView = view;
        }

        public void cleanUp() {
            final Context context = mView.getContext();
            Picasso.with(context).cancelRequest(mThumbnailView);
            mThumbnailView.setImageBitmap(null);
            mThumbnailView.setVisibility(View.INVISIBLE);
            mTitleView.setVisibility(View.GONE);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        RefreshView progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (RefreshView) v.findViewById(R.id.progressbar_loading);
        }
    }

    public void addCursor(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                Movie movie = new Movie(id, title, posterPath, overview, rating, releaseDate, backdropPath);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public void add(List<Movie> movies) {
        mMovies.clear();mMovies.addAll(movies);
        notifyDataSetChanged();
    }


    public ArrayList<Movie> getMovies() {
        return mMovies;
    }

    public void clear() {
        mMovies.clear();
        notifyDataSetChanged();
    }
}
