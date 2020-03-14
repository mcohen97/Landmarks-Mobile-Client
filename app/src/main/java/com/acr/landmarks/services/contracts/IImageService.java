package com.acr.landmarks.services.contracts;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface IImageService {

    void loadLandmarkImageToView(ImageView imageView, String fileName);

    void loadTourImageToView(ImageView imageView, String fileName);

    void loadBitmap(String fileName, ImageLoadListener listener);

    interface ImageLoadListener {
        void onImageLoaded(Bitmap img);
    }
}
