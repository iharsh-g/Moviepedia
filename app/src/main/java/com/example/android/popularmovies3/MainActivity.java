package com.example.android.popularmovies3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies3.Adapters.MoviesAdapter;
import com.example.android.popularmovies3.DataModels.Movies;
import com.example.android.popularmovies3.Utils.NetworkUtils;
import com.example.android.popularmovies3.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MovieItemClickListener, SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private MoviesAdapter mAdapter;
    private RequestQueue mQueue;
    private ActivityMainBinding mBinding;
    private ArrayList<Movies> mMoviesList;

    private int page = 1;
    private final int limit = 100;

    private static final int POPULAR_MOVIES = 1;
    private static final int TOP_RATED_MOVIES = 2;
    private static final int UPCOMING_MOVIES = 3;
    private static int fetchState = POPULAR_MOVIES;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        boolean aBoolean = mSharedPreferences.getBoolean("nightMode", false);
        if (!aBoolean) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        /* ---------------- If the user open the app first time ---------------------- */
        if (mSharedPreferences.getBoolean("howToUse", true)) {
            startActivity(new Intent(MainActivity.this, MainIntro.class));
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("howToUse", false);
            editor.apply();
        }

        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /* When any preference changes */
        aBoolean = mSharedPreferences.getBoolean("nightMode", false);
        if (aBoolean) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            MakeDialog();
        }

        mMoviesList = new ArrayList<>();

        // if no API KEY is defined in the strings.xml, notify about it and close the app
        if (getString(R.string.network_api_key).trim().equals("")) {
            makeBadApiKeyDialog(getString(R.string.main_no_api_key));
        }

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, getOrientationGridSpans());
        mBinding.mainRvMoviesList.setLayoutManager(mLayoutManager);
        mAdapter = new MoviesAdapter(this, mMoviesList, this);
        mBinding.mainRvMoviesList.setAdapter(mAdapter);

        mBinding.mainLayoutSwipe.setOnRefreshListener(this);
        mQueue = Volley.newRequestQueue(this);

        mBinding.mainRvMoviesList.setVisibility(View.GONE);
        mBinding.mainPb.setVisibility(View.VISIBLE);

        mBinding.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    mBinding.mainPb.setVisibility(View.VISIBLE);
                    jsonParse(fetchState);
                }

                if (scrollY == 0) {
                    mBinding.mainFab.setVisibility(View.GONE);
                }

                if (scrollY > oldScrollY) { //User going down
                    mBinding.mainFab.setVisibility(View.VISIBLE);
                } else { //User going up
                    mBinding.mainFab.setVisibility(View.GONE);
                }

            }
        });

        mBinding.mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.scrollView.scrollTo(0,0);
                mBinding.mainFab.setVisibility(View.GONE);
            }
        });

        jsonParse(fetchState);

        setSupportActionBar(mBinding.mainToolbar);

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mBinding.drawer, mBinding.mainToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mBinding.drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mBinding.navView.bringToFront();
        mBinding.navView.setCheckedItem(R.id.menu_popular);
        mBinding.navView.setNavigationItemSelectedListener(this);

        RelativeLayout rl = mBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.themoviedb.org/login"));
                startActivity(intent);
            }
        });
    }

    private void MakeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                    MakeDialog();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private int getOrientationGridSpans() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 4;
        else
            return 3;
    }

    void jsonParse(int fetchState) {
        if (page > limit) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            mBinding.mainPb.setVisibility(View.GONE);
            return;
        }

        String s;
        if (fetchState == POPULAR_MOVIES) {
            s = "popular";
            mBinding.mainToolbar.setSubtitle("Trending");
        }
        else if (fetchState == TOP_RATED_MOVIES) {
            s = "top_rated";
            mBinding.mainToolbar.setSubtitle("Top rated");
        }
        else {
            s = "upcoming";
            mBinding.mainToolbar.setSubtitle("Upcoming");
        }

        String url = "https://api.themoviedb.org/3/movie/" + s + "?api_key=eb77a6e7061b90a30a6a28502f197d45&page=" + page;
        Log.e("MainActivity", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray moviesJsonJSONArray = response.getJSONArray("results");
                            for (int i = 0; i < moviesJsonJSONArray.length(); i++) {
                                JSONObject movie = (JSONObject) moviesJsonJSONArray.get(i);

                                boolean adult = movie.optBoolean("adult");
                                String backDropPath = movie.getString("backdrop_path");
                                int id = movie.getInt("id");
                                String lang = movie.getString("original_language");
                                String title = movie.getString("title");
                                String overview = movie.getString("overview");
                                double popularity = movie.getDouble("popularity");
                                String posterPath = movie.getString("poster_path");
                                String releaseDate = movie.getString("release_date");
                                double vote = movie.getDouble("vote_average");

                                mBinding.mainRvMoviesList.setVisibility(View.VISIBLE);
                                mBinding.mainPb.setVisibility(View.GONE);
                                mBinding.mainLayoutSwipe.setRefreshing(false);

                                Movies movies = new Movies(adult, backDropPath, id, lang, title, overview, popularity, posterPath, releaseDate, vote);
                                mMoviesList.add(movies);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter = new MoviesAdapter(MainActivity.this, mMoviesList, MainActivity.this);
                        mBinding.mainRvMoviesList.setAdapter(mAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    @Override
    public void onMovieItemClick(int id, String title, String backdropPath, String ReleaseDate, double voteAverage, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("backdropPath", backdropPath);
        bundle.putString("releaseDate", ReleaseDate);
        bundle.putDouble("voteAvg", voteAverage);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtras(bundle);

        if (mSharedPreferences.getBoolean("enableAnimations", true)) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, ViewCompat.getTransitionName(imageView));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void makeBadApiKeyDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.main_no_api_key_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            startActivity(new Intent(MainActivity.this, SearchMovieActivity.class));
            if (mSharedPreferences.getBoolean("enableAnimations", true)) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
        return true;
    }

    private void animateNavDrawer() {
        mBinding.drawer.setScrimColor(getResources().getColor(R.color.black));
        mBinding.drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - 0.7f);
                final float offSetScale = 1 - diffScaledOffset;
                mBinding.mainCoordinator.setScaleX(offSetScale);
                mBinding.mainCoordinator.setScaleY(offSetScale);

                final float xOffSet = drawerView.getWidth() * slideOffset;
                final float xOffSetDiff = mBinding.mainCoordinator.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffSet * xOffSetDiff;
                mBinding.mainCoordinator.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        page = 1;

        switch (item.getItemId()) {
            case R.id.menu_popular:
                fetchState = POPULAR_MOVIES;

                mBinding.mainRvMoviesList.setVisibility(View.GONE);
                mBinding.mainPb.setVisibility(View.VISIBLE);

                jsonParse(fetchState);

                mMoviesList.clear();
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.menu_voted:
                fetchState = TOP_RATED_MOVIES;

                mBinding.mainRvMoviesList.setVisibility(View.GONE);
                mBinding.mainPb.setVisibility(View.VISIBLE);

                jsonParse(fetchState);

                mMoviesList.clear();
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.menu_upcoming:
                fetchState = UPCOMING_MOVIES;

                mBinding.mainRvMoviesList.setVisibility(View.GONE);
                mBinding.mainPb.setVisibility(View.VISIBLE);

                jsonParse(fetchState);

                mMoviesList.clear();
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.menu_favorites:

                startActivity(new Intent(MainActivity.this, FavoritesMoviesActivity.class));
                if (mSharedPreferences.getBoolean("enableAnimations", true)) {
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                }
                break;

            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                if (mSharedPreferences.getBoolean("enableAnimations", true)) {
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                }
                break;

            case R.id.menu_about:

                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                if (mSharedPreferences.getBoolean("enableAnimations", true)) {
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                }
                break;

            case R.id.menu_how_to_use:
                startActivity(new Intent(MainActivity.this, MainIntro.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }

        mBinding.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;

        Log.d("MAIN", "REFRESHING...");
        mBinding.mainRvMoviesList.setVisibility(View.GONE);
        mBinding.mainPb.setVisibility(View.VISIBLE);
        mMoviesList.clear();
        mAdapter.notifyDataSetChanged();
        jsonParse(fetchState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("mainActivity", "ONResume");
        if (fetchState == POPULAR_MOVIES) {
            mBinding.navView.setCheckedItem(R.id.menu_popular);
        } else if (fetchState == TOP_RATED_MOVIES) {
            mBinding.navView.setCheckedItem(R.id.menu_voted);
        } else if (fetchState == UPCOMING_MOVIES) {
            mBinding.navView.setCheckedItem(R.id.menu_upcoming);
        }
    }
}