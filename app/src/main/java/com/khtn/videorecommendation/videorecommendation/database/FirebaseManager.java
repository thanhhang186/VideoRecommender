package com.khtn.videorecommendation.videorecommendation.database;

import android.app.Activity;
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
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.VideoFragment;
import com.khtn.videorecommendation.videorecommendation.model.Log;
import com.khtn.videorecommendation.videorecommendation.model.User;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;
import com.khtn.videorecommendation.videorecommendation.utils.Constants;
import com.khtn.videorecommendation.videorecommendation.utils.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseManager {
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

    public synchronized void signUpUser(Activity activity, String username, String email, String password) throws Exception {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        try {
                            saveUser(user.getUid(), new User(username, email));
                            Toast.makeText(activity, "Registration successful" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public synchronized void saveUser(String id, User user) throws Exception {
        // add to database
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_USERS);
        mFirebaseDatabase.child(id).setValue(user);
    }

    public synchronized void signInUser(Activity activity, FragmentManager fragmentManager, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        PrefUtils.putUserID(activity, user.getUid());
                        VideoFragment videoFragment = new VideoFragment();
                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out).replace(R.id.container, videoFragment, HomeActivity.HOME_FRAGMENT).commit();
                        Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getRecommendUserIDInVideoAdapter(String userID, VideoAbstract videoAdapter) {
        videoAdapter.setVideos(new ArrayList<>());
        getRecommendUserID(userID, videoAdapter);
    }

    public void getRecommendUserIDInVideoDetailAdapter(String userID, Video video, VideoAbstract videoAdapter) {
        videoAdapter.setVideos(new ArrayList<>());
        videoAdapter.getVideos().add(video);
        getRecommendUserID(userID, videoAdapter);
    }

    public void getRecommendUserID(String userID, VideoAbstract videoAdapter) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_RECOMMENDS);
        Query myQuery = mFirebaseDatabase.child(userID).limitToLast(Constants.LIMIT_RECOMMEND);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String value = postSnapshot.getValue(String.class);
                        Video video = new Video();
                        video.setId(value);
                        videoAdapter.getVideos().add(video);
                        getViewByID(value, videoAdapter.getVideos().size() - 1, videoAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("mListVideoRecommend size: " + videoAdapter.getVideos().size());
                if (videoAdapter.getVideos().size() < Constants.LIMIT_RECOMMEND) {
                    getTopViewVideo(Constants.LIMIT_RECOMMEND + videoAdapter.getVideos().size(), videoAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");

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

    public void getTopViewVideo(int numberLimit, VideoAbstract videoAdapter) {
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_VIDEOS);
        Query myQuery = mFirebaseDatabase.orderByChild(Constants.DATABASE_TOTAL_VIEW).limitToLast(numberLimit);
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Video> mListVideoRecommend = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (Validation.containsVideoID(videoAdapter.getVideos(), key)) {
                        continue;
                    }
                    try {
                        Video video = postSnapshot.getValue(Video.class);
                        video.setId(key);
                        mListVideoRecommend.add(video);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mListVideoRecommend.size() + videoAdapter.getVideos().size() == Constants.LIMIT_RECOMMEND) {
                        break;
                    }
                }
                Collections.reverse(mListVideoRecommend);
                videoAdapter.getVideos().addAll(mListVideoRecommend);
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("onCancelled");

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
    }
}
