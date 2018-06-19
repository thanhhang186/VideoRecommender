package com.khtn.videorecommendation.videorecommendation.home.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.VideoDetailFragment;
import com.khtn.videorecommendation.videorecommendation.model.Video;

public class VideoDetailActivity extends AppCompatActivity implements VideoDetailFragment.Callback {
    private static String DETAIL_FRAGMENT = "DeTailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        loadDetailVideoFragment();

    }

    public void loadDetailVideoFragment() {
        VideoDetailFragment videoDetailFragment = new VideoDetailFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.containerDeTail, videoDetailFragment, DETAIL_FRAGMENT).commit();
    }

    @Override
    public void onVideoClicked(Video video) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("video", video);
        startActivity(intent);
    }
}
