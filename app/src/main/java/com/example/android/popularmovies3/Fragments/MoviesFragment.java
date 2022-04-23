package com.example.android.popularmovies3.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies3.Adapters.MovieDetailsAdapter;
import com.example.android.popularmovies3.DataModels.MovieCast;
import com.example.android.popularmovies3.DataModels.MovieDetails;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.FragmentMovieBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MoviesFragment extends Fragment {

    private FragmentMovieBinding mBinding;
    private Bundle mBundle;
    private RequestQueue mQueue, mQueue2;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private ArrayList<MovieDetails> mMovieDetailsList;

    public MoviesFragment() {

    }

    public static MoviesFragment newInstance(Bundle bundle) {
        Bundle args = new Bundle();
        args.putBundle("bundle", bundle);
        MoviesFragment moviesFragment = new MoviesFragment();
        moviesFragment.setArguments(args);
        return moviesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie, container, false);
        View rootView = mBinding.getRoot();

        mQueue = Volley.newRequestQueue(getContext());
        mQueue2 = Volley.newRequestQueue(getContext());

        mBundle = getArguments().getBundle("bundle");

        mMovieDetailsList = new ArrayList<>();
        mBinding.fragMovieRv.setLayoutManager(new LinearLayoutManager(getContext()));

        mMovieDetailsAdapter = new MovieDetailsAdapter(getContext(), mMovieDetailsList);
        mBinding.fragMovieRv.setAdapter(mMovieDetailsAdapter);

        mBinding.fragMoviePb.setVisibility(View.VISIBLE);
        mBinding.fragMovieRv.setVisibility(View.GONE);
        jsonParse(mBundle.getInt("id"));
        return rootView;
    }

    private void jsonParse(int id) {
        String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=eb77a6e7061b90a30a6a28502f197d45";
        String urlForCast = "https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=eb77a6e7061b90a30a6a28502f197d45";

        Log.e("MF", urlForCast);
        ArrayList<MovieCast> mMovieCastList = new ArrayList<>();
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlForCast, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray castArray = response.getJSONArray("cast");
                            for (int i = 0; i < castArray.length(); i++) {
                                JSONObject jsonObject = castArray.getJSONObject(i);

                                MovieCast movieCast = new MovieCast(jsonObject.getString("id"),
                                        jsonObject.getString("original_name"),
                                        jsonObject.getString("profile_path"),
                                        jsonObject.getString("character"));
                                mMovieCastList.add(movieCast);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request1);


        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            StringBuilder genres = null;
                            JSONArray genresArray = response.getJSONArray("genres");
                            if(genresArray.length() != 0) {
                                for (int i = 0; i < genresArray.length(); i++) {
                                    JSONObject jsonObject = genresArray.getJSONObject(i);

                                    if (genres == null) {
                                        genres = new StringBuilder(jsonObject.getString("name"));
                                    } else {
                                        genres.append(", " + jsonObject.getString("name"));
                                    }
                                }
                            }

                            String gen = "";
                            if(genres != null) {
                                gen = genres.toString();
                            }
                            mBinding.fragMoviePb.setVisibility(View.GONE);
                            mBinding.fragMovieRv.setVisibility(View.VISIBLE);
                            MovieDetails movieDetails = new MovieDetails(
                                    response.getDouble("vote_average"),
                                    response.getString("title"),
                                    response.getString("tagline"),
                                    response.getString("poster_path"),
                                    response.getString("overview"),
                                    response.getString("release_date"),
                                    response.getString("revenue"),
                                    response.getString("budget"),
                                    gen,
                                    response.getInt("runtime"),
                                    mMovieCastList);

                            mMovieDetailsList.add(movieDetails);

                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue2.add(request2);
    }
}
