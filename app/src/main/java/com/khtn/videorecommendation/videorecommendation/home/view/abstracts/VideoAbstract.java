package com.khtn.videorecommendation.videorecommendation.home.view.abstracts;

import android.support.v7.widget.RecyclerView;

import com.khtn.videorecommendation.videorecommendation.model.Video;

import java.util.List;

public abstract class VideoAbstract extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public List<Video> videos;

    public abstract List<Video> getVideos();

    public abstract void setVideos(List<Video> videos);
}
