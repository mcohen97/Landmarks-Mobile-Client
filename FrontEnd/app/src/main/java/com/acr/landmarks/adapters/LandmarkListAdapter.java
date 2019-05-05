package com.acr.landmarks.adapters;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Landmark;
import com.acr.landmarks.services.HaversineDistanceCalculatorService;
import com.acr.landmarks.services.contracts.IDistanceCalculatorService;
import com.acr.landmarks.ui.MainActivity;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class LandmarkListAdapter extends RecyclerView.Adapter<LandmarkListAdapter.ViewHolder>{

    private final IDistanceCalculatorService listCalculatorService;
    private List<Landmark> mLandmarks;
    private LandmarkListRecyclerClickListener mClickListener;

    public LandmarkListAdapter(List<Landmark> lms, LandmarkListRecyclerClickListener clickListener) {
        mLandmarks = lms;
        mClickListener = clickListener;
        listCalculatorService = new HaversineDistanceCalculatorService();
        updateDistances(mLandmarks);
        Collections.sort(mLandmarks);
    }

    private void updateDistances(List<Landmark> mLandmarks) {
        /*Location mUserLocation = MainActivity.mUserLocation;
        for(Landmark landmark:mLandmarks){
            double distance = listCalculatorService.calculateDistanceInKm(mUserLocation,landmark);
        }*/
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.layout_landmark_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, mClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DecimalFormat FORMATTER = new DecimalFormat("0.###");
        String distance = FORMATTER.format(mLandmarks.get(position).getDistance());
        distance += " Km";

        ((ViewHolder)holder).name.setText(mLandmarks.get(position).getName());
        ((ViewHolder)holder).description.setText(mLandmarks.get(position).getDescription());
        ((ViewHolder)holder).distance.setText( distance );
    }

    @Override
    public int getItemCount() {
        return mLandmarks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name , description , distance;
        LandmarkListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, LandmarkListRecyclerClickListener clickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            distance = itemView.findViewById(R.id.distance);
            mClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onLandmarkClicked(getAdapterPosition());
        }
    }

    public interface LandmarkListRecyclerClickListener{
        void onLandmarkClicked(int position);
    }


}
