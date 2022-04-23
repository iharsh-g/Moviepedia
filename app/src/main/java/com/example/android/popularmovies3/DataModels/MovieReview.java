package com.example.android.popularmovies3.DataModels;
public class MovieReview {
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public MovieReview(String id, String author, String content, String url) {
        this.mId = id;
        this.mAuthor = author;
        this.mContent = content;
        this.mUrl = url;
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getUrl() {
        return mUrl;
    }
}

