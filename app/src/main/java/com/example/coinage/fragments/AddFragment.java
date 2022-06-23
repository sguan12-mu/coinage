package com.example.coinage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.coinage.R;

import java.util.Date;

public class AddFragment extends Fragment {
    public static final String TAG = "AddFragment";

    private EditText etDate;
    private EditText etAmount;
    private EditText etCategory;
    private EditText etDescription;
    private Button btnAdd;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDate = view.findViewById(R.id.etDate);
        etAmount = view.findViewById(R.id.etAmount);
        etCategory = view.findViewById(R.id.etCategory);
        etDescription = view.findViewById(R.id.etDescription);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = java.sql.Date.valueOf(etDate.getText().toString());
                Float amount = Float.valueOf(etAmount.getText().toString());
                String category = etCategory.getText().toString();
                String description = etDescription.getText().toString();
            }
        });


    }
}