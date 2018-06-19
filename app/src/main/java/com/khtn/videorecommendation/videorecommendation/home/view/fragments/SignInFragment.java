package com.khtn.videorecommendation.videorecommendation.home.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.database.FirebaseManager;
import com.khtn.videorecommendation.videorecommendation.home.view.activity.HomeActivity;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.SignInView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements SignInView {
    @BindView(R.id.btnToSignUpFrag)
    TextView btnToSignUpFrag;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.edtLoginEmail)
    EditText edtLoginEmail;
    @BindView(R.id.edtLoginPass)
    EditText edtLoginPass;
    private SignInFragment.Callback callback;
    FirebaseAuth auth;

    public SignInFragment() {
        // Required empty public constructor
    }

    public interface Callback {
        void showToolbarLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        listenerClickBtn();
        showToolbarLogin();
        btnSignIn.setOnClickListener(v -> {
            signInUser();
        });
        return view;
    }

    private void signInUser() {
        String email = edtLoginEmail.getText().toString();
        String password = edtLoginPass.getText().toString();
        if (email.trim().equals("")) {
            Toast.makeText(getContext(), "Please enter valid email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.trim().equals("")) {
            Toast.makeText(getContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getActivity(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FirebaseManager.getInstance().signInUser(getActivity(), getFragmentManager(), email, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenerClickBtn() {
        btnToSignUpFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signUpFragment = new SignUpFragment();
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_enter, R.anim.left_out).replace(R.id.container, signUpFragment, HomeActivity.SIGN_UP_FRAGMENT).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (SignInFragment.Callback) context;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void showToolbarLogin() {
        callback.showToolbarLogin();
    }
}
