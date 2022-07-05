package com.example.coinage.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.coinage.R;
import com.example.coinage.models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// includes different charts and graphics for user's spending limits
public class SpendingLimitOverviewFragment extends Fragment {
    public static final String TAG = "SpendingLimitOverviewFragment";

    // private Number Overall;
    // private Number Food;
    // private Number Entertainment;
    // private Number Clothing;
    // private Number Travel;
    // private Number Housing;

    public SpendingLimitOverviewFragment() {
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

        BarChart chart2 = new BarChart(getContext());
        chart2.setMinimumHeight(500);

        calculateSpending(ParseUser.getCurrentUser());

        // chart 1
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(1, 2));
        entries.add(new BarEntry(2, 3));
        BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.yellow));
        BarData barData = new BarData(dataSet);
        BarChart chart = view.findViewById(R.id.chart);
        chart.setData(barData);
        chart.invalidate(); // refresh

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);


        // chart 2, programmatically add chart
        // LinearLayout ll = (LinearLayout) view.findViewById(R.id.chartContainer);
        // ll.addView(chart2); // add the programmatically created chart
        // chart2.setData(barData);
        // chart2.setPadding(0,20,0,0);
        // chart2.getDescription().setEnabled(false);
        // chart2.getLegend().setEnabled(false);
        // chart2.invalidate();

    }

    private void calculateSpending(ParseUser currentUser) {
        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
        query.include(Transaction.KEY_USER);
        query.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> transactions, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "issue getting transactions from backend", e);
                    return;
                }

                for (Transaction transaction : transactions) {
                    // calculate spending so far
                }
            }
        });
    }
}