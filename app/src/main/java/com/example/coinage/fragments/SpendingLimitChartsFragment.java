package com.example.coinage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.checkerframework.checker.units.qual.C;

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
        // calculate and set values
        PieChart chart = new PieChart(getContext());
        List<PieEntry> entries = new ArrayList<PieEntry>();
        entries.add(new PieEntry(categoryCumulativeAmount.floatValue()));
        Float spendingLimitDifference = spendingLimitAmount.floatValue() - categoryCumulativeAmount.floatValue();
        if (spendingLimitDifference.floatValue() < 0) {
            entries.add(new PieEntry(0));
        } else {
            entries.add(new PieEntry(spendingLimitDifference.floatValue()));
        }
        PieDataSet data = new PieDataSet(entries, "Label");
        PieData pieData = new PieData(data);
        chart.setData(pieData);
        int percentage = (int) (categoryCumulativeAmount.floatValue() / spendingLimitAmount.floatValue() * 100);
        chart.setCenterText(percentage + "%");

        // create card and other views
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.chartContainer);
        MaterialCardView materialCardView = new MaterialCardView(getContext());
        materialCardView.setMinimumHeight(300);
        materialCardView.setMinimumWidth(1500);
        materialCardView.setRadius(50);
        materialCardView.setCardBackgroundColor(getResources().getColor(R.color.yellow));
        TextView chartLabel = new TextView(getContext());
        chartLabel.setText(label + ":\n$" + String.format("%.2f", categoryCumulativeAmount));
        chartLabel.setTextSize(20);
        RelativeLayout innerLinearLayout = new RelativeLayout(getContext());
        innerLinearLayout.setMinimumHeight(300);
        innerLinearLayout.setMinimumWidth(1500);
        innerLinearLayout.setPadding(80,20,60,20);

        // add views to the linear layout
        materialCardView.addView(innerLinearLayout);
        innerLinearLayout.addView(chartLabel);
        innerLinearLayout.addView(chart);
        linearLayout.addView(materialCardView);
        Space space = new Space(getContext());
        space.setMinimumHeight(50);
        linearLayout.addView(space);

        // create chart and stylize
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.dark_yellow));
        colors.add(getResources().getColor(R.color.dark_gray));
        data.setColors(colors);
        chart.setHoleRadius(60);
        chart.setMinimumHeight(300);
        chart.setMinimumWidth(300);
        chart.setCenterTextSize(16);
        chart.setCenterTextColor(getResources().getColor(R.color.dark_gray));
        chart.setDrawHoleEnabled(true);
        chart.setHighlightPerTapEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.getData().setDrawValues(false);
        chart.invalidate();

        // set layout params for alignment
        RelativeLayout.LayoutParams chartLayoutParams = (RelativeLayout.LayoutParams) chart.getLayoutParams();
        chartLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, chart.getId());
        chart.setLayoutParams(chartLayoutParams);
        RelativeLayout.LayoutParams chartLabelLayoutParams = (RelativeLayout.LayoutParams) chartLabel.getLayoutParams();
        chartLabelLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, chartLabel.getId());
        chartLabel.setLayoutParams(chartLabelLayoutParams);
    }
}
