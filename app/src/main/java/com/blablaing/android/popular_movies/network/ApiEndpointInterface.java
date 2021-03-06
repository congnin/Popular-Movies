package com.blablaing.android.popular_movies.network;

import com.blablaing.android.popular_movies.model.Movies;
import com.blablaing.android.popular_movies.model.Reviews;
import com.blablaing.android.popular_movies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by congnc on 2/21/17.
 */

public interface ApiEndpointInterface {
    @GET("3/movie/{sort_by}")
    Call<Movies> getMoviesBySort(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<Trailers> findTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Reviews> findReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/search/movie")
    Call<Movies> getMoviesBySearch(@Query("api_key") String apiKey, @Query("query") String query, @Query("page") long page);
}
