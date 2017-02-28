package com.blablaing.android.popular_movies.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import com.blablaing.android.popular_movies.R;
import com.blablaing.android.popular_movies.model.DiscoverAndSearchResponse;
import com.blablaing.android.popular_movies.model.MoviesResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by congnc on 2/27/17.
 */

public class NetworkManager {
    private static Retrofit retrofit = null;
    TheMovieDbService apiService;
    private Context mContext;
    private IHttpResponse iHttpResponse;
    private ProgressDialog progressDialog;

    public NetworkManager(Context context, IHttpResponse iHttpResponse) {
        this.iHttpResponse = iHttpResponse;
        this.mContext = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        apiService = RetrofitClient.providesRetrofit().create(TheMovieDbService.class);
    }

    public void requestApi(Call<MoviesResponse> call, final int idRequest, final boolean showErrorMsg) {
        if (call == null) {
            return;
        }
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                MoviesResponse responseModel = response.body();
                Gson gson = new Gson();
                if (responseModel != null) {
                    iHttpResponse.onHttpComplete(gson.toJson(responseModel), idRequest);
                } else {
                    iHttpResponse.onHttpError(mContext.getString(R.string.text_for_empty_view), idRequest);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.text_for_empty_view), Toast.LENGTH_SHORT).show();
                iHttpResponse.onHttpError("", idRequest);
            }
        });
    }

    public Call<MoviesResponse> getDiscoverMovies(String sortBy, Integer page) {
        return apiService.discoverMovies(sortBy, page);
    }

    public Call<MoviesResponse> getMoviesBySort(String sortBy, Integer page){
        return apiService.getMoviesBySort(sortBy, page);
    }
}
