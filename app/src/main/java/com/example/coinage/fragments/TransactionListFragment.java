package com.example.coinage.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.coinage.R;
import com.example.coinage.TransactionsAdapter;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.google.android.material.card.MaterialCardView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// home page of app, displays a list of all tracked transactions
public class TransactionListFragment extends Fragment {
    public static final String TAG = "TransactionListFragment";

    private RecyclerView rvTransactions;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions;
    private MaterialCardView cardOverview;
    private TextView tvTotalSpending;

    public TransactionListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        allTransactions = new ArrayList<>();
        adapter = new TransactionsAdapter(getContext(), allTransactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // set up recycler view
        rvTransactions.setAdapter(adapter);
        rvTransactions.setLayoutManager(linearLayoutManager);
        // populate recycler view with transactions
        queryTransactions();

        tvTotalSpending = view.findViewById(R.id.tvDateDetail);
        fetchAndUpdateTotalSpending();

        // (placeholder button, will replace with some representation of overall spending limit)
        cardOverview = view.findViewById(R.id.cardOverview);
        cardOverview.setOnClickListener((View v) -> goSpendingLimitOverview());

        // hide message if there are no transactions
        view.findViewById(R.id.emptyRvLayout).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // query again once returned to this fragment
        queryTransactions();
    }

    private void fetchAndUpdateTotalSpending() {
        // calculate total spendings
        ParseQuery<Transaction> transactionQuery = ParseQuery.getQuery(Transaction.class);
        transactionQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
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
                tvTotalSpending.setText("$" + String.format("%.2f",totalSpendings));
            }
        });
    }

    private void goSpendingLimitOverview() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, new SpendingLimitChartsFragment());
        fragmentTransaction.commit();
    }

    private void queryTransactions() {
        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
        // set query parameters
        query.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(10);
        query.addDescendingOrder("date");
        query.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> transactions, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting transactions", e);
                    return;
                }
                // save received posts to list and notify adapter of new data
                allTransactions.clear();
                if (transactions.isEmpty()) {
                    getView().findViewById(R.id.emptyRvLayout).setVisibility(View.VISIBLE);
                } else {
                    allTransactions.addAll(transactions);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}