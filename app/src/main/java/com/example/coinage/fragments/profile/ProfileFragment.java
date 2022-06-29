package com.example.coinage.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.coinage.LoginActivity;
import com.example.coinage.R;
import com.example.coinage.fragments.profile.EditInfoFragment;
import com.example.coinage.fragments.profile.SetLimitsFragment;
import com.parse.ParseUser;

// profile information and settings page
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private TextView tvName;
    private Button btnLogout;
    private Button btnEditInfo;
    private Button btnSetLimits;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        tvName.setText(ParseUser.getCurrentUser().getUsername());

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(onClickListener -> logoutUser());

        btnEditInfo = view.findViewById(R.id.btnEditInfo);
        btnEditInfo.setOnClickListener(onClickListener -> goEditInfo());

        btnSetLimits = view.findViewById(R.id.btnSetLimits);
        btnSetLimits.setOnClickListener(onClickListener -> goSetLimits());
    }

    public interface onClickListener{
        public default void onClick(View v) {}
    }

    private void goLoginActivity() {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

    private void logoutUser() {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            goLoginActivity();
        }
    }

    private void goEditInfo() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditInfoFragment());
        fragmentTransaction.commit();
    }

    private void goSetLimits() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new SetLimitsFragment());
        fragmentTransaction.commit();
    }
}