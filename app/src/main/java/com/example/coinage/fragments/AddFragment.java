package com.example.coinage.fragments;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.coinage.R;
import com.example.coinage.models.Transaction;
import com.parse.SaveCallback;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddFragment extends Fragment {
    public static final String TAG = "AddFragment";

    final Calendar myCalendar= Calendar.getInstance();
    String myFormat="MM/dd/yy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
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
        // clicking the editText view for date will cause a date picker calendar to pop up
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                etDate.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etAmount = view.findViewById(R.id.etAmount);
        etCategory = view.findViewById(R.id.etCategory);
        etDescription = view.findViewById(R.id.etDescription);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal amount = new BigDecimal(etAmount.getText().toString());
                String category = etCategory.getText().toString();
                String description = etDescription.getText().toString();
                Date date;
                try {
                    date = dateFormat.parse(etDate.getText().toString());
                    saveTransaction(date, amount, category, description);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void saveTransaction(Date date, BigDecimal amount, String category, String description) {
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while adding purchase", e);
                }
                Log.i(TAG, "purchase added");
            }
        });
    }
}