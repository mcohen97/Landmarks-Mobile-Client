package com.acr.landmarks.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.adapters.TourCardAdapter;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.contracts.DebugConstants;
import com.acr.landmarks.services.contracts.IImageService;
import com.acr.landmarks.view_models.ToursViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ToursFragment extends DaggerFragment implements TourCardAdapter.TourClickedListener {

    private TourCardAdapter adapter;
    private RecyclerView recyclerView;
    private ToursViewModel viewModel;
    private TextView emptyView;
    private List<Tour> data;
    private TourSelectedListener mListener;
    @Inject
    IImageService imageService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ToursViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tours, container, false);
        data = new ArrayList<>();
        initRecyclerView(inflater, view);
        viewModel.getTours().observe(this, tours -> {
            Log.d(DebugConstants.AP_DEX, "Received new tours, time: " + System.currentTimeMillis());
            //Fragment and adapter share the same reference to data
            data.clear();
            data.addAll(tours);
            checkEmpty();
            adapter.notifyDataSetChanged();
            Log.d(DebugConstants.AP_DEX, "Displayed new tours, time: " + System.currentTimeMillis());
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
        recyclerView = view.findViewById(R.id.tour_cards_recyclerview_id);
        emptyView = view.findViewById(R.id.empty_tours);
        emptyView.setVisibility(View.VISIBLE);
        adapter = new TourCardAdapter(getContext(), this, data, imageService);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
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
    public void onTourClicked(Tour clicked) {
        viewModel.setSelectedTour(clicked.id);
        mListener.onTourSelected(clicked);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TourSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " + TourSelectedListener.class);
        }
    }
}
