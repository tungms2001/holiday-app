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
import com.example.holiday.model.Comment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
//xây dựng lớp một danh sách các comment
    private final LayoutInflater inflater;
    private final List<Comment> comments;//tạo 1 danh sách các comment

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.inflater = LayoutInflater.from(context);
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String url = "http://10.0.2.2:8080/holidayapp/server/" + comments.get(position).getAvatar();//192.168.1.2
        Picasso.get().load(url).transform(new CircleTransform()).into(holder.ivAvatar);//vào link lấy ảnh, xong dùng thư viện load ảnh về
        holder.tvFullname.setText(comments.get(position).getFullname());//lấy tên về
        holder.tvContent.setText(comments.get(position).getContent());//lấy nội dung về
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAvatar;
        private final TextView tvFullname;
        private final TextView tvContent;

        public ViewHolder(@NonNull @NotNull View itemView) {//ánh xạ dữ liệu để hiện ra xem
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvFullname = itemView.findViewById(R.id.tv_comment_fullname);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
        }
    }
}
