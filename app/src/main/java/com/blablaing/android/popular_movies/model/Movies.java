package com.blablaing.android.popular_movies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by congnc on 2/21/17.
 */

public class Movies {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();
    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private long totalResults;

    public List<Movie> getMovies() {
        return movies;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }
}
