package com.acr.landmarks.services;

import android.widget.ImageView;

import com.acr.landmarks.R;
import com.acr.landmarks.services.contracts.IImageService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PicassoImageService implements IImageService {

    private String landmarksUrl;
    private String toursUrl;
    private final List<PicassoLoadingTarget> targets;
    private static final String LANDMARK_IMAGES_PATH = "images/landmarks/%s";
    private static final String TOUR_IMAGES_PATH = "images/tours/%s";
    private static final int PLACEHOLDER = R.drawable.ic_statue_background;


    public PicassoImageService(String baseUrl) {
        this.landmarksUrl = baseUrl + LANDMARK_IMAGES_PATH;
        this.toursUrl = baseUrl + TOUR_IMAGES_PATH;
        this.targets = new ArrayList<>();
        Picasso.get().setLoggingEnabled(true);
    }

    public void loadLandmarkImageToView(ImageView imageView, String fileName) {
        Picasso.get().load(PLACEHOLDER).into(imageView);
        String url = String.format(landmarksUrl, fileName);
        Picasso.get().load(url).into(imageView);
    }

    public void loadTourImageToView(ImageView imageView, String fileName) {
        Picasso.get().load(PLACEHOLDER).into(imageView);
        String url = String.format(toursUrl, fileName);
        Picasso.get().load(url).into(imageView);
    }

    public void loadBitmap(String fileName, ImageLoadListener listener) {
        final PicassoLoadingTarget target = new PicassoLoadingTarget(listener, targets);
        targets.add(target);
        Picasso.get().load(String.format(landmarksUrl, fileName)).into(target);
    }
}
