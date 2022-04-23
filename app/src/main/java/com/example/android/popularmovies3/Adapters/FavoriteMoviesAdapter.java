package com.example.android.popularmovies3.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.DataModels.MainViewModel;
import com.example.android.popularmovies3.Database.FavoritesMoviesData;
import com.example.android.popularmovies3.Database.RoomDB;
import com.example.android.popularmovies3.R;
import com.example.android.popularmovies3.databinding.ListItemActivityMainBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMovieViewHolder> {

    private ArrayList<FavoritesMoviesData> mMoviesDataList;
    private Context context;
    private Activity mActivity;
    final private FavoriteMovieItemClickListener mOnClickListener;

    //Deleting
    private ArrayList<FavoritesMoviesData> mSelectedList = new ArrayList<>();
    private MainViewModel mainViewModel;
    private boolean isEnable = false, isSelectAll = false;
    private ActionMode mActionMode;
    private RoomDB mDatabase;

    public interface FavoriteMovieItemClickListener {
        void onMovieItemClick(int id, String title, String backdropPath, String ReleaseDate, double voteAverage, ImageView iv);
    }

    public FavoriteMoviesAdapter(Context context, Activity activity, ArrayList<FavoritesMoviesData> moviesData, FavoriteMovieItemClickListener clickListener) {
        this.mMoviesDataList = moviesData;
        this.context = context;
        this.mActivity = activity;
        this.mOnClickListener = clickListener;
    }

    @NonNull
    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemActivityMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_activity_main, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);
        return new FavoriteMovieViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMovieViewHolder holder, int position) {
        FavoritesMoviesData currentMovie = mMoviesDataList.get(position);

        Glide.with(context).load(context.getString(R.string.network_url_images) + context.getString(R.string.network_width_500) + currentMovie.getPoster()).placeholder(R.drawable.backdrop_noimage).into(holder.mBinding.rowIvMovieThumb);

        holder.mBinding.rowTvTitle.setText(currentMovie.getName());
        holder.mBinding.rowRatingBar.setRating((float) currentMovie.getVoteAverage());
        holder.setUpdatedDate(currentMovie.getReleaseDate());

        holder.mBinding.movieRowLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnable) {
                    clickItem(holder);
                }
                else {
                    mOnClickListener.onMovieItemClick(currentMovie.getMovieId(),
                            currentMovie.getName(),
                            currentMovie.getPoster(),
                            currentMovie.getReleaseDate(),
                            currentMovie.getVoteAverage(),
                            holder.mBinding.rowIvMovieThumb);
                }
            }
        });

        holder.mBinding.movieRowLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                if(!isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.favorite_activty_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            mSelectedList.clear();
                            isEnable = true;
                            mActionMode = mode;
                            clickItem(holder);

                            mainViewModel.getString().observe((LifecycleOwner) context, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    if(s.equals("0")){
                                        mode.setTitle("Select Items");
                                        menu.findItem(R.id.menu_delete).setVisible(false);
                                    }
                                    else if(s.equals("1")){
                                        mode.setTitle(String.format("%s Item Selected", s));
                                        menu.findItem(R.id.menu_delete).setVisible(true);
                                    }
                                    else {
                                        mode.setTitle(String.format("%s Items Selected", s));
                                        menu.findItem(R.id.menu_delete).setVisible(true);
                                    }
                                }
                            });

                            return true;
                        } //end of prepareActionMode

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();

                            switch (id) {
                                case R.id.menu_delete:

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                                    if(mode.getTitle().equals("1 Item Selected")) {
                                        builder.setTitle("Do you want to delete?");
                                    }
                                    else {
                                        builder.setTitle("Do you want to delete these items?");
                                    }
                                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for(FavoritesMoviesData data: mSelectedList) {
                                                mDatabase = RoomDB.getDatabase(context);

                                                mDatabase.favoritesMoviesDao().delete(data.getMovieId());
                                                mMoviesDataList.remove(data);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    });
                                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();

                                    if (mMoviesDataList.size() == 0) {
                                        holder.mBinding.movieRowLl.setVisibility(View.VISIBLE);
                                    }
                                    mode.finish();
                                    break;

                                case R.id.menu_select_all:

                                    if (mSelectedList.size() == mMoviesDataList.size()) {
                                        isSelectAll = false;
                                        mSelectedList.clear();
                                    }
                                    else {
                                        isSelectAll = true;
                                        mSelectedList.clear();
                                        mSelectedList.addAll(mMoviesDataList);
                                    }

                                    mainViewModel.setString(String.valueOf(mSelectedList.size()));
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        } //end of onActionItemClicked

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnable = false;
                            isSelectAll = false;
                            mActionMode = null;
                            notifyDataSetChanged();
                        }
                    };

                    mActionMode = mActivity.startActionMode(callback);
                }
                else {
                    clickItem(holder);
                }

                return true;
            }
        }); //end of long click

        if(isSelectAll) {
            holder.mBinding.ivCheck.setVisibility(View.VISIBLE);
//            holder.mBinding.movieRowLl.setBackgroundColor(Color.LTGRAY);
        }
        else {
            holder.mBinding.ivCheck.setVisibility(View.GONE);
//            holder.mBinding.movieRowLl.setBackgroundColor(Color.parseColor("#404040"));
        }
    }

    private void clickItem(FavoriteMovieViewHolder holder) {
        FavoritesMoviesData currentMovie = mMoviesDataList.get(holder.getAdapterPosition());

        if (holder.mBinding.ivCheck.getVisibility() == View.GONE) {
            holder.mBinding.ivCheck.setVisibility(View.VISIBLE);
//            holder.mBinding.movieRowLl.setBackgroundColor(Color.LTGRAY);
            mSelectedList.add(currentMovie);
        }
        else {
            holder.mBinding.ivCheck.setVisibility(View.GONE);
//            holder.mBinding.movieRowLl.setBackgroundResource(R.color.main_card_subBackground);
            mSelectedList.remove(currentMovie);
        }
        mainViewModel.setString(String.valueOf(mSelectedList.size()));
    }

    @Override
    public int getItemCount() {
        return mMoviesDataList.size();
    }

    class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {

        private ListItemActivityMainBinding mBinding;
        private Context context;

        public FavoriteMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();
        }

        public void setUpdatedDate(String givenDate){

            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            Date date = null;
            try {
                date = readDate.parse(givenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat writeDate = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
            givenDate = writeDate.format(date);

            mBinding.rowTvYear.setText(givenDate);
        }
    }
}
