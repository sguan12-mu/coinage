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
import com.parse.ParseUser;

// profile information and settings page
public class EditEmailFragment extends Fragment {
    public static final String TAG = "EditEmailFragment";

    private EditText etCurrentEmail;
    private EditText etNewEmail;
    private EditText etNewEmailConfirm;
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

        etCurrentEmail = view.findViewById(R.id.etCurrentPassword);
        etNewEmail = view.findViewById(R.id.etNewPassword);
        etNewEmailConfirm = view.findViewById(R.id.etNewPasswordConfirm);
        btnChangeEmail = view.findViewById(R.id.btnChangePassword);
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEmail = etCurrentEmail.getText().toString();
                String newEmail = etNewEmail.getText().toString();
                String newEmailConfirm = etNewEmailConfirm.getText().toString();
                if (!currentEmail.equals(ParseUser.getCurrentUser().getEmail())) {
                    Toast.makeText(getContext(), "Incorrect current email", Toast.LENGTH_SHORT).show();
                }
                else if (!newEmail.equals(newEmailConfirm)) {
                    Toast.makeText(getContext(), "Emails don't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    // update email
                    ParseUser.getCurrentUser().setEmail(newEmail);
                    ParseUser.getCurrentUser().saveInBackground();
                    goEditInfo();
                }
            }
        });
    }

    private void goEditInfo() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new EditInfoFragment());
        fragmentTransaction.commit();
    }
}
