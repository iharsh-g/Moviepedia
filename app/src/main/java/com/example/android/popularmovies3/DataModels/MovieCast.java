package com.example.android.popularmovies3.DataModels;

public class MovieCast {

    private String mId, mName, mImage, mCharacter;

    public MovieCast(String mId, String mName, String mImage, String mCharacter) {
        this.mId = mId;
        this.mName = mName;
        this.mImage = mImage;
        this.mCharacter = mCharacter;
    }

    public String getMovieCastId() {
        return mId;
    }

    public String getMovieCastName() {
        return mName;
    }

    public String getMovieCastImage() {
        return mImage;
    }

    public String getMovieCastCharacter() {
        return mCharacter;
    }
}
