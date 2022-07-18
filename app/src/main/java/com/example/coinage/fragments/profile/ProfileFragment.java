package com.example.coinage.fragments.profile;

import android.content.Intent;
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

import com.example.coinage.LoginActivity;
import com.example.coinage.R;
import com.example.coinage.models.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.util.List;

// profile information and settings page
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private TextView tvName;
    private ConstraintLayout clLogOut;
    private ConstraintLayout clEditInfo;
    private ConstraintLayout clSetLimits;
    private TextView tvSpent;
    private TextView tvNumTransactions;

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

        clLogOut = view.findViewById(R.id.clLogOut);
        clLogOut.setOnClickListener((View v) -> logoutUser());

        clEditInfo = view.findViewById(R.id.clEditInfo);
        clEditInfo.setOnClickListener((View v) -> goEditInfo());

        clSetLimits = view.findViewById(R.id.clSetLimits);
        clSetLimits.setOnClickListener((View v) -> goSetLimits());

        tvSpent = view.findViewById(R.id.tvSpent);
        tvNumTransactions = view.findViewById(R.id.tvNumTransactions);
        fetchAndUpdateTransactionCount();
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
        fragmentTransaction.replace(R.id.frameLayout, new SetSpendingLimitsFragment());
        fragmentTransaction.commit();
    }

    private void fetchAndUpdateTransactionCount() {
        // calculate total spendings
        ParseQuery<Transaction> transactionQuery = ParseQuery.getQuery(Transaction.class);
        transactionQuery.findInBackground(new FindCallback<Transaction>() {
            BigDecimal totalSpendings = BigDecimal.valueOf(0);
            @Override
            public void done(List<Transaction> transactions, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "issue with getting transactions", e);
                    return;
                }
                for (Transaction transaction : transactions) {
                    totalSpendings = totalSpendings.add(BigDecimal.valueOf(transaction.getAmount().floatValue()));
                }
                tvSpent.setText("$" + String.format("%.2f",totalSpendings));
                // count number of transactions saved
                tvNumTransactions.setText(String.valueOf(transactions.size()));
            }
        });
    }
}