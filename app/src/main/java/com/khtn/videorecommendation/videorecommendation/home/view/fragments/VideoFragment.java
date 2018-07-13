package com.khtn.videorecommendation.videorecommendation.home.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.database.FirebaseManager;
import com.khtn.videorecommendation.videorecommendation.home.adapter.VideoAdapter;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.HomeView;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.OnClickVideoListener;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoFragment extends Fragment implements HomeView, OnClickVideoListener {
    @BindView(R.id.primary_layout)
    RelativeLayout primaryLayout;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.load_data_progress)
    ProgressBar loadDataProgress;
    private Callback callback;

    private List<Video> videos;
    private OnFragmentInteractionListener mListener;
    private VideoAdapter videoCardAdapter;

    public VideoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        showToolbarHome();
        initLayoutReferences();
    }

    private void initLayoutReferences() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        videos = new ArrayList<>();
        videoCardAdapter = new VideoAdapter(this, videos);
        recyclerView.setAdapter(videoCardAdapter);

        loadDataProgress.setVisibility(View.VISIBLE);
        FirebaseManager.getInstance()
                    .getRecommendUserIDInVideoAdapter(
                                PrefUtils.getUserId(getActivity()),
                                videoCardAdapter,
                                new LoadVideosCallback() {
                                    @Override
                                    public void onLoadedVideos() {
                                        loadDataProgress.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onVideosNotAvailable() {
                                        loadDataProgress.setVisibility(View.GONE);
                                    }
                                }
                    );
    }

    public interface LoadVideosCallback {

        void onLoadedVideos();

        void onVideosNotAvailable();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (Callback) context;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }


    public interface Callback {
        void onVideoClicked(Video video);

        void showToolbarHome();
    }

    @Override
    public void displayHome() {
        new Handler().postDelayed(() -> {
            hideLoadingProgress();
        }, 1000);
    }

    @Override
    public void loadVideoFragment() {

    }

    @Override
    public void showLoadingProgress() {
        primaryLayout.setVisibility(View.INVISIBLE);
        loadDataProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingProgress() {
        loadDataProgress.setVisibility(View.GONE);
        primaryLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoClicked(Video video) {
        callback.onVideoClicked(video);
    }

    @Override
    public void showToolbarHome() {
        callback.showToolbarHome();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu, menu);
//        MenuItem item_search = menu.findItem(R.id.item_search);
//        item_search.setIcon(R.drawable.ic_search_black_24dp);
//        SearchView searchView = (SearchView) item_search.getActionView();
//        searchView.setOnQueryTextListener(this);
//        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
//        closeButton.setOnClickListener(v -> {
//            searchView.setQuery("", false);
//            recyclerView.setAdapter(videoCategoryAdapter);
//        });
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_user:
//                SignInFragment signInFragment = new SignInFragment();
//                getFragmentManager().beginTransaction().replace(R.id.container, signInFragment, SIGNIN_FRAGMENT).addToBackStack(null).commit();
//                break;
//            case R.id.item_search:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    // Search
//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return true;
//    }

//    @Override
//    public boolean onQueryTextChange(String newText) {
//        if (newText == null || newText.trim().isEmpty()) {
//            resetSearch();
//        } else {
//            videoFilterList = new ArrayList<>();
//            for (Video video : videoList) {
//                if (video.getTitle().toLowerCase().contains(newText.toLowerCase())) {
//                    videoFilterList.add(video);
//                }
//            }
//            //setAdapter
//            videoFilterAdapter = new VideoDeTailAdapter(videoFilterList, this);
//            recyclerView.setAdapter(videoFilterAdapter);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onMenuItemActionExpand(MenuItem item) {
//        return false;
//    }
//
//    @Override
//    public boolean onMenuItemActionCollapse(MenuItem item) {
//        return false;
//    }
//
//    private void resetSearch() {
//        videoFilterList.clear();
//        recyclerView.setAdapter(null);
//        //setAdapter
//
//    }

}
