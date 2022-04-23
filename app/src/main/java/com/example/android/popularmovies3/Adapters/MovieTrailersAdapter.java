package com.example.android.popularmovies3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.DataModels.MovieTrailer;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemFragmentTrailerBinding;

import java.util.ArrayList;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersHolder> {
    final private TrailerItemClickListener mOnClickListener;
    private Context mContext;
    private ArrayList<MovieTrailer> mMovieTrailersList;

    public interface TrailerItemClickListener {
        void onTrailerItemClick(String key);
    }

    public MovieTrailersAdapter(Context context, ArrayList<MovieTrailer> movieTrailers, TrailerItemClickListener clickListener) {
        this.mContext = context;
        this.mOnClickListener = clickListener;
        this.mMovieTrailersList = movieTrailers;
    }

    @NonNull
    @Override
    public MovieTrailersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemFragmentTrailerBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_fragment_trailer, parent, false);
        return new MovieTrailersHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailersHolder holder, int position) {
        MovieTrailer currentMovieTrailer = mMovieTrailersList.get(position);

        Glide.with(mContext).load(mContext.getString(R.string.youtube_img_url) + currentMovieTrailer.getKey() + "/mqdefault.jpg").into(holder.mBinding.rowTrailerIvVideo);
        holder.mBinding.rowTrailerIvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onTrailerItemClick(currentMovieTrailer.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovieTrailersList.size();
    }

    public class MovieTrailersHolder extends RecyclerView.ViewHolder{
        private ListItemFragmentTrailerBinding mBinding;

        public MovieTrailersHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
