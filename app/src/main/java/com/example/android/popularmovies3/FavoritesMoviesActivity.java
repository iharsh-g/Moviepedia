package com.example.android.popularmovies3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.android.popularmovies3.Adapters.FavoriteMoviesAdapter;
import com.example.android.popularmovies3.DataModels.MainViewModel;
import com.example.android.popularmovies3.Database.FavoritesMoviesData;
import com.example.android.popularmovies3.Database.RoomDB;
import com.example.android.popularmovies3.databinding.ActivityFavoritesMoviesBinding;

import java.util.ArrayList;

public class FavoritesMoviesActivity extends AppCompatActivity implements FavoriteMoviesAdapter.FavoriteMovieItemClickListener {

    private ArrayList<FavoritesMoviesData> mMoviesDataList = new ArrayList<>();
    private RoomDB mDatabase;
    private FavoriteMoviesAdapter mAdapter;
    private ActivityFavoritesMoviesBinding mBinding;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorites_movies);

        mBinding.rvFav.setVisibility(View.GONE);
        mBinding.emptyView.setVisibility(View.GONE);

        mDatabase = RoomDB.getDatabase(this);
        mMoviesDataList = (ArrayList<FavoritesMoviesData>) mDatabase.favoritesMoviesDao().getAll();

        mAdapter = new FavoriteMoviesAdapter(this, this, mMoviesDataList, this);
        mBinding.rvFav.setAdapter(mAdapter);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, getOrientationGridSpans());
        mBinding.rvFav.setLayoutManager(mLayoutManager);

        if(mMoviesDataList.isEmpty()) {
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvFav.setVisibility(View.VISIBLE);
        }

        getSupportActionBar().setTitle("Favorites");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        if(mSharedPreferences.getBoolean("enableAnimations", true)) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
        }
    }

    private int getOrientationGridSpans() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 4;
        else
            return 3;
    }

    @Override
    public void onMovieItemClick(int id, String title, String backdropPath, String ReleaseDate, double voteAverage, ImageView iv) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("backdropPath", backdropPath);
        bundle.putString("releaseDate", ReleaseDate);
        bundle.putDouble("voteAvg", voteAverage);

        Intent intent = new Intent(FavoritesMoviesActivity.this, DetailActivity.class);
        intent.putExtras(bundle);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(FavoritesMoviesActivity.this, iv, ViewCompat.getTransitionName(iv));
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMoviesDataList.clear();
        mMoviesDataList.addAll(mDatabase.favoritesMoviesDao().getAll());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}