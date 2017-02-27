package com.blablaing.android.popular_movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;

import com.blablaing.android.popular_movies.data.MovieContract;
import com.blablaing.android.popular_movies.model.Movie;

import java.util.List;

/**
 * Created by congnc on 2/22/17.
 */

public class Utility {

    public static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }

    public static float dip2px(Context context, float dpValue) {
        if (context != null) {
            float scale = context.getResources().getDisplayMetrics().density;
            return dpValue * scale;
        }
        return 0;
    }

    public static int changeAdultToInt(Boolean adult) {
        if (adult)
            return 1;
        else
            return 0;
    }

    public static String changeGenreToString(List<Integer> integers) {
        String result = "";
        for (int i = 0; i < integers.size(); i++) {
            result += Integer.toString(integers.get(i));
        }
        return result;
    }

    public static boolean isFavorite(Context context, long id) {
        Cursor movieCursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id,
                null,
                null);
        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

    public static void markAsFavorite(Context context, Movie movie) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                movie.getId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                movie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                movie.getPoster());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                movie.getOverview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                movie.getUserRating());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                movie.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                movie.getBackdrop());
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ADULT,
               changeAdultToInt(movie.getAdult()));
        movieValues.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS,
                changeGenreToString(movie.getGenreIds()));
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,
                movie.getPopularity());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
                movie.getVoteCount());
        context.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI,
                movieValues
        );
    }

    public static void removeFavorite(Context context, long id) {
        context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id, null);
    }
}
