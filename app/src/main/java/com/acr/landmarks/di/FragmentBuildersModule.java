package com.acr.landmarks.di;

import com.acr.landmarks.ui.LandmarkCardsFragment;
import com.acr.landmarks.ui.MapFragment;
import com.acr.landmarks.ui.ToursFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    //Register frangments where we want to inject dependencies.

    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

    @ContributesAndroidInjector
    abstract LandmarkCardsFragment contributeLandmarkCardsFragment();

    @ContributesAndroidInjector
    abstract ToursFragment contributeToursFragment();

}
