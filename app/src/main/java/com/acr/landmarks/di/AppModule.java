package com.acr.landmarks.di;


import android.app.Application;

import com.acr.landmarks.persistence.contracts.LandmarkStorage;
import com.acr.landmarks.persistence.RoomLandmarksStorage;
import com.acr.landmarks.services.AudioStreamPlayer;
import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.PicassoImageService;
import com.acr.landmarks.services.RetrofitLandmarksService;
import com.acr.landmarks.services.RetrofitLocationService;
import com.acr.landmarks.services.RetrofitToursService;
import com.acr.landmarks.services.ServerErrorHandler;
import com.acr.landmarks.services.contracts.IAudioService;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.services.contracts.ILandmarksService;
import com.acr.landmarks.services.contracts.ILocationService;
import com.acr.landmarks.services.contracts.ILocationUpdatesService;
import com.acr.landmarks.services.contracts.IServerErrorHandler;
import com.acr.landmarks.services.contracts.ITourService;
import com.acr.landmarks.util.Config;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    //Register all singleton services that are available for injection.

    @Singleton
    @Provides
    static IImageService provideImageService(Application application) {
        return new PicassoImageService(Config.getConfigValue(application, "api_url"));
    }

    @Singleton
    @Provides
    static ILandmarksService provideLandmarksService(Application application) {
        return new RetrofitLandmarksService(Config.getConfigValue(application, "api_url"));
    }

    @Singleton
    @Provides
    static ITourService provideTourService(Application application) {
        return new RetrofitToursService(Config.getConfigValue(application, "api_url"));
    }

    @Singleton
    @Provides
    static LandmarkStorage provideLandmarksStorage(Application application) {
        return new RoomLandmarksStorage(application);
    }

    @Singleton
    @Provides
    static IAudioService provideAudioService(Application application) {
        String audiosUrl = Config.getConfigValue(application, "api_url") + "audios/";
        return new AudioStreamPlayer(audiosUrl);
    }

    @Singleton
    @Provides
    static ILocationService provideLocationService() {
        return LocationService.getInstance();
    }

    @Singleton
    @Provides
    static IServerErrorHandler provideServerErrorHandler() {
        return ServerErrorHandler.getInstance();
    }

    @Singleton
    @Provides
    static ILocationUpdatesService provideLocationUpdatesService(Application application) {
        String url = Config.getConfigValue(application, "api_url");
        return new RetrofitLocationService(url);
    }
}
