package com.example.coinage.fragments.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.coinage.R;
import com.parse.ParseUser;

public class EditInfoFragment extends Fragment {
    public static final String TAG = "EditInfoFragment";

    private TextView tvNameEditDisplay;
    private TextView tvNameEdit;
    private ConstraintLayout clEmailEdit;
    private ConstraintLayout clPasswordEdit;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_info, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNameEditDisplay = view.findViewById(R.id.tvNameEditDisplay);
        tvNameEditDisplay.setText(ParseUser.getCurrentUser().getUsername());

        tvNameEdit = view.findViewById(R.id.tvNameEdit);
        tvNameEdit.setOnClickListener(onClickListener -> {
                Log.i(TAG, "go to edit name fragment to change account username");
                goEditName();
            });

        clEmailEdit = view.findViewById(R.id.clEmailEdit);
        clEmailEdit.setOnClickListener(onClickListener -> {
                Log.i(TAG, "go to edit email fragment to change account email");
                goEditEmail();
            });

        clPasswordEdit = view.findViewById(R.id.clPasswordEdit);
        clPasswordEdit.setOnClickListener(onClickListener -> {
            Log.i(TAG, "go to edit password fragment to change account password");
            goEditPassword();
        });

    }

    public interface onClickListener{
        public default void onClick(View v) {}
    }

    @Override
    public void onResume() {
        super.onResume();
        // minimize keyboard input once returned to this fragment
        InputMethodManager inputMethodManager =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void goEditName() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditNameFragment());
        fragmentTransaction.commit();
    }

    private void goEditEmail() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditEmailFragment());
        fragmentTransaction.commit();
    }

    private void goEditPassword() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditPasswordFragment());
        fragmentTransaction.commit();
    }
}