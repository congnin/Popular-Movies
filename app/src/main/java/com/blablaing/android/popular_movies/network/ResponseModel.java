package com.blablaing.android.popular_movies.network;

import com.google.gson.JsonElement;

/**
 * Created by congnc on 2/22/17.
 */

public class ResponseModel {
    int status_code;
    String status_message;
    JsonElement results;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public JsonElement getResults() {
        return results;
    }

    public void setResults(JsonElement results) {
        this.results = results;
    }
}
