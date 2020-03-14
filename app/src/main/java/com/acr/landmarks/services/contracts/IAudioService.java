package com.acr.landmarks.services.contracts;

public interface IAudioService {

    void load(String fileName);

    void play();

    void stop();

    void reset();

    void pause();

    boolean isAudioLoaded();
}
