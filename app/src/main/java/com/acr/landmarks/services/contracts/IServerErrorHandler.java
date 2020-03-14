package com.acr.landmarks.services.contracts;

import android.arch.lifecycle.LiveData;

public interface IServerErrorHandler {

    LiveData<Throwable> serverError();

    void raiseError(Throwable error);

}
