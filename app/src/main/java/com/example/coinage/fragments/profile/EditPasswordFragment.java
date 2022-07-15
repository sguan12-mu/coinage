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
import android.widget.TextView;
import android.widget.Toast;

import com.example.coinage.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.ParseException;
import com.parse.ParseUser;

// profile information and settings page
public class EditPasswordFragment extends Fragment {
    public static final String TAG = "EditPasswordFragment";

    private TextInputEditText tiCurrentPass;
    private TextInputEditText tiNewPassword;
    private TextInputEditText tiConfirmPassword;
    private Button btnChangePassword;

    public EditPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tiCurrentPass = view.findViewById(R.id.tiCurrentPass);
        tiNewPassword = view.findViewById(R.id.tiNewPassword);
        tiConfirmPassword = view.findViewById(R.id.tiConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener((View v) -> {
                String currentPassword = tiCurrentPass.getText().toString();
                String newPassword = tiNewPassword.getText().toString();
                String newPasswordConfirm = tiConfirmPassword.getText().toString();

                // verifyPassword not supported, logging in to verify:
                ParseUser user = null;
                try {
                    user = ParseUser.logIn(ParseUser.getCurrentUser().getUsername(), currentPassword);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (user == null) {
                    Toast.makeText(getContext(), "Incorrect current password", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(newPasswordConfirm)) {
                    Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else {
                    // update password
                    ParseUser.getCurrentUser().setPassword(newPassword);
                    ParseUser.getCurrentUser().saveInBackground();
                    goEditInfo();
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
