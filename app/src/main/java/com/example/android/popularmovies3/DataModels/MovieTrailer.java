package com.example.android.popularmovies3.DataModels;

public class MovieTrailer {

    private String mId;
    private String m_iso_3166_1;
    private String m_iso_639_1;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize = 0;
    private String mType;

    public MovieTrailer(String id, String iso_3166_1, String iso_639_1, String key, String name, String site, int size, String type) {
        this.mId = id;
        this.m_iso_3166_1 = iso_3166_1;
        this.m_iso_639_1 = iso_639_1;
        this.mKey = key;
        this.mName = name;
        this.mSite = site;
        this.mSize = size;
        this.mType = type;
    }

    public String getId() {
        return mId;
    }

    public String getIso_3166_1() {
        return m_iso_3166_1;
    }

    public String getIso_639_1() {
        return m_iso_639_1;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getSite() {
        return mSite;
    }

    public int getSize() {
        return mSize;
    }

    public String getType() {
        return mType;
    }
}
