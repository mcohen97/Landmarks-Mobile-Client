package com.acr.landmarks.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.LandmarkCardAdapter;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.models.LandmarkDistanceComparator;
import com.acr.landmarks.services.LocationService;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.view_models.LandmarksViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class LandmarkCardsFragment extends DaggerFragment implements LandmarkCardAdapter.LandmarkCardClickListener {

    private LandmarkCardAdapter adapter;
    private RecyclerView recyclerView;
    private LandmarksViewModel viewModel;
    private TextView emptyView;
    private List<Landmark> data;
    @Inject
    IImageService imageService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(LandmarksViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_landmark_list, container, false);
        data = new ArrayList<Landmark>();

        initRecyclerView(inflater, view);
        viewModel.getLandmarks().observe(this, landmarks -> {
            //Fragment and adapter share the same reference to data
            data.clear();
            Location userLocation = LocationService.getInstance().getLocation().getValue();
            for (Landmark landmark : landmarks) {
                Location landmarkLocation = new Location(new String());
                landmarkLocation.setLatitude(landmark.latitude);
                landmarkLocation.setLongitude(landmark.longitude);
                landmark.distance = (int) userLocation.distanceTo(landmarkLocation);
            }
            landmarks.sort(new LandmarkDistanceComparator());
            data.addAll(landmarks);
            checkEmpty();
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    private void checkEmpty() {
        if (data.isEmpty()) {
            this.emptyView.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.GONE);
        } else {
            this.emptyView.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView(LayoutInflater inflater, View view) {
        recyclerView = view.findViewById(R.id.cards_recyclerview_id);
        adapter = new LandmarkCardAdapter(getContext(), this, data, imageService);
        emptyView = view.findViewById(R.id.empty_landmarks);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                checkEmpty();
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                checkEmpty();
            }
        });
    }

    @Override
    public void onLandmarkClicked(Landmark clicked) {
        viewModel.setSelectedLandmark(clicked.id);
    }


}
