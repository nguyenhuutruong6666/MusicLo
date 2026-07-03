package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.MusicModel;

import java.util.List;

public class FavoriteMusicAdapter extends RecyclerView.Adapter<FavoriteMusicAdapter.FavoriteViewHolder> {

    private final Context context;
    private final List<MusicModel> favoriteList;
    private OnItemClickListener onItemClickListener;
    private OnRemoveFavoriteListener onRemoveFavoriteListener;

    public interface OnItemClickListener {
        void onItemClick(MusicModel music);
    }

    public interface OnRemoveFavoriteListener {
        void onRemoveFavorite(MusicModel music);
    }

    public FavoriteMusicAdapter(Context context, List<MusicModel> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnRemoveFavoriteListener(OnRemoveFavoriteListener listener) {
        this.onRemoveFavoriteListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_music, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MusicModel music = favoriteList.get(position);

        holder.tvTitle.setText(music.getTitle() != null ? music.getTitle() : "");
        holder.tvArtist.setText(music.getArtist() != null ? music.getArtist() : "");

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(music);
                }
            }
        });

        holder.btnRemoveFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRemoveFavoriteListener != null) {
                    onRemoveFavoriteListener.onRemoveFavorite(music);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        ImageButton btnRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}
