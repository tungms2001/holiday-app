package com.example.holiday.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holiday.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TourRecyclerViewAdapter extends RecyclerView.Adapter<TourRecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Tour> tours;

    public TourRecyclerViewAdapter(Context context, List<Tour> tours) {
        this.inflater = LayoutInflater.from(context);
        this.tours = tours;
    }

    @NonNull
    @NotNull
    @Override
    public TourRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tour_info_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TourRecyclerViewAdapter.ViewHolder holder, int position) {
        String url = "http://10.0.2.2:8080/holidayapp/server/" + tours.get(position).getImage();
        Picasso.get().load(url).into(holder.ivAvatar);
        holder.tvName.setText(tours.get(position).getTourName());
        holder.tvDuring.setText(tours.get(position).getDuring());
        holder.tvStatus.setText(tours.get(position).getStatus());
        holder.tvType.setText(tours.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvDuring;
        private final TextView tvStatus;
        private final TextView tvType;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_tour_name);
            tvDuring = itemView.findViewById(R.id.tv_during);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}
