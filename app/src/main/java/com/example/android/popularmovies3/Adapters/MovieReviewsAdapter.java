package com.example.android.popularmovies3.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies3.DataModels.MovieReview;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemFragmentReviewBinding;

import java.util.ArrayList;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsHolder> {
    private ArrayList<MovieReview> mMovieReviewList;
    private Context mContext;

    public MovieReviewsAdapter(Context context, ArrayList<MovieReview> movieReviews) {
        this.mMovieReviewList = movieReviews;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieReviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemFragmentReviewBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_fragment_review, parent,false);
        return new MovieReviewsHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsHolder holder, @SuppressLint("RecyclerView") int position) {
        MovieReview currentMovieReview = mMovieReviewList.get(position);
        holder.mBinding.rowReviewTvAuthor.setText(currentMovieReview.getAuthor());
        holder.mBinding.expandTextView.setText(currentMovieReview.getContent());
        holder.mBinding.llReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse(currentMovieReview.getUrl());
                Log.e("MRA", currentMovieReview.getUrl());
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, webpage));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieReviewList.size();
    }

    class MovieReviewsHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ListItemFragmentReviewBinding mBinding;

        public MovieReviewsHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();
        }
    }
}
