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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.coinage.R;
import com.example.coinage.TransactionsAdapter;
import com.example.coinage.models.EndlessScrollingViewScrollListener;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.card.MaterialCardView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// home page of app, displays a list of all tracked transactions
public class TransactionListFragment extends Fragment {
    public static final String TAG = "TransactionListFragment";
    public static final int NO_SKIP = 0;
    public static final int QUERY_LIMIT = 20;

    private RecyclerView rvTransactions;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions;
    private MaterialCardView cardOverview;
    private TextView tvTotalSpending;
    private Number overallSpendingLimit;
    private BigDecimal totalSpendings;
    private EndlessScrollingViewScrollListener scrollListener;
    private ImageButton ibGoogleSheets;

    private PieChart overallPie;

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
        // endless scrolling
        scrollListener = new EndlessScrollingViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // skip the already loaded transactions if endless scrolling
                queryTransactions(allTransactions.size());
            }
        };
        rvTransactions.addOnScrollListener(scrollListener);

        overallPie = view.findViewById(R.id.overallPie);
        overallPie.setNoDataText("");
        overallPie.invalidate();

        tvTotalSpending = view.findViewById(R.id.tvDateDetail);
        fetchAndUpdateTotalSpending();

        // (placeholder button, will replace with some representation of overall spending limit)
        cardOverview = view.findViewById(R.id.cardOverview);
        cardOverview.setOnClickListener((View v) -> goSpendingLimitOverview());

        // populate recycler view with transactions
        queryTransactions(NO_SKIP);

        // only show message if there are no transactions
        view.findViewById(R.id.emptyRvLayout).setVisibility(View.INVISIBLE);

        ibGoogleSheets = view.findViewById(R.id.ibGoogleSheets);
        ibGoogleSheets.setOnClickListener((View v) -> goGoogleSheets());
    }

    @Override
    public void onResume() {
        super.onResume();
        // query again once returned to this fragment
        queryTransactions(NO_SKIP);
    }

    private void createPieChart() {
        // find overall spending limit
        ParseQuery<SpendingLimit> spendingLimitQuery = ParseQuery.getQuery(SpendingLimit.class);
        spendingLimitQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        spendingLimitQuery.whereEqualTo(SpendingLimit.KEY_CATEGORY, Transaction.CATEGORY_OVERALL);
        spendingLimitQuery.getFirstInBackground(new GetCallback<SpendingLimit>() {
            @Override
            public void done(SpendingLimit spendingLimit, ParseException e) {
                if (spendingLimit != null) {
                    overallSpendingLimit = spendingLimit.getAmount();
                    List<PieEntry> entries = new ArrayList<PieEntry>();
                    entries.add(new PieEntry(totalSpendings.floatValue()));
                    Float spendingLimitDifference = overallSpendingLimit.floatValue() - totalSpendings.floatValue();
                    if (spendingLimitDifference.floatValue() < 0) {
                        entries.add(new PieEntry(0));
                    } else {
                        entries.add(new PieEntry(spendingLimitDifference.floatValue()));
                    }
                    PieDataSet data = new PieDataSet(entries, "Label");
                    ArrayList<Integer> colors = new ArrayList<>();
                    colors.add(getResources().getColor(R.color.dark_yellow));
                    colors.add(getResources().getColor(R.color.dark_gray));
                    data.setColors(colors);
                    PieData pieData = new PieData(data);
                    overallPie.setData(pieData);

                    overallPie.setHoleRadius(60);
                    int percentage = (int) (totalSpendings.floatValue() / overallSpendingLimit.floatValue() * 100);
                    overallPie.setCenterText(percentage + "%");
                    overallPie.setCenterTextSize(16);
                    overallPie.setCenterTextColor(getResources().getColor(R.color.dark_gray));
                    overallPie.setDrawHoleEnabled(true);
                    overallPie.setHighlightPerTapEnabled(false);
                    overallPie.getDescription().setEnabled(false);
                    overallPie.getLegend().setEnabled(false);
                    overallPie.setDrawEntryLabels(false);
                    overallPie.getData().setDrawValues(false);
                    overallPie.invalidate();
                }
            }
        });
    }

    private void fetchAndUpdateTotalSpending() {
        totalSpendings = BigDecimal.valueOf(0);
        // calculate total spendings
        ParseQuery<Transaction> transactionQuery = ParseQuery.getQuery(Transaction.class);
        transactionQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        transactionQuery.findInBackground(new FindCallback<Transaction>() {
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
                createPieChart();
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

    private void goGoogleSheets() {
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout, new GoogleSheetsFragment());
        fragmentTransaction.commit();
    }

    private void queryTransactions(int skip) {
        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
        // set query parameters
        query.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(QUERY_LIMIT);
        query.setSkip(skip);
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
                if (skip == NO_SKIP) {
                    adapter.clear();
                    // if user has no transactions, show message
                    if (transactions.isEmpty()) {
                        getView().findViewById(R.id.emptyRvLayout).setVisibility(View.VISIBLE);
                    }
                }
                allTransactions.addAll(transactions);
                adapter.notifyDataSetChanged();
            }
        });
    }
}