package com.khtn.videorecommendation.videorecommendation.home.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.khtn.videorecommendation.videorecommendation.R;
import com.khtn.videorecommendation.videorecommendation.database.FirebaseManager;
import com.khtn.videorecommendation.videorecommendation.home.view.interfaces.SignUpView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements SignUpView {
    private static final String TAG = "sign_up";
    @BindView(R.id.edtRegistEmail)
    EditText edtRegistEmail;
    @BindView(R.id.edtRegistName)
    EditText edtRegistName;
    @BindView(R.id.edtRegistPass)
    EditText edtRegistPass;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;
    FirebaseAuth auth;

    private SignUpFragment.Callback callback;

    private BackFragment backFragment;

    public SignUpFragment() {
        // Required empty public constructor
    }


    public interface Callback {
        void showToolbarRegister();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        showToolbarRegister();
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        showToolbarRegister();
        btnSignUp.setOnClickListener(v -> {
            signInUser();
        });
        return view;
    }

    private void signInUser() {
        String email = edtRegistEmail.getText().toString();
        String name = edtRegistName.getText().toString();
        String password = edtRegistPass.getText().toString();
        auth = FirebaseAuth.getInstance();
        if (email.trim().equals("")) {
            Toast.makeText(getContext(), "Please enter valid email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.trim().equals("")) {
            Toast.makeText(getContext(), "Please enter your name.", Toast.LENGTH_SHORT).show();
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
            int id = (int) (System.currentTimeMillis() / 1000);
            FirebaseManager.getInstance()
                        .signUpUser(getActivity(), id + "", email, password,
                                    new SignUpCallback() {
                                        @Override
                                        public void onSuccess(String email) {
                                            Log.d(TAG, "onSuccess: ");
                                            backFragment.onBack(email);
                                        }

                                        @Override
                                        public void onFailure(String msg) {
                                            Log.d(TAG, "onFailure: " + msg);
                                        }
                                    });
            edtRegistEmail.setText("");
            edtRegistName.setText("");
            edtRegistPass.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface SignUpCallback {

        void onSuccess(String email);

        void onFailure(String msg);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (SignUpFragment.Callback) context;
        backFragment = (BackFragment) context;
    }

    public interface BackFragment {

        void onBack(String email);
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void showToolbarRegister() {
        callback.showToolbarRegister();
    }
}
