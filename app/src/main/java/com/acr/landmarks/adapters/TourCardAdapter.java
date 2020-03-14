package com.acr.landmarks.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acr.landmarks.R;
import com.acr.landmarks.models.Tour;
import com.acr.landmarks.services.contracts.IImageService;

import java.util.List;

public class TourCardAdapter extends RecyclerView.Adapter<TourCardAdapter.ViewHolder> {
    private Context mContext;
    private TourCardAdapter.TourClickedListener clickListener;
    private List<Tour> lastAvailableTourData;
    private IImageService imageService;

    public TourCardAdapter(Context mContext, TourCardAdapter.TourClickedListener clickListener, List<Tour> data, IImageService service) {
        this.mContext = mContext;
        this.clickListener = clickListener;
        this.imageService = service;
        lastAvailableTourData = data;
    }

    @NonNull
    @Override
    public TourCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_tour_item, parent, false);
        return new TourCardAdapter.ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TourCardAdapter.ViewHolder holder, int position) {
        Tour requestedTour = lastAvailableTourData.get(position);
        String tourName = requestedTour.title;
        holder.title.setText(tourName);

        //Seteo el ícono según temática a la izquierda del textView--->Resolver como serán las temáticas, conseguir íconos y Codificar
        String tourTheme = requestedTour.category;
        switch (tourTheme) {
            case "CULTURAL":
                holder.theme.setImageResource(R.drawable.cultural);
                break;
            case "ENTERTAINMENT":
                holder.theme.setImageResource(R.drawable.entertainment);
                break;
            case "VIEWS":
                holder.theme.setImageResource(R.drawable.views);
                break;
            case "GREENZONES":
                holder.theme.setImageResource(R.drawable.greenzones);
                break;
            default:
                break;
        }

        String image = requestedTour.imageFile;
        imageService.loadTourImageToView(holder.categoryThumbnail, image);
        String tourDescription = requestedTour.description;
        holder.description.setText(tourDescription);
    }

    @Override
    public int getItemCount() {
        return lastAvailableTourData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView theme;
        ImageView categoryThumbnail;
        TextView description;
        Button guidebtn;
        TourCardAdapter.TourClickedListener clickListener;

        public ViewHolder(View itemView, TourCardAdapter.TourClickedListener clickListener) {
            super(itemView);

            this.title = itemView.findViewById(R.id.tour_card_title);
            this.theme = itemView.findViewById(R.id.tour_card_theme);
            this.categoryThumbnail = itemView.findViewById(R.id.tour_image);
            this.description = itemView.findViewById(R.id.tour_card_description);
            this.guidebtn = itemView.findViewById(R.id.view_guide_btn);
            this.clickListener = clickListener; //-> Las cards no serán clickeables solo el botón
            this.guidebtn.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onTourClicked(lastAvailableTourData.get(position));
        }
    }

    public interface TourClickedListener {
        void onTourClicked(Tour clicked);
    }
}
