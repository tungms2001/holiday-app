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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberAddingRecyclerViewAdapter extends RecyclerView.Adapter<MemberAddingRecyclerViewAdapter.ViewHolder> {
//Lớp này dùng để chứa danh sách các thành viên thêm vào
    private OnItemClickListener mListener;
    private final LayoutInflater inflater;
    private final List<String> usernames;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MemberAddingRecyclerViewAdapter(Context context, List<String> usernames) {
        this.inflater = LayoutInflater.from(context);
        this.usernames = usernames;
    }

    @NonNull
    @NotNull
    @Override
    public MemberAddingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.member_adding_block, parent, false);
        return new MemberAddingRecyclerViewAdapter.ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberAddingRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tvUsername.setText(usernames.get(position));//lấy ra tên
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private final TextView tvUsername;
        private final ImageView ivRemove;

        public ViewHolder(@NonNull @NotNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            ivRemove = itemView.findViewById(R.id.iv_remove);

            ivRemove.setOnClickListener(v -> {//tạo nút, bấm nào nó sẽ xóa thành phần đó
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        listener.onDeleteClick(position);
                }
            });
        }
    }
}