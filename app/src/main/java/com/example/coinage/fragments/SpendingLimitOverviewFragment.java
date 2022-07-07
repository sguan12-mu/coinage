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
import com.example.coinage.models.Budget;
import com.example.coinage.models.Spending;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// includes different charts and graphics for user's spending limits
public class SpendingLimitOverviewFragment extends Fragment {
    public static final String TAG = "SpendingLimitOverviewFragment";

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

        // generate a spendings vs budget chart for each category
        ParseQuery<Budget> query = ParseQuery.getQuery(Budget.class);
        query.include(Budget.KEY_CATEGORY);
        query.findInBackground(new FindCallback<Budget>() {
            @Override
            public void done(List<Budget> budgets, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "issue getting transactions from backend", e);
                    return;
                }

                // find corresponding spending amount for each budget category
                for (Budget budget : budgets) {
                    String category = budget.getCategory();
                    BigDecimal budgetAmount = BigDecimal.valueOf(budget.getAmount().floatValue());

                    ParseQuery<Spending> query = ParseQuery.getQuery(Spending.class);
                    query.whereEqualTo(Spending.KEY_CATEGORY, category);
                    query.getFirstInBackground(new GetCallback<Spending>() {
                        BigDecimal spendingAmount;
                        @Override
                        public void done(Spending spending, ParseException e) {
                           if (e != null) {
                               Log.e(TAG, "issue with getting spending amounts", e);
                               return;
                           }
                           if (spending == null) {
                               // spendings in this category is 0
                               spendingAmount = BigDecimal.valueOf(0);
                           } else {
                               // spendings exist in this category
                               spendingAmount = BigDecimal.valueOf(spending.getAmount().floatValue());
                           }
                           generateChart(view, budgetAmount, spendingAmount);
                        }
                    });
                }
            }
        });
    }

    private void generateChart (View view, BigDecimal budgetAmount, BigDecimal spendingAmount) {
        // generate a chart for each pairing
        List<BarEntry> entries = new ArrayList<BarEntry>();
        entries.add(new BarEntry(1, spendingAmount.floatValue()));
        entries.add(new BarEntry(2, budgetAmount.floatValue()));
        BarDataSet data = new BarDataSet(entries, "Label");
        data.setColor(getResources().getColor(R.color.yellow));
        BarData barData = new BarData(data);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.chartContainer);
        BarChart chart = new BarChart(getContext());
        ll.addView(chart);

        chart.setData(barData);
        chart.setPadding(0,20,0,0);
        chart.setMinimumHeight(500);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }
}
