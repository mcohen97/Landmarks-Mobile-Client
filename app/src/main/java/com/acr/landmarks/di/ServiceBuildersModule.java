package com.acr.landmarks.di;

import com.acr.landmarks.background_services.LocationUpdatesService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuildersModule {

    @ContributesAndroidInjector
    abstract LocationUpdatesService contributeLocationUpdatesService();
}
