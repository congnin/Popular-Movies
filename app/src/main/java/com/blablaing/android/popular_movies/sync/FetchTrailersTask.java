package com.blablaing.android.popular_movies.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.blablaing.android.popular_movies.BuildConfig;
import com.blablaing.android.popular_movies.model.Trailer;
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
 * Created by congnc on 2/23/17.
 */

public class FetchTrailersTask extends AsyncTask<Long, Void, List<Trailer>> {
    public static String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final Callback mCallback;

    public interface Callback {
        void onFetchFinished(List<Trailer> trailers);
    }

    public FetchTrailersTask(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected List<Trailer> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }
        long movieId = params[0];
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiEndpointInterface service = retrofit.create(ApiEndpointInterface.class);
        Call<Trailers> call = service.findTrailersById(movieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mCallback.onFetchFinished(trailers);
        } else {
            mCallback.onFetchFinished(new ArrayList<Trailer>());
        }
    }
}
