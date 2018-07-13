package com.khtn.videorecommendation.videorecommendation.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.home.view.abstracts.VideoAbstract;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.OnClickVideoListener;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends VideoAbstract {
    private Context context;
    private OnClickVideoListener onClickVideoListener;

    public VideoAdapter(OnClickVideoListener onClickVideoListener, List<Video> videos) {
        this.onClickVideoListener = onClickVideoListener;
        this.videos = videos;
    }

    @Override
    public List<Video> getVideos() {
        return this.videos;
    }

    @Override
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster)
        ImageView poster;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.view)
        TextView view;
        @BindView(R.id.loading_image_progress)
        ProgressBar loadingImageProgressBar;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Video video) {
            loadingImageProgressBar.setVisibility(View.VISIBLE);
            poster.setImageDrawable(null);
            title.setText(video.getName());
            tvDescription.setText(video.getVideoDescribe());
            view.setText(Long.toString(video.getTotalView()) + " lượt xem");
            Picasso.get()
                    .load("https://img.youtube.com/vi/" + video.getLinkVideo() + "/0.jpg")
                    .placeholder(R.drawable.default_image).into(poster, new Callback() {
                @Override
                public void onSuccess() {
                    loadingImageProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception ex) {

                }
            });
            itemView.setOnClickListener(v -> VideoAdapter.this.onClickVideoListener.onVideoClicked(video));

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;
        View view = layoutInflater.inflate(R.layout.cardview_item, parent, false);
        viewHolder = new VideoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Video video = videos.get(position);
        ((VideoViewHolder) holder).bind(video);
    }

    @Override
    public int getItemCount() {
        return (videos == null) ? 0 : videos.size();
    }

}
