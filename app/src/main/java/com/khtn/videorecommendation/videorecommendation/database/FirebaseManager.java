package com.khtn.videorecommendation.videorecommendation.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.RatingBar;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.home.adapter.VideoAdapter;
import com.khtn.videorecommendation.videorecommendation.home.view.abstracts.VideoAbstract;
import com.khtn.videorecommendation.videorecommendation.home.view.activity.HomeActivity;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.SignUpFragment;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.VideoFragment;
import com.khtn.videorecommendation.videorecommendation.model.Log;
import com.khtn.videorecommendation.videorecommendation.model.User;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;
import com.khtn.videorecommendation.videorecommendation.utils.Constants;
import com.khtn.videorecommendation.videorecommendation.utils.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "fbmnmg";
    private static FirebaseManager instance;

    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
    }

    private FirebaseManager() {
        initFirebase();
    }

    public synchronized static FirebaseManager getInstance() {
        if (instance == null) {
            synchronized (FirebaseManager.class) {
                if (instance == null) {
                    instance = new FirebaseManager();
                }
            }
        }
        return instance;
    }

    public synchronized void signUpUser(Activity activity, String id, String email, String password, SignUpFragment.SignUpCallback signUpCallback) throws Exception {
        mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                saveUser(id, new User(id, user.getUid(), email));
                                signUpCallback.onSuccess(email);
//                            Toast.makeText(activity, "Registration successful" + task.getException(),
//                                    Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
//                                signUpCallback.onFailure("Authentication failed.");
                                e.printStackTrace();
                                android.util.Log.d(TAG, "signUpUser: catch");
                            }
                        } else {
                            signUpCallback.onFailure("Authentication failed.");
                            android.util.Log.d(TAG, "signUpUser: failure");
                        }
                    });
    }

    public synchronized void saveUser(String id, User user) throws Exception {
        // add to database
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_USERS);
        mFirebaseDatabase.child(id).setValue(user);
        android.util.Log.d(TAG, "signUpUser: success");
    }

    public synchronized void signInUser(Activity activity, FragmentManager fragmentManager, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserByID(activity, fragmentManager, user.getUid());
                        } else {
                            Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    public void getRecommendUserIDInVideoAdapter(String userID, VideoAbstract videoAdapter, VideoFragment.LoadVideosCallback callback) {
        videoAdapter.setVideos(new ArrayList<>());
        getRecommendUserID(userID, videoAdapter, callback);
    }

    public void getRecommendUserIDInVideoDetailAdapter(String userID, Video video, VideoAbstract videoAdapter, VideoFragment.LoadVideosCallback callback) {
        videoAdapter.setVideos(new ArrayList<>());
        videoAdapter.getVideos().add(video);
        getRecommendUserID(userID, videoAdapter, callback);
    }

    public void getUserByID(Activity activity, FragmentManager fragmentManager, String userID) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_USERS);
        Query myQuery = mFirebaseDatabase.orderByChild("uid").equalTo(userID);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        User user = postSnapshot.getValue(User.class);
                        PrefUtils.putUserID(activity, user.getId());
                        VideoFragment videoFragment = new VideoFragment();
                        fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                    .replace(R.id.container, videoFragment, HomeActivity.HOME_FRAGMENT)
                                    .commit();
                        Toast.makeText(activity, "Login successfully", Toast.LENGTH_SHORT).show();
                        System.out.println("Login successful: " + user.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");

            }
        });
    }

    public void saveRating(Log log) throws Exception {
        // add to database
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_RATINGS);
        mFirebaseDatabase.child(log.getUserId()).child(log.getVideoId()).setValue(log.getRating());
    }

    public void getRatingByUidAndVid(Context context, String userId, String videoId, RatingBar ratingBar) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_RATINGS);
        Query myQuery = mFirebaseDatabase.child(userId).child(videoId);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Float rating = dataSnapshot.getValue(Float.class);
                    ratingBar.setRating(rating);
                } else {
                    ratingBar.setRating(0);
                }
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (rating != 0)
                            createAlertDialog(context, videoId, rating, ratingBar);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");

            }
        });
    }

    private void createAlertDialog(Context context, String videoId, float rating, RatingBar ratingBar) {
        new AlertDialog.Builder(context)
                    .setTitle("Rating")
                    .setMessage("Confirm rating for video: " + rating)
                    .setNegativeButton(context.getString(android.R.string.cancel), (dialog, which) -> {
                        ratingBar.setRating(0);
                    })
                    .setPositiveButton(context.getString(android.R.string.ok), (dialog, which) -> {
                        try {
                            FirebaseManager.getInstance().saveLog(null, new Log(PrefUtils.getUserId(context), videoId, rating));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .show();
    }

    public void getRecommendUserID(String userID, VideoAbstract videoAdapter, VideoFragment.LoadVideosCallback callback) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_RECOMMENDS);
        Query myQuery = mFirebaseDatabase.child(userID).limitToLast(Constants.LIMIT_RECOMMEND);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                android.util.Log.d(TAG, "onDataChange: start");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String value = postSnapshot.getValue(String.class);
                        Video video = new Video();
                        video.setId(value);
                        video.setRecommended(true);
                        videoAdapter.getVideos().add(video);
                        getViewByID(value, videoAdapter.getVideos().size() - 1, videoAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("mListVideoRecommend size: " + videoAdapter.getVideos().size());
                if (videoAdapter.getVideos().size() < Constants.LIMIT_RECOMMEND) {
                    android.util.Log.d(TAG, "getTopViewVideo: ");
                    getTopViewVideo(Constants.LIMIT_RECOMMEND + videoAdapter.getVideos().size(), videoAdapter, callback);
                } else {
                    callback.onLoadedVideos();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");
                callback.onVideosNotAvailable();
            }
        });
    }

    public void getViewByID(String idVideo, int index, VideoAbstract videoAdapter) {
        // Most viewed posts
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_VIDEOS);
        mFirebaseDatabase.child(idVideo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                try {
                    Video video = dataSnapshot.getValue(Video.class);
                    video.setId(key);
                    video.setRecommended(true);
                    videoAdapter.getVideos().set(index, video);
                    videoAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");

            }
        });
    }

    public void getTopViewVideo(int numberLimit, VideoAbstract videoAdapter, VideoFragment.LoadVideosCallback callback) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_VIDEOS);
        Query myQuery = mFirebaseDatabase.orderByChild(Constants.DATABASE_TOTAL_VIEW).limitToLast(numberLimit);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Video> mListVideoRecommend = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    android.util.Log.d(TAG, "getTopViewVideo: " + key);
                    try {
                        Video video = postSnapshot.getValue(Video.class);
                        video.setId(key);
                        if (Validation.containsVideoID(videoAdapter.getVideos(), video)) {
                            continue;
                        }
                        mListVideoRecommend.add(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListVideoRecommend.size() + videoAdapter.getVideos().size() == Constants.LIMIT_RECOMMEND) {
                        android.util.Log.d(TAG, "onDataChange: fuck");
                        break;
                    }
                }
                Collections.reverse(mListVideoRecommend);
                videoAdapter.getVideos().addAll(mListVideoRecommend);
                videoAdapter.notifyDataSetChanged();
                callback.onLoadedVideos();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");
                callback.onVideosNotAvailable();
            }
        });
    }

    public void saveLog(String id, Log log) throws Exception {
        // add to database
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_LOGS);
        if (id == null || id.equals("")) {
            id = mFirebaseDatabase.push().getKey();
        }
        mFirebaseDatabase.child(id).setValue(log);
        saveRating(log);
    }

    public void updateTotalViewsVideo(Video video) throws Exception {
        // add to database
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_VIDEOS);
        if (video.getId() != null && !video.getId().equals("")) {
            Map<String, Object> videoUpdates = new HashMap<>();
            videoUpdates.put(Constants.DATABASE_TOTAL_VIEW, video.getTotalView() + 1);
            mFirebaseDatabase.child(video.getId()).updateChildren(videoUpdates);
        }
    }
}
