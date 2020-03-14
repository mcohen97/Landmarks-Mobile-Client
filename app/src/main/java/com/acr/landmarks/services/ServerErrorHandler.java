package com.acr.landmarks.services;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.acr.landmarks.services.contracts.IServerErrorHandler;

public class ServerErrorHandler implements IServerErrorHandler {

    private MutableLiveData<Throwable> serverError;
    private static final ServerErrorHandler singleton = new ServerErrorHandler();

    private ServerErrorHandler() {
        serverError = new MutableLiveData<>();
    }

    public static ServerErrorHandler getInstance() {
        return singleton;
    }

    public LiveData<Throwable> serverError() {
        return serverError;
    }

    public void raiseError(Throwable error) {
        serverError.postValue(error);
    }
}
