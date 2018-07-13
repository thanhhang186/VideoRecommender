package com.khtn.videorecommendation.videorecommendation.utils;

import com.khtn.videorecommendation.videorecommendation.model.Video;

import java.util.List;

public class Validation {

    public static boolean containsVideoID(List<Video> videos, Video vd) {
        for (Video video : videos) {
            if (video.getLinkVideo().equals(vd.getLinkVideo())) {
                return true;
            }
        }
        return false;
    }
}
