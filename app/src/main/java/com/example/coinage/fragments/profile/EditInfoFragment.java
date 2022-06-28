package com.example.coinage.fragments.profile;

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
import android.widget.TextView;

import com.example.coinage.R;
import com.parse.ParseUser;

public class EditInfoFragment extends Fragment {
    public static final String TAG = "EditInfoFragment";

    private TextView tvNameEditDisplay;
    private TextView tvNameEdit;
    private ConstraintLayout clEmailEdit;

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
        tvNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change name
            }
        });

        clEmailEdit = view.findViewById(R.id.clEmailEdit);
        clEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "change email");
                goEditEmail();
            }
        });

    }

    private void goEditEmail() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditEmailFragment());
        fragmentTransaction.commit();
    }
}