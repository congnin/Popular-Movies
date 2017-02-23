package com.blablaing.android.popular_movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.ui.AspectLockedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private final static String LOG_TAG = MovieListAdapter.class.getSimpleName();
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        final Context context = view.getContext();

        int gridColsNumber = context.getResources()
                .getInteger(R.integer.grid_number_cols);

        view.getLayoutParams().height = (int) (parent.getWidth() / gridColsNumber *
                POSTER_ASPECT_RATIO);
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUp();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();

        holder.mMovie = movie;
        holder.mTitleView.setText(movie.getTitle());

        String posterUrl = movie.getPosterUrl(context);

        Picasso.with(context)
                .load(posterUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mThumbnailView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                if(holder.mMovie.getId() != movie.getId()){
                                    holder.cleanUp();
                                }else{
                                    holder.mThumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.open(movie, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final AspectLockedImageView mThumbnailView;
        public final View mItemView;
        public final View mFooter;
        public final TextView mTitleView;
        public Movie mMovie;

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

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
        return mMovies;
    }


}
