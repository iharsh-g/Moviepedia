package com.example.android.popularmovies3.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.DataModels.MovieCast;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemActivityMainBinding;
import com.example.android.popularmovies3.databinding.ListItemFragmentMovieCastBinding;

import java.util.ArrayList;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder> {

    private ArrayList<MovieCast> mMovieCastList;
    private Context mContext;

    public MovieCastAdapter(Context context, ArrayList<MovieCast> movieCasts) {
        this.mContext = context;
        this.mMovieCastList = movieCasts;
    }

    @NonNull
    @Override
    public MovieCastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemFragmentMovieCastBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_fragment_movie_cast, parent, false);
        return new MovieCastViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCastViewHolder holder, int position) {
        MovieCast currentPerson = mMovieCastList.get(position);

        Glide.with(mContext).load(mContext.getString(R.string.network_url_images) + mContext.getString(R.string.network_width_780) + currentPerson.getMovieCastImage()).placeholder(R.drawable.poster_noimage).into(holder.mBinding.listItemFmcIv);

        holder.mBinding.listItemFmcTvOriginalName.setText(currentPerson.getMovieCastName());
        holder.mBinding.listItemFmcTvChar.setText(currentPerson.getMovieCastCharacter());
    }

    @Override
    public int getItemCount() {
        return mMovieCastList.size();
    }

    class MovieCastViewHolder extends RecyclerView.ViewHolder {
        private ListItemFragmentMovieCastBinding mBinding;

        public MovieCastViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
