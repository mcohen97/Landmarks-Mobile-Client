package com.acr.landmarks;


//import com.acr.landmarks.di.DaggerAppComponent;


import com.acr.landmarks.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;


public class BaseApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
      return DaggerAppComponent.builder().application(this).build();
    }
}
