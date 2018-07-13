package com.khtn.videorecommendation.videorecommendation.home.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RatingBar;
import android.widget.TextView;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.database.FirebaseManager;
import com.khtn.videorecommendation.videorecommendation.home.view.abstracts.VideoAbstract;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.OnClickVideoListener;
import com.khtn.videorecommendation.videorecommendation.model.Log;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoDeTailAdapter extends VideoAbstract {
    private Context context;
    private OnClickVideoListener onClickVideoListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private boolean btnShowDetailClicked = false;

    public VideoDeTailAdapter(List<Video> videoFilterList, OnClickVideoListener onClickVideoListener) {
        this.videos = videoFilterList;
        this.onClickVideoListener = onClickVideoListener;
    }

    @Override
    public List<Video> getVideos() {
        return this.videos;
    }

    @Override
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public class VideoRecommendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster_rec)
        ImageView posterRec;
        @BindView(R.id.title_rec)
        TextView titleRec;
        @BindView(R.id.tv_description_rec)
        TextView tvDescriptionRec;
        @BindView(R.id.view_rec)
        TextView viewRec;
        @BindView(R.id.loading_image_progress)
        ProgressBar loadingImageProgressBar;

        public VideoRecommendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Video video) {
            loadingImageProgressBar.setVisibility(View.VISIBLE);
            posterRec.setImageDrawable(null);
            titleRec.setText(video.getName());
            tvDescriptionRec.setText(video.getVideoDescribe());
            viewRec.setText(formatViews(video.getTotalView()) + " lượt xem");
            Picasso.get()
                    .load("https://img.youtube.com/vi/" + video.getLinkVideo() + "/0.jpg")
                    .placeholder(R.drawable.default_image).into(posterRec, new Callback() {
                @Override
                public void onSuccess() {
                    loadingImageProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception ex) {

                }
            });
            itemView.setOnClickListener(v -> VideoDeTailAdapter.this.onClickVideoListener.onVideoClicked(video));
        }
    }

    public class VideoHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.view)
        TextView view;
        @BindView(R.id.btnShowDetailVideo)
        ImageView btnShowDetailVideo;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;
        @BindView(R.id.underLineRating)
        View underLineRating;
        private boolean isRatingBarCreate = false;

        public VideoHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Video video) {
            title.setText(video.getName());
            view.setText(formatViews(video.getTotalView()) + " lượt xem");
            FirebaseManager.getInstance().getRatingByUidAndVid(context, PrefUtils.getUserId(context), video.getId(), ratingBar);
            if (PrefUtils.getUserId(context) != null && !PrefUtils.getUserId(context).equals("")) {
                ratingBar.setVisibility(View.VISIBLE);
                underLineRating.setVisibility(View.VISIBLE);
            } else {
                ratingBar.setVisibility(View.GONE);
                underLineRating.setVisibility(View.GONE);
            }
            btnShowDetailVideo.setOnClickListener(v -> {
                tvDescription.setText(video.getVideoDescribe());
                if (btnShowDetailClicked == false) {
                    btnShowDetailClicked = true;
                    btnShowDetailVideo.setImageResource(R.drawable.ic_arrow_drop_up);
                    tvDescription.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                } else {
                    btnShowDetailClicked = false;
                    btnShowDetailVideo.setImageResource(R.drawable.ic_arrow_drop_down);
                    tvDescription.setVisibility(View.GONE);
                    line.setVisibility(View.GONE);
                }
            });
        }

    }

    private String formatViews(long totalView) {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(dfs);

        String result = formatter.format(totalView);
        return result.substring(0, result.length() - 3);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == TYPE_HEADER) {
            View view = layoutInflater.inflate(R.layout.video_header, parent, false);
            return new VideoHeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = layoutInflater.inflate(R.layout.video_item, parent, false);
            return new VideoRecommendViewHolder(view);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Video video = videos.get(position);

        if (holder instanceof VideoHeaderViewHolder) {
            ((VideoHeaderViewHolder) holder).bind(video);
        } else if (holder instanceof VideoRecommendViewHolder) {
            ((VideoRecommendViewHolder) holder).bind(video);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return (videos == null) ? 0 : videos.size();
    }
}
