package com.example.holiday.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.holiday.R;
import com.example.holiday.helper.CircleTransform;
import com.example.holiday.model.Member;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberPresentationRecyclerViewAdapter extends RecyclerView.Adapter<MemberPresentationRecyclerViewAdapter.ViewHolder> {
//Lớp này hiện ra thành viên ở các chuyến đi chung, chỉ để xem danh sách lớp mà thôi
    private final LayoutInflater inflater;
    private final List<Member> members;

    public MemberPresentationRecyclerViewAdapter(Context context, List<Member> members) {
        this.inflater = LayoutInflater.from(context);
        this.members = members;
    }

    @NonNull
    @NotNull
    @Override
    public MemberPresentationRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {//dùng để ánh xạ chính xác view nào trong xml
        View view = inflater.inflate(R.layout.member_presentation_block, parent, false);
        return new MemberPresentationRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberPresentationRecyclerViewAdapter.ViewHolder holder, int position) {
        String url = "http://10.0.2.2:8080/holidayapp/server/" + members.get(position).getAvatar();
        Picasso.get().load(url).transform(new CircleTransform()).into(holder.ivAvatar);//load ảnh và tên thành viên
        holder.tvUsername.setText(members.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvUsername;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
        }
    }
}