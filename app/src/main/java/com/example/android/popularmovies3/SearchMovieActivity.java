package com.example.android.popularmovies3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies3.Adapters.MoviesAdapter;
import com.example.android.popularmovies3.DataModels.Movies;
import com.example.android.popularmovies3.Utils.NetworkUtils;
import com.example.android.popularmovies3.databinding.ActivitySearchMovieBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SearchMovieActivity extends AppCompatActivity implements
        MoviesAdapter.MovieItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private MoviesAdapter mAdapter;
    private RequestQueue mQueue;
    private ActivitySearchMovieBinding mBinding;
    private ArrayList<Movies> mMoviesList;

    private ArrayList<String> mSavedList;
    private SharedPreferences mSharedPreferences;

    private int page = 1, limit;
    private String mSearchedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_movie);
        mMoviesList = new ArrayList<>();

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, getOrientationGridSpans());
        mBinding.searchRvMoviesList.setLayoutManager(mLayoutManager);

        mAdapter = new MoviesAdapter(this, mMoviesList, this);
        mBinding.searchRvMoviesList.setAdapter(mAdapter);

        mBinding.searchLayoutSwipe.setOnRefreshListener(this);
        mQueue = Volley.newRequestQueue(this);

        mBinding.searchRvMoviesList.setVisibility(View.GONE);
        mBinding.searchPb.setVisibility(View.GONE);

        mBinding.searchScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if(page < limit) {
                        page++;
                        mBinding.searchPb.setVisibility(View.VISIBLE);
                        jsonParse(mSearchedString);
                    }
                }

                if (scrollY == 0) {
                    mBinding.searchFab.setVisibility(View.GONE);
                }

                if (scrollY > oldScrollY) { //User going down
                    mBinding.searchFab.setVisibility(View.VISIBLE);
                } else { //User going up
                    mBinding.searchFab.setVisibility(View.GONE);
                }

            }
        });

        mBinding.searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.searchScrollView.smoothScrollTo(0, 0);
                mBinding.searchFab.setVisibility(View.GONE);
            }
        });

        loadData();
        ArrayAdapter<String> mSavedListAdapter = new ArrayAdapter<>(this, R.layout.list_item_search_activity, mSavedList);
        mBinding.searchEditText.setAdapter(mSavedListAdapter);

        mBinding.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchedString = v.getText().toString();
                    search(mSearchedString);

                    mBinding.searchEditText.dismissDropDown();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        mBinding.textInput.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard();
                loadData();
            }
        });

        mBinding.textInput.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().hide();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("listPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("movies", null);

        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        mSavedList = gson.fromJson(json, type);

        if(mSavedList == null){
            mSavedList = new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);
        if(mSharedPreferences.getBoolean("enableAnimations", true)) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void showKeyboard(){
        mBinding.searchEditText.setText("");
        mBinding.searchEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private int getOrientationGridSpans() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 4;
        else
            return 3;
    }

    private void search(String query){
        if(!query.isEmpty()){
            mBinding.searchRvMoviesList.setVisibility(View.GONE);
            mBinding.searchPb.setVisibility(View.VISIBLE);
            mBinding.rlSearchEmpty.setVisibility(View.GONE);
            mMoviesList.clear();
            mAdapter.notifyDataSetChanged();

            if(!NetworkUtils.isNetworkAvailable(this)){
                MakeDialog();
            } else {
                jsonParse(query);
            }
        } else {
            Toast.makeText(this, "Pass Some query", Toast.LENGTH_SHORT).show();
            mBinding.rlSearchEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void MakeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!NetworkUtils.isNetworkAvailable(SearchMovieActivity.this)){
                    MakeDialog();
                } else {
                    search(mBinding.searchEditText.getText().toString());
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

    void jsonParse(String query) {

        String url = "https://api.themoviedb.org/3/search/movie?api_key=eb77a6e7061b90a30a6a28502f197d45&query="+ query + "&page=" + page;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(!mSavedList.contains(query)) {
                            mSavedList.add(query);
                            saveData();
                        }
                        try {
                            limit = response.getInt("total_pages");
                            JSONArray moviesJsonJSONArray = response.getJSONArray("results");
                            if(moviesJsonJSONArray.isNull(0)) {
                                showError();
                                return;
                            }

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

                                mBinding.searchRvMoviesList.setVisibility(View.VISIBLE);
                                mBinding.searchPb.setVisibility(View.GONE);
                                mBinding.searchLayoutSwipe.setRefreshing(false);

                                Movies movies = new Movies(adult, backDropPath, id, lang, title, overview, popularity, posterPath, releaseDate, vote);
                                mMoviesList.add(movies);
                            }

                        } catch (JSONException e) {
                            showError();
                        }
                        mAdapter = new MoviesAdapter(SearchMovieActivity.this, mMoviesList, SearchMovieActivity.this);
                        mBinding.searchRvMoviesList.setAdapter(mAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError();
            }
        });
        mQueue.add(request);
    }

    void showError() {
        mBinding.searchRvMoviesList.setVisibility(View.GONE);
        mBinding.searchPb.setVisibility(View.GONE);
        mBinding.rlSearchEmpty.setVisibility(View.VISIBLE);
        Toast.makeText(SearchMovieActivity.this, "Try to search different results!", Toast.LENGTH_SHORT).show();
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("listPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(mSavedList);
        editor.putString("movies", json);

        editor.apply();
    }

    @Override
    public void onMovieItemClick(int id, String title, String backdropPath, String ReleaseDate, double voteAverage, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("title", title);
        bundle.putString("backdropPath", backdropPath);
        bundle.putString("releaseDate", ReleaseDate);
        bundle.putDouble("voteAvg", voteAverage);

        Intent intent = new Intent(SearchMovieActivity.this, DetailActivity.class);
        intent.putExtras(bundle);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchMovieActivity.this, imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onRefresh() {
        Log.d("MAIN", "REFRESHING...");
        mBinding.searchRvMoviesList.setVisibility(View.GONE);
        mBinding.searchPb.setVisibility(View.VISIBLE);
        mMoviesList.clear();
        mAdapter.notifyDataSetChanged();

        jsonParse(mBinding.searchEditText.getText().toString());
    }
}