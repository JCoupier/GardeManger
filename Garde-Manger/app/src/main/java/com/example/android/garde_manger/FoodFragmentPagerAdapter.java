package com.example.android.garde_manger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Garde-Manger created by JCoupier on 12/08/2017.
 */
public class FoodFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public FoodFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FruitsLegumesFragment();
        } else if (position == 1){
           return new FrigoFragment();
        } else if (position == 2) {
            return new CongeloFragment();
        } else if (position == 3) {
            return new PlacardsFragment();
        } else {
            return new EpicesFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.category_fruits_legumes);
        } else if (position == 1) {
            return mContext.getString(R.string.category_frigo);
        } else if (position == 2) {
            return mContext.getString(R.string.category_congelo);
        } else if (position == 3) {
            return mContext.getString(R.string.category_placards);
        } else {
            return mContext.getString(R.string.category_epices);
        }
    }
}
