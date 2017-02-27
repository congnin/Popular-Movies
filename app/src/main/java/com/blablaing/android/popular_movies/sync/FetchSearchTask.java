package com.blablaing.android.popular_movies.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.blablaing.android.popular_movies.BuildConfig;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Movies;
import com.blablaing.android.popular_movies.model.Trailers;
import com.blablaing.android.popular_movies.network.ApiEndpointInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by congnc on 2/24/17.
 */

public class FetchSearchTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchSearchTask.class.getSimpleName();
    private Callback mCallback;

    public FetchSearchTask(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onFetchSearchFinish(List<Movie> movies);
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        String query = params[0];
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiEndpointInterface service = retrofit.create(ApiEndpointInterface.class);
        Call<Movies> call = service.getMoviesBySearch(
                BuildConfig.THE_MOVIE_DATABASE_API_KEY, query, 1);
        try {
            Response<Movies> response = call.execute();
            Movies movies = response.body();
            return movies.getMovies();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if (movies != null) {
            mCallback.onFetchSearchFinish(movies);
        } else {
            mCallback.onFetchSearchFinish(new ArrayList<Movie>());
        }
    }
}
