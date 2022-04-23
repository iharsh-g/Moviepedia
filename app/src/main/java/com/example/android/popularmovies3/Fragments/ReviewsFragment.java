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
import com.example.android.popularmovies3.Adapters.MovieReviewsAdapter;
import com.example.android.popularmovies3.DataModels.MovieReview;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.FragmentReviewsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    private MovieReviewsAdapter mReviewsAdapter;
    private ArrayList<MovieReview> mMovieReviewsList;
    private FragmentReviewsBinding mBinding;
    private RequestQueue mQueue;
    private Bundle mBundle;

    public ReviewsFragment() {

    }

    public static ReviewsFragment newInstance(Bundle bundle) {
        Bundle args = new Bundle();
        args.putBundle("bundle", bundle);
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        reviewsFragment.setArguments(args);
        return reviewsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews, container, false);
        View rootView = mBinding.getRoot();
        mQueue = Volley.newRequestQueue(getContext());

        mBundle = getArguments().getBundle("bundle");
        mMovieReviewsList = new ArrayList<>();

        mBinding.rvReviewsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mReviewsAdapter = new MovieReviewsAdapter(getContext(), mMovieReviewsList);
        mBinding.rvReviewsList.setAdapter(mReviewsAdapter);

        mBinding.pbReviews.setVisibility(View.VISIBLE);
        mBinding.rvReviewsList.setVisibility(View.GONE);
        jsonParse(mBundle.getInt("id"));
        return rootView;
    }

    private void jsonParse(int id) {
        String url = "https://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=eb77a6e7061b90a30a6a28502f197d45";

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
                                JSONObject reviewJson = (JSONObject) moviesJsonJSONArray.get(i);

                                mBinding.pbReviews.setVisibility(View.GONE);
                                mBinding.rvReviewsList.setVisibility(View.VISIBLE);
                                mBinding.pbReviews.setVisibility(View.GONE);

                                MovieReview movieReview = new MovieReview(
                                        reviewJson.getString("id"),
                                        reviewJson.getString("author"),
                                        reviewJson.getString("content"),
                                        reviewJson.getString("url")
                                );
                                mMovieReviewsList.add(movieReview);
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
        mBinding.pbReviews.setVisibility(View.GONE);
        mBinding.tvReviewsNoData.setVisibility(View.VISIBLE);
        mBinding.rvReviewsList.setVisibility(View.GONE);
    }
}
