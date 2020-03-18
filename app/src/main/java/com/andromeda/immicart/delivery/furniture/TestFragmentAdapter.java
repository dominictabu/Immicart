package com.andromeda.immicart.delivery.furniture;

import android.util.Log;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TestFragmentAdapter extends FragmentPagerAdapter {
//    protected static final int[] CONTENT = new int[]{R.drawable.bakeries, R.drawable.chair, R.drawable.furniture, R.drawable.model_african,};
    protected static final String[] Titles = new String[]{"one", "2", "3", "4",};

    protected ArrayList<FurnitureImage> banners;
    private static final String TAG = "TestFragmentAdapter";

    private int mCount = 8;

    public TestFragmentAdapter(FragmentManager fm, ArrayList<FurnitureImage> banners) {
        super(fm);
        this.banners = banners;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem()" );
        return ImageFragment.newInstance(banners.get(position).getLink());
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TestFragmentAdapter.Titles[position % banners.size()];
    }
}