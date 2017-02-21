package com.blablaing.android.popular_movies.network;

import com.blablaing.android.popular_movies.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by congnc on 2/21/17.
 */

public interface API {
    @GET("3/movie/{sort_by}")
    Call<Movies> getMoviesBySort(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);
}
