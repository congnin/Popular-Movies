package com.blablaing.android.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blablaing.android.popular_movies.adapter.MovieListAdapter;
import com.blablaing.android.popular_movies.data.MovieContract;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.MoviesResponse;
import com.blablaing.android.popular_movies.network.Command;
import com.blablaing.android.popular_movies.network.IHttpResponse;
import com.blablaing.android.popular_movies.network.NetworkManager;
import com.blablaing.android.popular_movies.sync.FetchMovieTask;
import com.blablaing.android.popular_movies.ui.RecycleViewLoadMore;
import com.blablaing.android.popular_movies.ui.RecyclerView.CustomLinearLayoutManager;
import com.blablaing.android.popular_movies.ui.RecyclerView.OnLoadMoreListener;
import com.blablaing.android.popular_movies.ui.RecyclerView.RecyclerRefreshLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListFragment extends Fragment implements
        MovieListAdapter.Callbacks, LoaderManager.LoaderCallbacks<Cursor>, IHttpResponse,
        OnLoadMoreListener, RecyclerRefreshLayout.OnRefreshListener {
    private NetworkManager networkManager;
    private List<Movie> movies = new ArrayList<>();
    private MoviesResponse movieModel;
    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    private static final String EXTRA_SORT_BY = "EXTRA_SORT_BY";
    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";
    private static final int ID_MOVIES = 1;
    private static final int ID_LOAD_MORE = 2;
    private static final int FAVORITE_MOVIES_LOADER = 0;
    private Toolbar toolbar;
    private MovieListAdapter movieAdapter;
    private RecyclerRefreshLayout mRecyclerRefreshLayout;
    private RecycleViewLoadMore mRecyclerView;
    private CustomLinearLayoutManager linearLayoutManager;
    private String mSortBy = FetchMovieTask.MOST_POPULAR;
    private View emptyViewConnection;
    private View emptyViewFavorites;
    private ProgressBar progressBar;
    private boolean mPaused = false;
    private Command mWaitingCommand = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        networkManager = new NetworkManager(context, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecycleViewLoadMore) rootView.findViewById(R.id.recyclerview_movie);
        mRecyclerRefreshLayout = (RecyclerRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        emptyViewConnection = rootView.findViewById(R.id.empty_state_container);
        emptyViewFavorites = rootView.findViewById(R.id.empty_state_favorites_container);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        toolbar.setTitle(R.string.title_movie_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.grid_number_cols)));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.initLoadMore();
        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerRefreshLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.PINNED);
        mRecyclerRefreshLayout.setRefreshInitialOffset(Utility.dip2px(getActivity(), 10));
        mRecyclerRefreshLayout.setNestedScrollingEnabled(true);
        mRecyclerRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLoadMore(false);
        movieAdapter = new MovieListAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(movieAdapter);

        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getString(EXTRA_SORT_BY);
            if (savedInstanceState.containsKey(EXTRA_MOVIES)) {
                List<Movie> movies = savedInstanceState.getParcelableArrayList(EXTRA_MOVIES);
                movieAdapter.add(movies);
                progressBar.setVisibility(View.GONE);

                if (mSortBy.equals(FetchMovieTask.FAVORITES)) {
                    getActivity().getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
                }
            }
            updateEmptyState();
        } else {
            //fetchMovies(mSortBy);
            requestMovies(mSortBy, ID_MOVIES);
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

        if (!mSortBy.equals(FetchMovieTask.FAVORITES)) {
            getActivity().getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
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
                if (mSortBy.equals(FetchMovieTask.FAVORITES)) {
                    getActivity().getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = MOST_POPULAR;
                //fetchMovies(mSortBy);
                requestMovies(MOST_POPULAR, ID_MOVIES);
                item.setChecked(true);
                break;
            case R.id.sort_by_top_rated:
                if (mSortBy.equals(FetchMovieTask.FAVORITES)) {
                    getActivity().getSupportLoaderManager().destroyLoader(FAVORITE_MOVIES_LOADER);
                }
                mSortBy = TOP_RATED;
                //fetchMovies(mSortBy);
                requestMovies(TOP_RATED, ID_MOVIES);
                item.setChecked(true);
                break;
            case R.id.sort_by_favorites:
                mSortBy = FetchMovieTask.FAVORITES;
                item.setChecked(true);
                fetchMovies(mSortBy);
                break;
            case R.id.action_search:
                openSearch();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies(String sortBy) {
        getActivity().getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);
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

    private void openSearch() {
        SearchMovieFragment fragment = new SearchMovieFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.movie_search_container, fragment, SearchMovieFragment.SearchMovieFragmentTag);
        transaction.addToBackStack(SearchMovieFragment.SearchMovieFragmentTag);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.addCursor(data);
        updateEmptyState();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onHttpComplete(String response, int idRequest) {
        Gson gson = new Gson();
        switch (idRequest) {
            case ID_MOVIES: {
                movieModel = gson.fromJson(response, MoviesResponse.class);
                if (movieModel != null) {
                    handleResponse();
                }
                mRecyclerRefreshLayout.setRefreshing(false);
                break;
            }
            case ID_LOAD_MORE: {
                stopLoadMore();
                MoviesResponse model = gson.fromJson(response, MoviesResponse.class);
                if (model != null) {
                    movieModel.setPage(movieModel.getPage() + 1);
                    if (model.getResults() != null && model.getResults().size() > 0) {
                        for (int i = 0; i < model.getResults().size(); i++) {
                            if (!movieModel.getResults().contains(model.getResults().get(i))) {
                                movieModel.getResults().add(model.getResults().get(i));
                            }
                        }
                        handleResponse();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onHttpError(String response, int idRequest) {

    }

    private void handleResponse() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (movieModel != null && movieModel.getResults() != null
                            && movieModel.getResults().size() > 0) {
                        if (movies == null || movies.size() == 0) {
                            movies = movieModel.getResults();
                        } else {
                            for (int i = 0; i < movieModel.getResults().size(); i++) {
                                if (!movies.contains(movieModel.getResults().get(i))) {
                                    movies.add(movieModel.getResults().get(i));
                                }
                            }
                        }
                        movieAdapter.add(movies);
                    }
                    mRecyclerView.setLoadMore(true);
                }
            });
        }
    }

    private void stopLoadMore() {
        if (movies != null && movies.size() > 0
                && movies.get(movies.size() - 1) == null) {
            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    movieAdapter.notifyItemInserted(movies.size() - 1);
                }
            };
            handler.post(r);
        }
        mRecyclerView.setLoadMore(true);
    }

    @Override
    public void onLoadMore() {
        if (movieModel != null && movieModel.getPage() < movieModel.getTotalPages()
                && movies != null) {
            movieAdapter.clear();
            requestMovies(mSortBy, ID_LOAD_MORE);
        }
    }

    @Override
    public void onRefresh() {

    }

    private void requestMovies(String sortBy, int idRequest) {
        int page = 1;
        if (idRequest == ID_LOAD_MORE) {
            if (movieModel != null && movieModel.getResults().size() > 0) {
                page = movieModel.getPage() + 1;
            }
        } else {
            movies.clear();
        }
        mRecyclerView.setLoadMore(false);
        networkManager.requestApi(networkManager.getMoviesBySort(sortBy, page), idRequest, true);
    }
}
