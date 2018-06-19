package com.khtn.videorecommendation.videorecommendation.model;

public class Log {

	private String id;
	private String userId;
	private String videoId;
	private float rating;

	public Log() {
	}

	public Log(String userId, String videoId, float rating) {
		this.userId = userId;
		this.videoId = videoId;
		this.rating = rating;
	}

	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getVideoId() {
		return videoId;
	}

	public float getRating() {
		return rating;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	
}
