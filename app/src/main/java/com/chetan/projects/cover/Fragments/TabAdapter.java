package com.chetan.projects.cover.Fragments;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by katherinekuan on 4/14/16.
 */
public class TabAdapter extends FragmentPagerAdapter {

    private final Tab1 tab1;
    private final Tab3 tab3;
    private final Tab2 tab2;

    public TabAdapter(FragmentManager fm) {
        super(fm);
        tab1 = new Tab1();
        tab2 = new Tab2();
        tab3 = new Tab3();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return tab1;
        } else if (position == 1) {
            return tab2;
        } else {
            return tab3;
        }
    }



    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position ==0){
            return "CATEGORIES";
        }else if(position == 1){
            return "FEATURED";
        }
        else{
            return "NEW";
        }
    }
}
