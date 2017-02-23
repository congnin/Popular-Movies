package com.blablaing.android.popular_movies.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.blablaing.android.popular_movies.R;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by congnc on 2/21/17.
 */

public class Movie implements Parcelable {
    public static final String LOG_TAG = Movie.class.getSimpleName();
    @SerializedName("id")
    private long mId;

    @SerializedName("genre_ids")
    List<Integer> genreIds = new ArrayList<>();
    @SerializedName("adult")
    private Boolean mAdult;
    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("poster_path")
    private String mPoster;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("vote_average")
    private String mUserRating;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("backdrop_path")
    private String mBackdrop;
    @SerializedName("popularity")
    private Double mPopularity;
    @SerializedName("vote_count")
    private Integer mVoteCount;

    private Movie() {
    }

    public Movie(long id, String title, String poster, String overview, String userRating,
                 String releaseDate, String backdrop) {
        mId = id;
        mTitle = title;
        mPoster = poster;
        mOverview = overview;
        mUserRating = userRating;
        mReleaseDate = releaseDate;
        mBackdrop = backdrop;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getId() {
        return mId;
    }

    public String getPosterUrl(Context context) {
        if (mPoster != null && !mPoster.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_poster) + mPoster;
        }
        // IllegalArgumentException: Path must not be empty. at com.squareup.picasso.Picasso.load.
        // Placeholder/Error/Title will be shown instead of a crash.
        return null;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getReleaseDate(Context context) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
        if (mReleaseDate != null && !mReleaseDate.isEmpty()) {
            try {
                Date date = inputFormat.parse(mReleaseDate);
                return DateFormat.getDateInstance().format(date);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "The Release data was not parsed successfully: " + mReleaseDate);
                // Return not formatted date
            }
        } else {
            mReleaseDate = context.getString(R.string.release_date_missing);
        }

        return mReleaseDate;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getBackdropUrl(Context context) {
        if (mBackdrop != null && !mBackdrop.isEmpty()) {
            return context.getResources().getString(R.string.url_for_downloading_backdrop) +
                    mBackdrop;
        }
        // Placeholder/Error/Title will be shown instead of a crash.
        return null;
    }

    public Boolean getAdult() {
        return mAdult;
    }

    public String getBackdrop() {
        return mBackdrop;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public Integer getVoteCount() {
        return mVoteCount;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setPoster(String mPoster) {
        this.mPoster = mPoster;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public void setUserRating(String mUserRating) {
        this.mUserRating = mUserRating;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public void setBackdrop(String mBackdrop) {
        this.mBackdrop = mBackdrop;
    }

    public void setPopularity(Double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public void setVoteCount(Integer mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public void setAdult(Boolean mAdult) {
        this.mAdult = mAdult;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeList(this.genreIds);
        dest.writeValue(this.mAdult);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPoster);
        dest.writeString(this.mOverview);
        dest.writeString(this.mUserRating);
        dest.writeString(this.mReleaseDate);
        dest.writeString(this.mBackdrop);
        dest.writeValue(this.mPopularity);
        dest.writeValue(this.mVoteCount);
    }

    protected Movie(Parcel in) {
        this.mId = in.readLong();
        this.genreIds = new ArrayList<Integer>();
        in.readList(this.genreIds, Integer.class.getClassLoader());
        this.mAdult = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mTitle = in.readString();
        this.mPoster = in.readString();
        this.mOverview = in.readString();
        this.mUserRating = in.readString();
        this.mReleaseDate = in.readString();
        this.mBackdrop = in.readString();
        this.mPopularity = (Double) in.readValue(Double.class.getClassLoader());
        this.mVoteCount = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
