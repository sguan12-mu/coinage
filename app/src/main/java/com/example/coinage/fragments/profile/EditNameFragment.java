package com.example.coinage.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coinage.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseUser;

// profile information and settings page
public class EditNameFragment extends Fragment {
    public static final String TAG = "EditNameFragment";

    private TextInputEditText tiNewName;
    private Button btnChangeName;

    public EditNameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_name, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tiNewName = view.findViewById(R.id.tiNewName);
        tiNewName.setText(ParseUser.getCurrentUser().getUsername());
        btnChangeName = view.findViewById(R.id.btnChangeName);
        btnChangeName.setOnClickListener((View v) -> {
                String name = tiNewName.getText().toString();
                // update name
                ParseUser.getCurrentUser().setUsername(name);
                ParseUser.getCurrentUser().saveInBackground();
                goEditInfo();
            });
    }

    private void goEditInfo() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, new EditInfoFragment());
        fragmentTransaction.commit();
    }
}
