package com.example.android.popularmovies3.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Movies")
public class FavoritesMoviesData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int Id;

    @ColumnInfo(name = "movieId")
    private int movieId;

    @ColumnInfo(name = "poster")
    private String poster;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "voteAverage")
    private double voteAverage;

    @ColumnInfo(name = "releaseDate")
    private String releaseDate;

    public int getId() {
        return Id;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getPoster() {
        return poster;
    }

    public String getName() {
        return name;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
