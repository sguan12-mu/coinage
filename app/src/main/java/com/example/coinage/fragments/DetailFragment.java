package com.example.coinage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coinage.R;
import com.example.coinage.models.Transaction;

public class DetailFragment extends Fragment {
    public static final String TAG = "DetailFragment";

    private TextView tvDateDetail;
    private TextView tvAmountDetail;
    private TextView tvCategoryDetail;
    private TextView tvDescriptionDetail;
    private Transaction transaction;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDateDetail = view.findViewById(R.id.tvDateDetail);
        tvAmountDetail = view.findViewById(R.id.tvAmountDetail);
        tvCategoryDetail = view.findViewById(R.id.tvCategoryDetail);
        tvDescriptionDetail = view.findViewById(R.id.tvDescriptionDetail);

        // get transaction from bundle
        Bundle bundle = getArguments();
        transaction = bundle.getParcelable(Transaction.class.getSimpleName());
        // set and display information
        tvDateDetail.setText(transaction.getDate().toString());
        tvAmountDetail.setText("$"+transaction.getAmount().toString());
        tvCategoryDetail.setText(transaction.getCategory());
        tvDescriptionDetail.setText(transaction.getDescription());
    }
}