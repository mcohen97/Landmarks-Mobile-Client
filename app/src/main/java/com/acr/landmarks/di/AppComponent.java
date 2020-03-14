package com.acr.landmarks.di;

import android.app.Application;

import com.acr.landmarks.BaseApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ServiceBuildersModule.class,
                ActivityBuildersModule.class,
                FragmentBuildersModule.class,
                AppModule.class,
                ViewModelFactoryModule.class,
                ViewModelsModule.class
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
