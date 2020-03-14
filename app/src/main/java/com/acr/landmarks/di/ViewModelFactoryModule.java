package com.acr.landmarks.di;

import android.arch.lifecycle.ViewModelProvider;

import com.acr.landmarks.view_models.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);
}
