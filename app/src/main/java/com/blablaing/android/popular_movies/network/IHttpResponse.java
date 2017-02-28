package com.blablaing.android.popular_movies.network;

/**
 * Created by congnc on 2/27/17.
 */

public interface IHttpResponse {
    public void onHttpComplete(String response, int idRequest);

    public void onHttpError(String response, int idRequest);
}
