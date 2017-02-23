package com.blablaing.android.popular_movies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blablaing.android.popular_movies.adapter.ReviewAdapter;
import com.blablaing.android.popular_movies.adapter.TrailerAdapter;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Review;
import com.blablaing.android.popular_movies.model.Trailer;
import com.blablaing.android.popular_movies.sync.FetchReviewsTask;
import com.blablaing.android.popular_movies.sync.FetchTrailersTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by congnc on 2/22/17.
 */

public class DetailFragment extends Fragment implements View.OnClickListener, TrailerAdapter.Callbacks,
        ReviewAdapter.Callbacks, FetchTrailersTask.Callback, FetchReviewsTask.Listener {
    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    private TrailerAdapter mTrailerListAdapter;
    private ReviewAdapter mReviewListAdapter;
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
    private RecyclerView mRecyclerViewForTrailers;
    private RecyclerView mRecyclerViewForReviews;

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

        mRecyclerViewForTrailers = (RecyclerView) rootView.findViewById(R.id.trailer_list);
        mRecyclerViewForReviews = (RecyclerView) rootView.findViewById(R.id.review_list);

        toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.toolbar_layout);
        backDropImage = (ImageView) rootView.findViewById(R.id.movie_backdrop);

        mButtonMarkAsFavorite.setOnClickListener(this);
        mButtonRemoveFavorite.setOnClickListener(this);
        mButtonWatchTrailer.setOnClickListener(this);
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

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewForTrailers.setLayoutManager(layoutManager);
        mTrailerListAdapter = new TrailerAdapter(new ArrayList<Trailer>(), this);
        mRecyclerViewForTrailers.setAdapter(mTrailerListAdapter);
        mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

        mReviewListAdapter = new ReviewAdapter(new ArrayList<Review>(), this);
        mRecyclerViewForReviews.setAdapter(mReviewListAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
            List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
            mTrailerListAdapter.add(trailers);
            mButtonWatchTrailer.setEnabled(true);
        } else {
            fetchTrailers();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
            List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
            mReviewListAdapter.add(reviews);
        } else {
            fetchReviews();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_fragment, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
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
            case R.id.button_watch_trailer: {
                if (mTrailerListAdapter.getItemCount() > 0) {
                    watch(mTrailerListAdapter.getTrailers().get(0), 0);
                }
                break;
            }
        }
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));
    }

    private void fetchTrailers() {
        FetchTrailersTask task = new FetchTrailersTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    private void fetchReviews() {
        FetchReviewsTask task = new FetchReviewsTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mMovie.getId());
    }

    @Override
    public void onReviewsFetchFinished(List<Review> reviews) {
        mReviewListAdapter.add(reviews);
    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {
        mTrailerListAdapter.add(trailers);
        mButtonWatchTrailer.setEnabled(!trailers.isEmpty());

        if (mTrailerListAdapter.getItemCount() > 0) {
            Trailer trailer = mTrailerListAdapter.getTrailers().get(0);
            updateShareActionProvider(trailer);
        }
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + ": "
                + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }
}
