package com.example.holiday.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holiday.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Notification> notifications;

    public NotificationRecyclerViewAdapter(Context context, List<Notification> notifications) {
        this.inflater = LayoutInflater.from(context);
        this.notifications = notifications;
    }

    @NonNull
    @NotNull
    @Override
    public NotificationRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String url = "http://10.0.2.2:8080/holidayapp/server/" + notifications.get(position).getAvatar();
        Picasso.get().load(url).into(holder.ivAvatar);
        if (notifications.get(position).getType().equals("apply")) {
            String content = notifications.get(position).getCreator() + " want to join " +
                    notifications.get(position).getTourName();
            holder.tvContent.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static ImageView ivAvatar;
        private static TextView tvContent;
        private Button btnAccept;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_notif_avatar);
            tvContent = itemView.findViewById(R.id.tv_notif_content);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }
}
