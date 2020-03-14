package com.acr.landmarks.di;

import com.acr.landmarks.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    //Register activities where we want to inject dependencies.

    @ContributesAndroidInjector
    abstract MainActivity provideMainActivity();
}
