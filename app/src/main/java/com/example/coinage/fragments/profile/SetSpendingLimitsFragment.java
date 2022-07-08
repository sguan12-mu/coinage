package com.example.coinage.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseException;

import java.math.BigDecimal;

public class SetSpendingLimitsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "SetLimitsFragment";

    private Spinner sCategories;
    private EditText etSetAmount;
    private Button btnSetLimit;

    public SetSpendingLimitsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_spending_limits, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sCategories = view.findViewById(R.id.sCategories);
        etSetAmount = view.findViewById(R.id.etSetAmount);
        btnSetLimit = view.findViewById(R.id.btnSetLimit);

        // category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategories.setAdapter(adapter);
        sCategories.setOnItemSelectedListener(this);

        btnSetLimit.setOnClickListener((View v) -> {
            ParseUser currentUser = ParseUser.getCurrentUser();
            BigDecimal amount = new BigDecimal(etSetAmount.getText().toString());
            String category = sCategories.getSelectedItem().toString();
            saveLimit(currentUser, amount, category);
            goProfile();
        });
    }

    private void goProfile() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new ProfileFragment());
        fragmentTransaction.commit();
    }

    private void saveLimit(ParseUser currentUser, BigDecimal amount, String category) {
        SpendingLimit spendingLimit = new SpendingLimit();
        spendingLimit.setUser(currentUser);
        spendingLimit.setAmount(amount);
        spendingLimit.setCategory(category);
        spendingLimit.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving spending limit", e);
                }
                Log.i(TAG, "spending limit added to database");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, String.valueOf(parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}