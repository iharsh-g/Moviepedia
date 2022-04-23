package com.example.android.popularmovies3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.DataModels.Movies;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    final private MovieItemClickListener mOnClickListener;
    private ArrayList<Movies> mMoviesList;
    private Context mContext;
    private int lastPos = -1;

    private SharedPreferences mSharedPreferences;

    public interface MovieItemClickListener {
        void onMovieItemClick(int id, String title, String backdropPath, String ReleaseDate, double voteAverage, ImageView iv);
    }

    public MoviesAdapter(Context context, ArrayList<Movies> movies, MovieItemClickListener clickListener) {
        this.mContext = context;
        this.mMoviesList = movies;
        this.mOnClickListener = clickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemActivityMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_activity_main, parent, false);
        return new MovieViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movies currentMovie = mMoviesList.get(position);

        int quality = getImageQuality();
        Glide.with(mContext).load(mContext.getString(R.string.network_url_images) + mContext.getString(quality) + currentMovie.getBackdropPath()).placeholder(R.drawable.backdrop_noimage).into(holder.mBinding.rowIvMovieThumb);

        holder.mBinding.rowTvTitle.setText(currentMovie.getTitle());
        holder.mBinding.rowRatingBar.setRating((float) currentMovie.getRating());
        holder.setUpdatedDate(currentMovie.getReleaseDate());

        holder.mBinding.movieRowLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onMovieItemClick(currentMovie.getId(),
                        currentMovie.getTitle(),
                        currentMovie.getBackdropPath(),
                        currentMovie.getReleaseDate(),
                        currentMovie.getRating(),
                        holder.mBinding.rowIvMovieThumb);
            }
        });

        Animation animation;
        if( holder.getAdapterPosition() > lastPos ){
            animation = AnimationUtils.loadAnimation(mContext, R.anim.item_right_to_left);
        } else {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.item_left_to_right);
        }
        holder.itemView.setAnimation(animation);
        lastPos = holder.getAdapterPosition();
    }

    private int getImageQuality() {
        mSharedPreferences = mContext.getSharedPreferences("prefSettings", Context.MODE_PRIVATE);
        int quality;

        switch (mSharedPreferences.getString("imageQuality", "Medium")) {
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

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ListItemActivityMainBinding mBinding;
        private Context context;

        private MovieViewHolder(View itemView){
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();
        }

        public void setUpdatedDate(String givenDate){
            String[] data = givenDate.split("-");
            givenDate = data[0];
            mBinding.rowTvYear.setText(givenDate);
        }
    }
}
