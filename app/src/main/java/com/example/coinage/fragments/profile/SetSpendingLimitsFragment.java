package com.example.coinage.fragments.profile;

import android.content.Context;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coinage.MainActivity;
import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseException;

import java.math.BigDecimal;

public class SetSpendingLimitsFragment extends Fragment {
    public static final String TAG = "SetLimitsFragment";

    private AutoCompleteTextView tiCategoryLimit;
    private TextInputEditText tiAmountLimit;
    private Button btnSetLimit;
    private Context context;

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

        context = getContext();

        tiCategoryLimit = view.findViewById(R.id.tiCategoryLimit);
        tiAmountLimit = view.findViewById(R.id.tiAmountLimit);
        btnSetLimit = view.findViewById(R.id.btnSetLimit);

        // category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories,
                R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiCategoryLimit.setAdapter(adapter);

        btnSetLimit.setOnClickListener((View v) -> {
            ParseUser currentUser = ParseUser.getCurrentUser();
            String category = tiCategoryLimit.getEditableText().toString();
            if (category.equals("")) {
                Toast.makeText(getContext(), "Category should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String stringAmount = tiAmountLimit.getText().toString();
            if (stringAmount.equals("")) {
                Toast.makeText(getContext(), "Spending limit amount should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            BigDecimal amount = new BigDecimal(stringAmount);
            saveLimit(currentUser, amount, category);
            // go to profile fragment
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_profile);
        });
    }

    private void saveLimit(ParseUser currentUser, BigDecimal amount, String category) {
        ParseQuery<SpendingLimit> spendingLimitQuery = ParseQuery.getQuery(SpendingLimit.class);
        spendingLimitQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        spendingLimitQuery.whereEqualTo(SpendingLimit.KEY_CATEGORY, category);
        spendingLimitQuery.getFirstInBackground(new GetCallback<SpendingLimit>() {
            @Override
            public void done(SpendingLimit spendingLimit, ParseException e) {
                if (spendingLimit == null) {
                    // if spending limit for that category doesn't already exist, make a new one
                    spendingLimit = new SpendingLimit();
                    spendingLimit.setUser(currentUser);
                    spendingLimit.setCategory(category);
                }
                // if spending limit exists, just adjust the amount
                Toast.makeText(context, "Updating existing spending limit", Toast.LENGTH_SHORT).show();
                spendingLimit.setAmount(amount);
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
        });
    }
}