package com.khtn.videorecommendation.videorecommendation.utils;

import com.khtn.videorecommendation.videorecommendation.model.Video;

import java.util.List;

public class Validation {

    public static boolean containsVideoID(List<Video> videos, String id) {
        for (Video video : videos) {
            if (video.getLinkVideo().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
