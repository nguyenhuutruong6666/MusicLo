package com.example.musiclo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musiclo.R;
import com.example.musiclo.models.MusicModel;

import java.util.List;

public class AdminMusicAdapter extends RecyclerView.Adapter<AdminMusicAdapter.AdminMusicViewHolder> {

    private final Context context;
    private final List<MusicModel> musicList;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(MusicModel music);
    }

    public interface OnEditClickListener {
        void onEditClick(MusicModel music);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(MusicModel music);
    }

    public AdminMusicAdapter(Context context, List<MusicModel> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AdminMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_music, parent, false);
        return new AdminMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMusicViewHolder holder, int position) {
        MusicModel music = musicList.get(position);

        holder.tvTitle.setText(music.getTitle() != null ? music.getTitle() : "");
        holder.tvArtist.setText(music.getArtist() != null ? music.getArtist() : "");
        holder.tvCategory.setText(music.getCategory() != null ? music.getCategory() : "");

        if (music.getImageUrl() != null && !music.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(music.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(music);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(music);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(music);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class AdminMusicViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        TextView tvCategory;
        Button btnEdit;
        Button btnDelete;

        public AdminMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
