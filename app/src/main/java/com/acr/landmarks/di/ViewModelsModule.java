package com.acr.landmarks.di;

import android.arch.lifecycle.ViewModel;

import com.acr.landmarks.view_models.LandmarksViewModel;
import com.acr.landmarks.view_models.ToursViewModel;
import com.acr.landmarks.view_models.UserLocationViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(LandmarksViewModel.class)
    public abstract ViewModel bindLandmarksViewModel(LandmarksViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ToursViewModel.class)
    public abstract ViewModel bindToursViewModel(ToursViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserLocationViewModel.class)
    public abstract ViewModel bindUserLocarionViewModel(UserLocationViewModel viewModel);

}
