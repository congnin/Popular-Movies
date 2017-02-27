package com.blablaing.android.popular_movies.network;

import android.database.Observable;

import com.blablaing.android.popular_movies.model.DiscoverAndSearchResponse;
import com.blablaing.android.popular_movies.model.Movie;
import com.blablaing.android.popular_movies.model.Reviews;
import com.blablaing.android.popular_movies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by congnc on 2/26/17.
 */

public interface TheMovieDbService {
    @GET("movie/{id}/videos")
    Call<Trailers> getMovieVideos(@Path("id") long movieId);

    @GET("movie/{id}/reviews")
    Call<Reviews> getMovieReviews(@Path("id") long movieId);

    @GET("discover/movie")
    Call<DiscoverAndSearchResponse<Movie>> discoverMovies(@Query("sort_by") String sortBy,
                                                                @Query("page") Integer page);

    @GET("search/movie")
    Call<DiscoverAndSearchResponse<Movie>> searchMovies(@Query("query") String query,
                                                              @Query("page") Integer page);
}
