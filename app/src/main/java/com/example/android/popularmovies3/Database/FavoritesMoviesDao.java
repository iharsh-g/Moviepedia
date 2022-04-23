package com.example.android.popularmovies3.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoritesMoviesDao {

    @Insert(onConflict = REPLACE)
    void insert(FavoritesMoviesData favoritesMoviesData);

    @Query("SELECT * FROM Movies")
    List<FavoritesMoviesData> getAll();

    @Delete
    void reset(List<FavoritesMoviesData> favoritesMoviesData);

    @Query("SELECT EXISTS(SELECT * FROM Movies WHERE movieId = :movId)")
    boolean isRowIsExist(int movId);

    @Query("DELETE FROM Movies WHERE movieId = :movId")
    void delete(int movId);
}
