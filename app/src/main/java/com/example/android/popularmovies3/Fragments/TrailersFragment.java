package com.example.android.popularmovies3.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.popularmovies3.Adapters.MovieTrailersAdapter;
import com.example.android.popularmovies3.DataModels.MovieTrailer;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.FragmentTrailersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrailersFragment extends Fragment implements
        MovieTrailersAdapter.TrailerItemClickListener {


    private MovieTrailersAdapter mTrailersAdapter;
    private ArrayList<MovieTrailer> mMovieTrailersList;
    private FragmentTrailersBinding mBinding;
    private RequestQueue mQueue;

    private Bundle mBundle;

    public TrailersFragment() {

    }

    public static TrailersFragment newInstance(Bundle bundle) {
        Bundle args = new Bundle();
        args.putBundle("bundle", bundle);
        TrailersFragment trailersFragment = new TrailersFragment();
        trailersFragment.setArguments(args);
        return trailersFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trailers, container, false);
        View rootView = mBinding.getRoot();
        mQueue = Volley.newRequestQueue(getContext());

        mBundle = getArguments().getBundle("bundle");
        mMovieTrailersList = new ArrayList<>();

        GridLayoutManager trailersLayoutManager = new GridLayoutManager(getActivity(), getOrientationGridSpans());
        mBinding.rvTrailersList.setLayoutManager(trailersLayoutManager);

        mTrailersAdapter = new MovieTrailersAdapter(getContext(), mMovieTrailersList, this);
        mBinding.rvTrailersList.setAdapter(mTrailersAdapter);

        mBinding.pbTrailer.setVisibility(View.VISIBLE);
        mBinding.rvTrailersList.setVisibility(View.GONE);
        jsonParse(mBundle.getInt("id"));
        return rootView;
    }

    private void jsonParse(int id) {
        String url = "https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=eb77a6e7061b90a30a6a28502f197d45";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray moviesJsonJSONArray = response.getJSONArray("results");
                            if(moviesJsonJSONArray.isNull(0)) {
                                showError();
                                return;
                            }

                            for (int i = 0; i < moviesJsonJSONArray.length(); i++) {
                                JSONObject trailerJson = (JSONObject) moviesJsonJSONArray.get(i);
                                if (trailerJson.getString("type").equals("Trailer") && trailerJson.getString("site").equals("YouTube")) {

                                    mBinding.pbTrailer.setVisibility(View.GONE);
                                    mBinding.rvTrailersList.setVisibility(View.VISIBLE);

                                    MovieTrailer movieTrailer = new MovieTrailer(
                                            trailerJson.optString("id"),
                                            trailerJson.optString("iso_3166_1"),
                                            trailerJson.optString("iso_639_1"),
                                            trailerJson.optString("key"),
                                            trailerJson.optString("name"),
                                            trailerJson.optString("site"),
                                            trailerJson.optInt("size"),
                                            trailerJson.optString("type")
                                    );
                                    mMovieTrailersList.add(movieTrailer);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError();
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void showError() {
        mBinding.pbTrailer.setVisibility(View.GONE);
        mBinding.tvTrailersNoData.setVisibility(View.VISIBLE);
        mBinding.rvTrailersList.setVisibility(View.GONE);
    }

    private int getOrientationGridSpans() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 2;
        else
            return 1;
    }

    public static void watchYoutubeVideo(Context context, String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.youtube_video_url) + key));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public void onTrailerItemClick(String key) {
        watchYoutubeVideo(getActivity(), key);
    }
}
