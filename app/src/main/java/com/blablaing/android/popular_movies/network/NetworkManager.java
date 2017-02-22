package com.blablaing.android.popular_movies.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import com.blablaing.android.popular_movies.R;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Movies;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by congnc on 2/21/17.
 */

public class NetworkManager {
    private static Retrofit retrofit = null;
    ApiEndpointInterface apiService;
    private Context mContext;
    private IHttpResponse iHttpResponse;
    private ProgressDialog progressDialog;

    public interface IHttpResponse {
        void onHttpComplete(String response, int idRequest);

        void onHttpError(String response, int idRequest);
    }

    public NetworkManager(Context context, IHttpResponse iHttpResponse) {
        this.iHttpResponse = iHttpResponse;
        this.mContext = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        apiService = getClient((Activity) context).create(ApiEndpointInterface.class);
    }

    public static Retrofit getClient(final Activity activity) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public void requestMoviesApi(Call<Movies> call, final int idRequest, final boolean showErrorMsg) {
        if (call == null) {
            return;
        }

        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Movies movies = response.body();
                if (movies != null) {
                    if (movies.getMovies().size() > 0 || movies.getMovies() != null) {
                        Gson gson = new Gson();
                        iHttpResponse.onHttpComplete(gson.toJson(movies), idRequest);
                    }
                } else {
                    Toast.makeText(mContext, "Something is error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

            }
        });
    }

    public Call<Movies> getMovies(String sortBy, String key){
        return apiService.getMoviesBySort(sortBy, key);
    }
}
