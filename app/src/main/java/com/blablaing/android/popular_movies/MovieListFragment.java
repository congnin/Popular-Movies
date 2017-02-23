package com.blablaing.android.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.network.Command;
import com.blablaing.android.popular_movies.sync.FetchMovieTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListFragment extends Fragment implements FetchMovieTask.CallbackLoadMovie,
        MovieListAdapter.Callbacks {

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    private MovieListAdapter movieAdapter;
    private RecyclerView mRecyclerView;
    private String mSortBy = FetchMovieTask.MOST_POPULAR;
    private View emptyViewConnection;
    private View emptyViewFavorites;
    private ProgressBar progressBar;
    private boolean mPaused = false;
    private Command mWaitingCommand = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movie);
        emptyViewConnection = rootView.findViewById(R.id.empty_state_container);
        emptyViewFavorites = rootView.findViewById(R.id.empty_state_favorites_container);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        movieAdapter = new MovieListAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(movieAdapter);

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                movieAdapter.add(movies);
                progressBar.setVisibility(View.GONE);
            }
            updateEmptyState();
        } else {
            fetchMovies(mSortBy);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> movies = movieAdapter.getMovies();
        if (movies != null && !movies.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_MOVIES, movies);
        }
        outState.putString(EXTRA_SORT_BY, mSortBy);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
        if (mWaitingCommand != null) {
            onFetchFinished(mWaitingCommand);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list_activity, menu);
        switch (mSortBy) {
            case FetchMovieTask.MOST_POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;
            case FetchMovieTask.TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case FetchMovieTask.FAVORITES:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_most_popular:
                mSortBy = FetchMovieTask.MOST_POPULAR;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
            case R.id.sort_by_top_rated:
                mSortBy = FetchMovieTask.TOP_RATED;
                fetchMovies(mSortBy);
                item.setChecked(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies(String sortBy) {
        if (!sortBy.equals(FetchMovieTask.FAVORITES)) {
            progressBar.setVisibility(View.VISIBLE);
            FetchMovieTask.NotifyAboutTaskCompletionCommand command =
                    new FetchMovieTask.NotifyAboutTaskCompletionCommand(this);
            new FetchMovieTask(sortBy, command).execute();
        }
    }

    private void updateEmptyState() {
        if (movieAdapter.getItemCount() == 0) {
            if (mSortBy.equals(FetchMovieTask.FAVORITES)) {
                emptyViewConnection.setVisibility(View.GONE);
                emptyViewFavorites.setVisibility(View.VISIBLE);
            } else {
                emptyViewConnection.setVisibility(View.VISIBLE);
                emptyViewFavorites.setVisibility(View.GONE);
            }
        } else {
            emptyViewConnection.setVisibility(View.GONE);
            emptyViewFavorites.setVisibility(View.GONE);
        }
    }


    @Override
    public void open(Movie movie, int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailFragment.ARG_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void onFetchFinished(Command command) {
        if (command instanceof FetchMovieTask.CallbackLoadMovie && !mPaused) {
            FetchMovieTask.CallbackLoadMovie listener = (FetchMovieTask.CallbackLoadMovie) getActivity();
            listener.onFetchFinished(command);
            mWaitingCommand = null;
        } else {
            mWaitingCommand = command;
        }
        if (command instanceof FetchMovieTask.NotifyAboutTaskCompletionCommand) {
            movieAdapter.add(((FetchMovieTask.NotifyAboutTaskCompletionCommand) command).getMovies());
            updateEmptyState();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchError(Command command) {

    }
}
