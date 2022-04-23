package com.example.android.popularmovies3.DataModels;

import java.util.ArrayList;

public class MovieDetails {

    private double mVoteAverage;
    private String mTitle, mTagline, mPosterPath, mOverview, mReleaseDate, mRevenue, mBudget, mGenres;
    private int mRuntime;
    private ArrayList<MovieCast> mMovieCastList;

    public MovieDetails(double mVoteAverage, String mTitle, String mTagline,
                        String mPosterPath, String mOverview, String mReleaseDate,
                        String mRevenue, String mBudget, String mGenres, int mRuntime,
                        ArrayList<MovieCast> mMovieCastList)
    {
        this.mVoteAverage = mVoteAverage;
        this.mTitle = mTitle;
        this.mTagline = mTagline;
        this.mPosterPath = mPosterPath;
        this.mOverview = mOverview;
        this.mReleaseDate = mReleaseDate;
        this.mRevenue = mRevenue;
        this.mBudget = mBudget;
        this.mRuntime = mRuntime;
        this.mMovieCastList = mMovieCastList;
        this.mGenres = mGenres;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getRating() {
        return mVoteAverage;
    }

    public String getTagline() {
        return mTagline;
    }

    public String getRevenue() {
        return mRevenue;
    }

    public String getBudget() {
        return mBudget;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public ArrayList<MovieCast> getMovieCastList() {
        return mMovieCastList;
    }

    public String getGenres() {
        return mGenres;
    }
}
