package com.acr.landmarks.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.acr.landmarks.ui.LandmarkCardsFragment;
import com.acr.landmarks.ui.MapFragment;
import com.acr.landmarks.ui.ToursFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                ToursFragment toursFragment = new ToursFragment();
                return toursFragment;
            case 1:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;
            case 2:
                LandmarkCardsFragment cardsFragment = new LandmarkCardsFragment();
                return cardsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}