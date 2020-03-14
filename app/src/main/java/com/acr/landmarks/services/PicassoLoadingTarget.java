package com.acr.landmarks.services;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.acr.landmarks.services.contracts.IImageService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class PicassoLoadingTarget implements Target {

    private IImageService.ImageLoadListener mListener;
    private List<PicassoLoadingTarget> collection;
    private static final String TAG = "PicassoLoadingTarget";

    public PicassoLoadingTarget(IImageService.ImageLoadListener listener, List<PicassoLoadingTarget> targets) {
        mListener = listener;
        collection = targets;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mListener.onImageLoaded(bitmap);
        collection.remove(this);
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        collection.remove(this);
        Log.d(TAG, "Request failed");
        ServerErrorHandler.getInstance().raiseError(e);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}
