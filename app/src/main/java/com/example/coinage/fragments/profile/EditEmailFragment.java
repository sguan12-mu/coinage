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
public class EditEmailFragment extends Fragment {
    public static final String TAG = "EditEmailFragment";

    private TextInputEditText tiCurrentEmail;
    private TextInputEditText tiNewEmail;
    private TextInputEditText tiConfirmEmail;
    private Button btnChangeEmail;

    public EditEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_email, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tiCurrentEmail = view.findViewById(R.id.tiCurrentEmail);
        tiNewEmail = view.findViewById(R.id.tiNewEmail);
        tiConfirmEmail = view.findViewById(R.id.tiConfirmEmail);
        btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
        btnChangeEmail.setOnClickListener((View v) -> {
                String currentEmail = tiCurrentEmail.getText().toString();
                String newEmail = tiNewEmail.getText().toString();
                String newEmailConfirm = tiConfirmEmail.getText().toString();
                if (!currentEmail.equals(ParseUser.getCurrentUser().getEmail())) {
                    Toast.makeText(getContext(), "Incorrect current email", Toast.LENGTH_SHORT).show();
                } else if (!newEmail.equals(newEmailConfirm)) {
                    Toast.makeText(getContext(), "Emails don't match", Toast.LENGTH_SHORT).show();
                } else {
                    // update email
                    ParseUser.getCurrentUser().setEmail(newEmail);
                    ParseUser.getCurrentUser().saveInBackground();
                    goEditInfo();
                }
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
