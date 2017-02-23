package com.blablaing.android.popular_movies.sync;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import com.blablaing.android.popular_movies.BuildConfig;
import com.blablaing.android.popular_movies.R;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Movies;
import com.blablaing.android.popular_movies.network.ApiEndpointInterface;
import com.blablaing.android.popular_movies.network.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by congnc on 2/22/17.
 */

public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";

    public final NotifyAboutTaskCompletionCommand mCommand;

    public static class NotifyAboutTaskCompletionCommand implements Command {
        private FetchMovieTask.CallbackLoadMovie mCallback;
        private List<Movie> movies;

        public NotifyAboutTaskCompletionCommand(CallbackLoadMovie callback) {
            mCallback = callback;
        }

        @Override
        public void execute() {
            mCallback.onFetchFinished(this);
        }

        public List<Movie> getMovies() {
            return movies;
        }
    }

    public FetchMovieTask(@SORT_BY String sortBy, NotifyAboutTaskCompletionCommand command) {
        mSortBy = sortBy;
        mCommand = command;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiEndpointInterface service = retrofit.create(ApiEndpointInterface.class);
        Call<Movies> call = service.getMoviesBySort(mSortBy,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
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
            mCommand.movies = movies;
        } else {
            mCommand.movies = new ArrayList<>();
        }
        mCommand.execute();
    }

    @StringDef({MOST_POPULAR, TOP_RATED, FAVORITES})
    public @interface SORT_BY {
    }

    private
    @SORT_BY
    String mSortBy = MOST_POPULAR;

    public interface CallbackLoadMovie {
        void onFetchFinished(Command command);

        void onFetchError(Command command);
    }


}
