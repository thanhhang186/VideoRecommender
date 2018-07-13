package com.khtn.videorecommendation.videorecommendation.home.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.SignInFragment;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.SignUpFragment;
import com.khtn.videorecommendation.videorecommendation.home.view.fragments.VideoFragment;
import com.khtn.videorecommendation.videorecommendation.model.Video;
import com.khtn.videorecommendation.videorecommendation.search.view.SearchActivity;
import com.khtn.videorecommendation.videorecommendation.utils.PrefUtils;

public class HomeActivity extends AppCompatActivity implements VideoFragment.Callback, SignInFragment.Callback, SignUpFragment.Callback, SignUpFragment.BackFragment {
    public static final String HOME_FRAGMENT = "HomeFragment";
    public static final String SIGN_IN_FRAGMENT = "SignInFragment";
    public static final String SIGN_UP_FRAGMENT = "SignUpFragment";
    private Toolbar toolbar;
    private Menu menu;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        loadVideoFragment();
        isLoginUser();
    }

    private boolean isLoginUser() {
        if(PrefUtils.getUserId(this)!= null && !PrefUtils.getUserId(this).equals("")){
            return true;
        }else{
            return false;
        }
    }

    private void loadVideoFragment() {
        setToolbar();
        VideoFragment videoFragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, videoFragment, HOME_FRAGMENT).commit();
    }

    private void loadBackVideoFragment() {
        setToolbar();
        VideoFragment videoFragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_enter, R.anim.right_out).replace(R.id.container, videoFragment, HOME_FRAGMENT).commit();
    }

    private void setToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            toolbarTitle.setText(R.string.app_name);
            setVisibleMenu(true);
        }
    }

    @Override
    public void onVideoClicked(Video video) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("video",video);
        startActivity(intent);
    }

    @Override
    public void showToolbarHome() {
        setToolbar();
    }

    @Override
    public void showToolbarLogin() {
        if (getSupportActionBar() != null) {
            toolbarTitle.setText(R.string.login_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            setVisibleMenu(false);
        }
    }

    @Override
    public void showToolbarRegister() {
        if (getSupportActionBar() != null) {
            toolbarTitle.setText(R.string.sign_up_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            setVisibleMenu(false);
        }
    }

    private void setVisibleMenu(boolean isVisibleMenu) {
        if (menu != null) {
            if(isLoginUser()){
                menu.getItem(1).setIcon(R.drawable.ic_check_black_24dp);
            } else {
                menu.getItem(1).setIcon(R.drawable.ic_person);
            }
            menu.findItem(R.id.user).setVisible(isVisibleMenu);
            menu.findItem(R.id.search).setVisible(isVisibleMenu);
        }
    }


    @Override
    public void onBackPressed() {
        String fragmentCurrentTag = getSupportFragmentManager().findFragmentById(R.id.container).getTag();
        if (fragmentCurrentTag.equals(SIGN_IN_FRAGMENT)) {
            loadBackVideoFragment();
        }
        if (fragmentCurrentTag.equals(SIGN_UP_FRAGMENT)) {
            loadBackSignInFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        String fragmentCurrentTag = getSupportFragmentManager().findFragmentById(R.id.container).getTag();
        if (fragmentCurrentTag.equals(SIGN_IN_FRAGMENT)) {
            setVisibleMenu(false);
        }
        if (fragmentCurrentTag.equals(SIGN_UP_FRAGMENT)) {
            setVisibleMenu(false);
        }
        if (fragmentCurrentTag.equals(HOME_FRAGMENT)) {
            setVisibleMenu(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.search:
                startSearchActivity();
                break;
            case R.id.user:
                if(isLoginUser()){
                    View menuItemView = findViewById(R.id.user);
                    showPopup(menuItemView);
                } else{
                    loadSignInFragment();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopup(View menuItemView) {
        PopupMenu popup = new PopupMenu(HomeActivity.this, menuItemView);
        popup.inflate(R.menu.menu_child);
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(HomeActivity.this, "Sign out successfully", Toast.LENGTH_SHORT).show();
                PrefUtils.putUserID(HomeActivity.this,"");
                loadVideoFragment();
                return false;
            }
        });
    }

    private void loadSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out).replace(R.id.container, signInFragment, SIGN_IN_FRAGMENT).commit();
    }

    private void loadBackSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.left_enter, R.anim.right_out).replace(R.id.container, signInFragment, SIGN_IN_FRAGMENT).commit();
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBack(String email) {
        SignInFragment signInFragment = new SignInFragment(email);
        getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                    .replace(R.id.container, signInFragment, SIGN_IN_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
    }
}
