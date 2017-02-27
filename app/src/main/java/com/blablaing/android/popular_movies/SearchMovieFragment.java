package com.blablaing.android.popular_movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blablaing.android.popular_movies.adapter.MovieListAdapter;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.sync.FetchMovieTask;
import com.blablaing.android.popular_movies.sync.FetchSearchTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/24/17.
 */

public class SearchMovieFragment extends Fragment implements MovieListAdapter.Callbacks,
        FetchSearchTask.Callback, View.OnClickListener {
    private ImageView mBackImageView;
    private EditText mSearchTextView;
    private RecyclerView mRecyclerViewListSearch;
    private MovieListAdapter movieListAdapter;
    private ProgressBar progressBar;
    private FetchSearchTask fetchSearchTask;
    private boolean isFinished = true;
    public static final String EXTRA_SEARCH_MOVIES = "EXTRA_SEARCH_MOVIES";
    public static final String SearchMovieFragmentTag = "SearchMovieFragment";
    public static final String FINISHED_LOADED = "finished";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mBackImageView = (ImageView) rootView.findViewById(R.id.back_img);
        mSearchTextView = (EditText) rootView.findViewById(R.id.search_input);
        mRecyclerViewListSearch = (RecyclerView) rootView.findViewById(R.id.recyclerview_movie);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        movieListAdapter = new MovieListAdapter(new ArrayList<Movie>(), this);
        mRecyclerViewListSearch.setLayoutManager(new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.grid_number_cols)));
        mRecyclerViewListSearch.setAdapter(movieListAdapter);

        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.length() == 0) {
                            movieListAdapter.clear();
                        } else {
                            fetchSearchMovies(s.toString());
                        }
                    }
                }, 2000);
            }
        });
        mBackImageView.setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_SEARCH_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_SEARCH_MOVIES);
                movieListAdapter.add(movies);
                progressBar.setVisibility(View.GONE);
            }
            if (savedInstanceState.containsKey(FINISHED_LOADED)) {
                isFinished = savedInstanceState.getBoolean(FINISHED_LOADED);
            }
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = movieListAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_SEARCH_MOVIES, movies);
        }
        outState.putBoolean(FINISHED_LOADED, isFinished);
    }

    private void fetchSearchMovies(String query) {
        progressBar.setVisibility(View.VISIBLE);
        fetchSearchTask = new FetchSearchTask(this);
        fetchSearchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
    }

    @Override
    public void open(Movie movie, int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailFragment.ARG_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onFetchSearchFinish(List<Movie> movies) {
        isFinished = true;
        progressBar.setVisibility(View.GONE);
        movieListAdapter.add(movies);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_img) {
            getActivity().getSupportFragmentManager().popBackStack(SearchMovieFragmentTag, 1);
        }

    }
}
