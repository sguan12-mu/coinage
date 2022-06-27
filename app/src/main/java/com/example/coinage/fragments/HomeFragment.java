package com.example.coinage.fragments;

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
import android.widget.Button;

import com.example.coinage.R;
import com.example.coinage.TransactionsAdapter;
import com.example.coinage.models.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private RecyclerView rvTransactions;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions;
    private Button button2;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
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

        // (placeholder button, will replace with some representation of overall spending limit)
        button2 = view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOverview();
            }
        });
    }

    private void goOverview() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new OverviewFragment());
        fragmentTransaction.commit();
    }

    private void queryTransactions() {
        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
        // set query parameters
        query.include(Transaction.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> transactions, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting transactions", e);
                    return;
                }
                // save received posts to list and notify adapter of new data
                allTransactions.addAll(transactions);
                adapter.notifyDataSetChanged();
            }
        });
    }
}