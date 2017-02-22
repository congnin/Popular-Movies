package com.blablaing.android.popular_movies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Movies;
import com.blablaing.android.popular_movies.network.NetworkManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by congnc on 2/21/17.
 */

public class MovieListFragment extends Fragment implements MovieListAdapter.Callbacks, NetworkManager.IHttpResponse {
    private MovieListAdapter movieListAdapter;
    private NetworkManager networkManager;
    private ArrayList<Movie> movies;
    private Movies movieRes;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        networkManager = new NetworkManager(context, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movie);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        movieListAdapter = new MovieListAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(movieListAdapter);
        if (movies == null) {
            getMovies();
        }
    }

    @Override
    public void open(Movie movie, int position) {

    }

    @Override
    public void onHttpComplete(String response, int idRequest) {
        Gson gson = new Gson();
        movieRes = gson.fromJson(response, Movies.class);
        if (movieRes.getMovies() != null || movieRes.getMovies().size() > 0) {
            if (movies == null) {
                movies = new ArrayList<>();
            }
            movies.clear();
            movies.addAll(movieRes.getMovies());
            handleResponse();
        }
    }

    @Override
    public void onHttpError(String response, int idRequest) {

    }

    void getMovies() {
        networkManager.requestMoviesApi(networkManager.getMovies("popular", "3cc91d496a0b7cf338fafae2c567ffd7"), 1, true);
    }

    void handleResponse() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    movieListAdapter.add(movieRes.getMovies());
                }
            });
        }
    }
}
