package com.example.android.popularmovies3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.DataModels.MovieCast;
import com.example.android.popularmovies3.DataModels.MovieDetails;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemFragmentMovieBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MovieDetailsAdapter extends RecyclerView.Adapter<MovieDetailsAdapter.MovieDetailViewHolder> {

    private ArrayList<MovieDetails> mDetailsList;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public MovieDetailsAdapter(Context context, ArrayList<MovieDetails> mDetailsList) {
        this.mDetailsList = mDetailsList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemFragmentMovieBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_fragment_movie, parent, false);
        return new MovieDetailViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieDetailViewHolder holder, int position) {
        MovieDetails currentMovieDetails = mDetailsList.get(position);

        Glide.with(holder.mContext).load(holder.mContext.getString(R.string.network_url_images)
                + holder.mContext.getString(getImageQuality())
                + currentMovieDetails.getPosterPath()).placeholder(R.drawable.backdrop_noimage).into(holder.mBinding.fragmentMovieIvPoster);

        holder.mBinding.fragmentMovieTvTitle.setText(currentMovieDetails.getTitle());
        holder.mBinding.fragmentMovieTvTagline.setText(currentMovieDetails.getTagline());
        holder.setUpdatedDate(currentMovieDetails.getReleaseDate());
        holder.mBinding.fragmentMovieTvRating.setText("" + currentMovieDetails.getRating());
        holder.mBinding.fragmentMovieTvSynopsis.setText(currentMovieDetails.getOverview());

        String revenue = updatedInDollar(currentMovieDetails.getRevenue());
        holder.mBinding.fragmentMovieTvMovieRevenue.setText(revenue);

        String budget = updatedInDollar(currentMovieDetails.getBudget());
        holder.mBinding.fragmentMovieTvMovieBudget.setText(budget);

        holder.mBinding.fragmentMovieTvMovieRuntime.setText("" + currentMovieDetails.getRuntime());
        holder.mBinding.fragmentMovieTvMovieGenres.setText(currentMovieDetails.getGenres());

        ArrayList<MovieCast> movieCasts = currentMovieDetails.getMovieCastList();
        MovieCastAdapter movieCastAdapter = new MovieCastAdapter(mContext, movieCasts);
        holder.mBinding.fragmentMovieRvHorizontal.setHasFixedSize(true);
        holder.mBinding.fragmentMovieRvHorizontal.setLayoutManager(new LinearLayoutManager(holder.mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.mBinding.fragmentMovieRvHorizontal.setAdapter(movieCastAdapter);
    }

    @Override
    public int getItemCount() {
        return mDetailsList.size();
    }

    private int getImageQuality() {
        mSharedPreferences = mContext.getSharedPreferences("prefSettings", Context.MODE_PRIVATE);
        int quality;

        switch (mSharedPreferences.getString("detailImageQuality", "Medium")) {
            case "Low":
                quality = R.string.network_width_342;
                break;
            case "Medium":
                quality = R.string.network_width_500;
                break;
            case "High":
                quality = R.string.network_width_780;
                break;
            default:
                quality = R.string.network_width_original;
                break;
        }

        return quality;
    }

    private String updatedInDollar(String ch) {
        int length = ch.length();

        switch (length) {
            case 1:  return "Available Soon";
            case 7:  return "$" + ch.charAt(0) + "." + ch.charAt(1) + " Million";
            case 8:  return "$" + ch.charAt(0) + ch.charAt(1) + " Million";
            case 9:  return "$" + ch.charAt(0) + ch.charAt(1) + ch.charAt(2) + " Million";
            case 10: return "$" + ch.charAt(0) + "." + ch.charAt(1) + " Billion";
            case 11: return "$" + ch.charAt(0) + ch.charAt(1) + " Billion";
            case 12: return "$" + ch.charAt(0) + ch.charAt(1) + ch.charAt(2) + " Billion";
            case 13: return "$" + ch.charAt(0) + "." + ch.charAt(1) + " Trillion";
            default: return ch;
        }
    }

    class MovieDetailViewHolder extends RecyclerView.ViewHolder {
        private ListItemFragmentMovieBinding mBinding;
        private Context mContext;

        public MovieDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mContext = itemView.getContext();
        }

        public void setUpdatedDate(String givenDate){

            if(givenDate == null || givenDate.equals("")) {
                mBinding.fragmentMovieTvReleaseDate.setText("Available Soon");
                return;
            }

            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Date date = null;
            try {
                date = readDate.parse(givenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat writeDate = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);
            givenDate = writeDate.format(date);

            mBinding.fragmentMovieTvReleaseDate.setText(givenDate);
        }
    }
}
