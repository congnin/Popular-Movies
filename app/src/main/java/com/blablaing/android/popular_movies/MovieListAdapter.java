package com.blablaing.android.popular_movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.ui.AspectLockedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
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

        int gridColNumber = 2;
        view.getLayoutParams().height = (int) ((parent.getWidth() / gridColNumber) * 1.5);
        return new ViewHolder(view);
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @Bind(R.id.movie_item_container)
        View mContentContainer;
        @Bind(R.id.movie_item_image)
        AspectLockedImageView mImage;
        @Bind(R.id.movie_item_title)
        TextView mTitle;
        @Bind(R.id.movie_item_genres)
        TextView mGenres;
        @Bind(R.id.movie_item_footer)
        View mFooter;

        @BindColor(R.color.colorPrimary)
        int mColorBackground;
        @BindColor(R.color.body_text_white)
        int mColorTitle;
        @BindColor(R.color.body_text_1_inverse)
        int mColorSubtitle;
        public Movie mMovie;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);
        final Context context = holder.mView.getContext();

        holder.mMovie = movie;
        holder.mTitle.setText(movie.getTitle());

        String posterUrl = movie.getPosterUrl(context);

        Picasso.with(context)
                .load(posterUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
