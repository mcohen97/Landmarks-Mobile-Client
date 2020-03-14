package com.acr.landmarks.ui;

import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.contracts.IAudioService;
import com.acr.landmarks.util.Config;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

public class BottomSheetManager {

    private AppCompatActivity mActivity;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private FloatingActionButton directionsButton;
    private SliderLayout mSliderLayout;
    private IAudioService audioPlayer;

    public BottomSheetManager(AppCompatActivity activity, IAudioService service) {
        mActivity = activity;
        createBottomSheet();
        setSlider();
        audioPlayer = service;
    }

    private void createBottomSheet() {
        View bottomSheet = mActivity.findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        TextView description = mActivity.findViewById(R.id.landmarkDescription);
        description.setMovementMethod(new ScrollingMovementMethod());

        directionsButton = mActivity.findViewById(R.id.fab_directions);

        FloatingActionButton audios = mActivity.findViewById(R.id.fab_audios);
        audios.setOnClickListener(v -> playAudio());

        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            boolean expanded = false;

            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    expanded = true;
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                    audioPlayer.reset();
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                if (expanded) {
                    expanded = false;
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
    }

    private void playAudio() {
        if (audioPlayer.isAudioLoaded())
            audioPlayer.play();
        else {
            Toast.makeText(mActivity.getApplicationContext(), "Sin audio", Toast.LENGTH_SHORT).show();

        }

    }

    private void setSlider() {
        LinearLayout layoutBottomSheet = mActivity.findViewById(R.id.bottom_sheet_layout);
        mSliderLayout = layoutBottomSheet.findViewById(R.id.imageSlider);
        mSliderLayout.setAutoScrolling(false);
        mSliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
    }

    public void setDirectionsButtonClickListener(View.OnClickListener listener) {
        directionsButton.setOnClickListener(listener);
    }

    public void hideSheetIfExpanded() {
        if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            audioPlayer.reset();
        }
    }

    public void expandSheetIfHidden() {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onLandmarkSelected(Landmark selectedLandmark, Location mCurrentLocation, int bottomSheetHeight) {
        LinearLayout layoutBottomSheet = mActivity.findViewById(R.id.bottom_sheet_layout);
        TextView sheetLandmarkName = layoutBottomSheet.findViewById(R.id.landmarkName);
        TextView sheetLandmarkDistance = layoutBottomSheet.findViewById(R.id.landmarkDistance);
        TextView sheetLandmarkDescription = layoutBottomSheet.findViewById(R.id.landmarkDescription);

        sheetLandmarkName.setText(selectedLandmark.title);
        sheetLandmarkDescription.setText(selectedLandmark.description);
        sheetLandmarkDescription.scrollTo(0, 0);


        if (mCurrentLocation != null) {
            String distance = "" + (mCurrentLocation.distanceTo(createLocation(selectedLandmark.latitude, selectedLandmark.longitude)) / 1000);
            distance = distance.substring(0, 4);
            distance += " Km";
            sheetLandmarkDistance.setText(distance);
        }

        addImages(selectedLandmark.imageFiles);

        if (selectedLandmark.audioFiles != null && selectedLandmark.audioFiles.length > 0) {
            audioPlayer.load(selectedLandmark.audioFiles[0]);
        }

        View bottomSheet = mActivity.findViewById(R.id.bottom_sheet_layout);
        bottomSheet.getLayoutParams().height = bottomSheetHeight;
        bottomSheet.requestLayout();
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public boolean isHidden() {
        return mBottomSheetBehaviour.getState() == mBottomSheetBehaviour.STATE_HIDDEN;
    }

    private void addImages(String[] images) {
        mSliderLayout.clearSliderViews();
        String imagesDirectory = Config.getConfigValue(mActivity, "api_url") + "images/landmarks/";
        for (String image : images) {
            SliderView sliderView = new DefaultSliderView(mActivity);
            sliderView.setImageUrl(imagesDirectory + image);
            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);

            mSliderLayout.addSliderView(sliderView);
        }
    }

    private Location createLocation(double lat, double lng) {
        Location conversion = new Location(new String());
        conversion.setLatitude(lat);
        conversion.setLongitude(lng);
        return conversion;
    }
}
