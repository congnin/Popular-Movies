package com.blablaing.android.popular_movies;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class DetailFragment extends Fragment {
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
    private ImageView ratingStar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_MOVIE)) {
            mMovie = getArguments().getParcelable(ARG_MOVIE);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    }
}
