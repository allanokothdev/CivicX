package com.civicx.android.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.civicx.android.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<String> videoIds;
    private final Lifecycle lifecycle;

    public RecyclerViewAdapter(List<String> videoIds, Lifecycle lifecycle) {
        this.videoIds = videoIds;
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        lifecycle.addObserver(youTubePlayerView);
        return new ViewHolder(youTubePlayerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.cueVideo(videoIds.get(position));
    }

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView youTubePlayerView;
        private YouTubePlayer youTubePlayer;
        private String currentVideoId;

        public ViewHolder(YouTubePlayerView playerView) {
            super(playerView);
            try {
                youTubePlayerView = playerView;
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                        youTubePlayer = initializedYouTubePlayer;
                        youTubePlayer.cueVideo(currentVideoId, 0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void cueVideo(String videoId) {
            try {
                currentVideoId = videoId;
                if(youTubePlayer == null)
                    return;
                youTubePlayer.cueVideo(videoId, 0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
