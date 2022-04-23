package com.example.android.popularmovies3.Adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.popularmovies3.Fragments.MoviesFragment;
import com.example.android.popularmovies3.Fragments.ReviewsFragment;
import com.example.android.popularmovies3.Fragments.TrailersFragment;

public class CategoryAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private final int mTabCount;
    private Bundle mBundle;

    public CategoryAdapter(Context context, @NonNull FragmentManager fm, int num, Bundle bundle) {
        super(fm);
        mContext = context;
        mTabCount = num;
        mBundle = bundle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return MoviesFragment.newInstance(mBundle);
        }
        else if(position == 1){
            return TrailersFragment.newInstance(mBundle);
        }
        else{
            return ReviewsFragment.newInstance(mBundle);
        }
    }

    @Override
    public int getCount() {
        return mTabCount;
    }
}
