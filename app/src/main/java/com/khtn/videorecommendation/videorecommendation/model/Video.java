package com.khtn.videorecommendation.videorecommendation.model;

import java.io.Serializable;

public class Video implements Serializable {
    private String id;
    private String name;
    private String linkVideo;
    private String duration;
    private long totalView;
    private String videoDescribe;
    private boolean isRecommended = false;

    public Video() {
    }

    public Video(String name) {
        this.name = name;
    }

    public Video(String duration,  String linkVideo, String name, int totalView, String videoDescribe) {
        this.name = name;
        this.linkVideo = linkVideo;
        this.duration = duration;
        this.totalView = totalView;
        this.videoDescribe = videoDescribe;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getTotalView() {
        return totalView;
    }

    public void setTotalView(long totalView) {
        this.totalView = totalView;
    }

    public String getVideoDescribe() {
        return videoDescribe;
    }

    public void setVideoDescribe(String videoDescribe) {
        this.videoDescribe = videoDescribe;
    }

    public void setRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public boolean isRecommended() { return this.isRecommended; }
}
