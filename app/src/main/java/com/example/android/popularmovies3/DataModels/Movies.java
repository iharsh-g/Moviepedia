package com.example.android.popularmovies3.DataModels;
public class Movies {

    private boolean mAdult;
    private int mId;
    private double mVoteAverage;
    private String mTitle;
    private double mPopularity;
    private String mPosterPath, mOriginalLanguage, mBackdropPath;
    private String mOverview;
    private String mReleaseDate;

    public Movies( boolean adult, String backdropPath, int id, String originalLanguage, String title,
                   String overview, double popularity, String posterPath, String releaseDate, double voteAverage)
    {
        this.mAdult = adult; this.mBackdropPath = backdropPath; this.mId = id; this.mOriginalLanguage = originalLanguage;
        this.mTitle = title; this.mOverview = overview; this.mPopularity = popularity; this.mPosterPath = posterPath;
        this.mReleaseDate = releaseDate; this.mVoteAverage = voteAverage;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getRating() {
        return mVoteAverage/2;
    }

}

