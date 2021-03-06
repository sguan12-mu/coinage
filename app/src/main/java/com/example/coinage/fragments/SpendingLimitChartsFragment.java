package com.example.coinage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// includes different charts and graphics for user's spending limits
public class SpendingLimitChartsFragment extends Fragment {
    public static final String TAG = "SpendingLimitChartsFragment";

    public SpendingLimitChartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spending_limit_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // generate a spendings vs spending limit chart for each category
        ParseQuery<SpendingLimit> spendingLimitQuery = ParseQuery.getQuery(SpendingLimit.class);
        spendingLimitQuery.include(SpendingLimit.KEY_CATEGORY);
        spendingLimitQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
        spendingLimitQuery.findInBackground(new FindCallback<SpendingLimit>() {
            @Override
            public void done(List<SpendingLimit> spendingLimits, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "issue getting transactions from backend", e);
                    return;
                }

                // find corresponding spending amount for each spending limit category
                for (SpendingLimit spendingLimit : spendingLimits) {
                    String category = spendingLimit.getCategory();
                    BigDecimal spendingLimitAmount = BigDecimal.valueOf(spendingLimit.getAmount().floatValue());

                    ParseQuery<Transaction> transactionQuery = ParseQuery.getQuery(Transaction.class);
                    transactionQuery.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
                    if (!category.equals(Transaction.CATEGORY_OVERALL)) {
                        // don't filter by category if spending limit is for overall spendings
                        transactionQuery.whereEqualTo(Transaction.KEY_CATEGORY, category);
                    }
                    transactionQuery.findInBackground(new FindCallback<Transaction>() {
                        BigDecimal categoryCumulativeAmount = BigDecimal.valueOf(0);
                        @Override
                        public void done(List<Transaction> transactions, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "issue with getting transactions", e);
                                return;
                            }
                            for (Transaction transaction : transactions) {
                                // add all spendings in this category
                                categoryCumulativeAmount = categoryCumulativeAmount.add(BigDecimal.valueOf(transaction.getAmount().floatValue()));
                            }
                            generateChart(view, spendingLimitAmount, categoryCumulativeAmount, category);
                        }
                    });
                }
            }
        });
    }

    private void generateChart (View view, BigDecimal spendingLimitAmount, BigDecimal categoryCumulativeAmount, String label) {
        // generate a chart for each pairing
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(1, categoryCumulativeAmount.floatValue()));
        entries.add(new BarEntry(2, spendingLimitAmount.floatValue()));
        BarDataSet data = new BarDataSet(entries, "Label");
        data.setColor(getResources().getColor(R.color.yellow));
        BarData barData = new BarData(data);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.chartContainer);

        TextView chartLabel = new TextView(getContext());
        chartLabel.setText("Category: " + label);
        ll.addView(chartLabel);

        BarChart chart = new BarChart(getContext());
        ll.addView(chart);

        barData.setValueTextSize(12);
        chart.setData(barData);
        chart.setPadding(0,20,0,0);
        chart.setMinimumHeight(500);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.invalidate();

        Space space = new Space(getContext());
        space.setMinimumHeight(50);
        ll.addView(space);
    }
}
