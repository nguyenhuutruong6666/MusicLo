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

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final Context context;
    private List<MusicModel> musicList;
    private List<MusicModel> musicListFull;
    private OnMusicClickListener onMusicClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private List<String> favoriteIds;

    public interface OnMusicClickListener {
        void onMusicClick(MusicModel music);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(MusicModel music, boolean isFavorite);
    }

    public MusicAdapter(Context context, List<MusicModel> musicList) {
        this.context = context;
        this.musicList = musicList;
        this.musicListFull = new ArrayList<>(musicList);
        this.favoriteIds = new ArrayList<>();
    }

    public void setOnMusicClickListener(OnMusicClickListener listener) {
        this.onMusicClickListener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    public void setFavoriteIds(List<String> favoriteIds) {
        this.favoriteIds = favoriteIds;
        notifyDataSetChanged();
    }

    public void updateList(List<MusicModel> newList) {
        this.musicList = newList;
        notifyDataSetChanged();
    }

    private String currentQuery = "";
    private String currentCategory = "Tất cả";

    public void filter(String query, String category) {
        this.currentQuery = query != null ? query.trim().toLowerCase() : "";
        this.currentCategory = category != null ? category : "Tất cả";

        musicList = new ArrayList<>();
        for (MusicModel music : musicListFull) {
            boolean matchQuery = currentQuery.isEmpty() ||
                    (music.getTitle() != null && music.getTitle().toLowerCase().contains(currentQuery)) ||
                    (music.getArtist() != null && music.getArtist().toLowerCase().contains(currentQuery));

            boolean matchCategory = currentCategory.equals("Tất cả") ||
                    (music.getCategory() != null && music.getCategory().equalsIgnoreCase(currentCategory));

            if (matchQuery && matchCategory) {
                musicList.add(music);
            }
        }
        notifyDataSetChanged();
    }

    public void setFullList(List<MusicModel> fullList) {
        this.musicListFull = new ArrayList<>(fullList);
        this.musicList = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
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

        boolean isFavorite = favoriteIds.contains(music.getMusicId());
        holder.btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMusicClickListener != null) {
                    onMusicClickListener.onMusicClick(music);
                }
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFavoriteClickListener != null) {
                    boolean currentFav = favoriteIds.contains(music.getMusicId());
                    onFavoriteClickListener.onFavoriteClick(music, currentFav);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        TextView tvCategory;
        ImageButton btnFavorite;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
