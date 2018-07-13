package com.khtn.videorecommendation.videorecommendation.home.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.database.FirebaseManager;
import com.khtn.videorecommendation.videorecommendation.home.adapter.VideoAdapter;
import com.khtn.videorecommendation.videorecommendation.home.adapter.VideoDeTailAdapter;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.OnClickVideoListener;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoDetailFragment extends Fragment implements YouTubePlayer.OnInitializedListener, OnClickVideoListener {
    private YouTubePlayerSupportFragment youTubeVideo;
    @BindView(R.id.videosInCategory)
    RecyclerView listVideoRecommended;
    @BindView(R.id.load_data_progress)
    ProgressBar loadingProgress;
    private Video video;

    private Callback callback;
    private VideoDeTailAdapter videoAdapter;
    private List<Video> listVideos;

    public VideoDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (VideoDetailFragment.Callback) context;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_detail, container, false);
        ButterKnife.bind(this, view);
        initYoutubePlayer();
        initData();
        return view;
    }

    public interface Callback {
        void onVideoClicked(Video video);
    }

    private void initData() {
        listVideos = new ArrayList<>();
        video = (Video) getActivity().getIntent().getSerializableExtra("video");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listVideoRecommended.setLayoutManager(linearLayoutManager);
        listVideoRecommended.setHasFixedSize(true);

        videoAdapter = new VideoDeTailAdapter(listVideos, this);
        listVideoRecommended.setAdapter(videoAdapter);

        loadingProgress.setVisibility(View.VISIBLE);
        FirebaseManager.getInstance()
                    .getRecommendUserIDInVideoDetailAdapter(
                                PrefUtils.getUserId(getActivity()), video, videoAdapter,
                                new VideoFragment.LoadVideosCallback() {
                                    @Override
                                    public void onLoadedVideos() {
                                        loadingProgress.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onVideosNotAvailable() {
                                        loadingProgress.setVisibility(View.GONE);
                                    }
                                });
    }

    @Override
    public void onResume() {
        super.onResume();
        videoAdapter.notifyDataSetChanged();
    }

    private void initYoutubePlayer() {
        youTubeVideo = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtubeView);
        youTubeVideo.initialize(getString(R.string.youtube_api_key), this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(video.getLinkVideo());
        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {
                youTubePlayer.pause();
            }

            @Override
            public void onLoaded(String s) {
                youTubePlayer.play();
            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {
                try {
                    FirebaseManager.getInstance().updateTotalViewsVideo(video);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onVideoClicked(Video video) {
        callback.onVideoClicked(video);
    }
}
