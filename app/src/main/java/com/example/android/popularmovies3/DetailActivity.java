package com.example.android.popularmovies3;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies3.Adapters.CategoryAdapter;
import com.example.android.popularmovies3.Database.FavoritesMoviesData;
import com.example.android.popularmovies3.Database.RoomDB;
import com.example.android.popularmovies3.Utils.NetworkUtils;
import com.example.android.popularmovies3.databinding.ActivityDetailBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity{

    private ActivityDetailBinding mBinding;
    private RoomDB mDatabase;
    private SharedPreferences mSharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mSharedPreferences = getSharedPreferences("prefSettings", MODE_PRIVATE);

        mDatabase = RoomDB.getDatabase(this);

        if(mSharedPreferences.getBoolean("enableAnimations", true)) {
            Fade fade = new Fade();
            View decor = getWindow().getDecorView();

            fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(fade);
                getWindow().setExitTransition(fade);
            }
        }

        if(isInFavorites()) {
            mBinding.fab.setImageResource(R.drawable.ic_fav);
        }
        else {
            mBinding.fab.setImageResource(R.drawable.ic_fav_border);
        }

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isInFavorites()){
                    remFav();
                } else {
                    addFav();
                }
            }
        });

        String image = null;
        String movieName;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            movieName = extras.getString("title");
            image = extras.getString("backdropPath");

            mBinding.detailTvTitleToolbar.setTitle(movieName);
        }

        mBinding.tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mBinding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){}
            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });

        String url = null;
        if(!NetworkUtils.isNetworkAvailable(this)){
            MakeDialog();
        } else {
            CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager(), mBinding.tabs.getTabCount(), extras);
            mBinding.viewPager.setAdapter(adapter);
            mBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabs));

            url = getString(R.string.network_url_images) + getString(getImageQuality()) + image;
            Glide.with(this).load(url).placeholder(R.drawable.backdrop_noimage).into(mBinding.detailIvImgHoriz);
            enableDynamicColoring(url);
        }

        setSupportActionBar(mBinding.detailTvTitleToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void MakeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!NetworkUtils.isNetworkAvailable(DetailActivity.this)){
                    MakeDialog();
                }
                else {
                    CategoryAdapter adapter = new CategoryAdapter(DetailActivity.this, getSupportFragmentManager(), mBinding.tabs.getTabCount(), getIntent().getExtras());
                    mBinding.viewPager.setAdapter(adapter);
                    mBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tabs));

                    String url = getString(R.string.network_url_images) + getString(getImageQuality()) + getIntent().getExtras().getString("backdropPath");
                    Glide.with(DetailActivity.this).load(url).placeholder(R.drawable.backdrop_noimage).into(mBinding.detailIvImgHoriz);
                    enableDynamicColoring(url);
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

    private int getImageQuality() {
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

    private void enableDynamicColoring(String url) {
        if(mSharedPreferences.getBoolean("enableDynamicColoring", true)) {
            new getBitmap(url).execute();
        }
    }

    private boolean isInFavorites() {
        return mDatabase.favoritesMoviesDao().isRowIsExist(getIntent().getExtras().getInt("id"));
    }

    private void remFav() {
        mDatabase.favoritesMoviesDao().delete(getIntent().getExtras().getInt("id"));
        mBinding.fab.setImageResource(R.drawable.ic_fav_border);
        Snackbar.make(mBinding.fab, "Removed from Favourites", Snackbar.LENGTH_SHORT).show();
    }

    void addFav(){
        FavoritesMoviesData data = new FavoritesMoviesData();
        data.setMovieId(getIntent().getExtras().getInt("id"));
        Log.e("DetailAct ", "" + getIntent().getExtras().getInt("id"));

        data.setName(getIntent().getExtras().getString("title"));
        data.setPoster(getIntent().getExtras().getString("backdropPath"));
        data.setReleaseDate(getIntent().getExtras().getString("releaseDate"));
        data.setVoteAverage(getIntent().getExtras().getDouble("voteAvg"));

        mDatabase.favoritesMoviesDao().insert(data);
        mBinding.fab.setImageResource(R.drawable.ic_fav);
        Snackbar.make(mBinding.fab, "Added to Favourites", Snackbar.LENGTH_SHORT).show();
    }

    public class getBitmap extends AsyncTask<String, Void, Bitmap> {
        String src;
        Bitmap bit;

        public getBitmap(String url) {
            src = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(src);
                bit = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch(IOException e) {
                System.out.println(e);
            }

            return bit;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            try {
                Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();

                        if(vibrantSwatch != null) {
                            mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
                            mBinding.collapsingToolbar.setContentScrim(new ColorDrawable(vibrantSwatch.getRgb()));
                        }

                        if(darkMutedSwatch != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(darkMutedSwatch.getRgb());
                            }
                        }
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    } //End of bitmap
}
