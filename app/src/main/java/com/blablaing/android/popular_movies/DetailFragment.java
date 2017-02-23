package com.blablaing.android.popular_movies;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blablaing.android.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by congnc on 2/22/17.
 */

public class DetailFragment extends Fragment implements View.OnClickListener {
    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private Movie mMovie;
    private TextView mMovieTitleView;
    private TextView mMovieOverviewView;
    private TextView mMovieReleaseDateView;
    private TextView mMovieRatingView;
    private ImageView mMoviePosterView;
    private List<ImageView> ratingStarViews;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView backDropImage;
    private Button mButtonMarkAsFavorite;
    private Button mButtonRemoveFavorite;
    private Button mButtonWatchTrailer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMovieTitleView = (TextView) rootView.findViewById(R.id.movie_title);
        mMovieOverviewView = (TextView) rootView.findViewById(R.id.movie_overview);
        mMovieReleaseDateView = (TextView) rootView.findViewById(R.id.movie_release_date);
        mMovieRatingView = (TextView) rootView.findViewById(R.id.movie_user_rating);
        mMoviePosterView = (ImageView) rootView.findViewById(R.id.movie_poster);
        ratingStarViews = new ArrayList<>();
        ratingStarViews.add((ImageView) rootView.findViewById(R.id.rating_first_star));
        ratingStarViews.add((ImageView) rootView.findViewById(R.id.rating_second_star));
        ratingStarViews.add((ImageView) rootView.findViewById(R.id.rating_third_star));
        ratingStarViews.add((ImageView) rootView.findViewById(R.id.rating_fourth_star));
        ratingStarViews.add((ImageView) rootView.findViewById(R.id.rating_fifth_star));

        mButtonMarkAsFavorite = (Button) rootView.findViewById(R.id.button_mark_as_favorite);
        mButtonRemoveFavorite = (Button) rootView.findViewById(R.id.button_remove_from_favorites);
        mButtonWatchTrailer = (Button) rootView.findViewById(R.id.button_watch_trailer);

        toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        backDropImage = (ImageView) rootView.findViewById(R.id.movie_backdrop);

        mButtonMarkAsFavorite.setOnClickListener(this);
        mButtonRemoveFavorite.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMovieTitleView.setText(mMovie.getTitle());
        mMovieOverviewView.setText(mMovie.getOverview());
        mMovieReleaseDateView.setText(mMovie.getReleaseDate(getContext()));

        Picasso.with(getContext())
                .load(mMovie.getPosterUrl(getContext()))
                .config(Bitmap.Config.RGB_565)
                .into(mMoviePosterView);

        updateRatingBar();
        updateFavoriteButtons();

        if (toolbarLayout != null) {
            toolbarLayout.setTitle(mMovie.getTitle());
        }

        Picasso.with(getContext())
                .load(mMovie.getBackdropUrl(getContext()))
                .config(Bitmap.Config.RGB_565)
                .into(backDropImage);
    }

    private void updateRatingBar() {
        if (mMovie.getUserRating() != null && !mMovie.getUserRating().isEmpty()) {
            String userRatingStr = getResources().getString(R.string.user_rating_movie,
                    mMovie.getUserRating());
            mMovieRatingView.setText(userRatingStr);

            float userRating = Float.valueOf(mMovie.getUserRating()) / 2;
            int integerPart = (int) userRating;

            for (int i = 0; i < integerPart; i++) {
                ratingStarViews.get(i).setImageResource(R.drawable.ic_star_black_24dp);
            }

            if (Math.round(userRating) > integerPart) {
                ratingStarViews.get(integerPart).setImageResource(R.drawable.ic_star_half_black_24dp);
            }
        } else {
            mMovieRatingView.setVisibility(View.GONE);
        }
    }

    private void updateFavoriteButtons() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return Utility.isFavorite(getActivity(), mMovie.getId());
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    mButtonRemoveFavorite.setVisibility(View.VISIBLE);
                    mButtonMarkAsFavorite.setVisibility(View.GONE);
                } else {
                    mButtonMarkAsFavorite.setVisibility(View.VISIBLE);
                    mButtonRemoveFavorite.setVisibility(View.GONE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void markAsFavorite() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!Utility.isFavorite(getActivity(), mMovie.getId())) {
                    Utility.markAsFavorite(getActivity(), mMovie);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (Utility.isFavorite(getActivity(), mMovie.getId())) {
                    Utility.removeFavorite(getActivity(), mMovie.getId());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButtons();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_mark_as_favorite: {
                markAsFavorite();
                break;
            }
            case R.id.button_remove_from_favorites: {
                removeFromFavorites();
                break;
            }
        }
    }
}
