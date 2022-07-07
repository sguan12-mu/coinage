package com.example.coinage.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// track a new transaction by entering information (date, amount, category, description)
public class AddTransactionFragment extends Fragment {
    public static final String TAG = "AddTransactionFragment";

    public final Calendar myCalendar = Calendar.getInstance();
    public static final String myFormat = "MM/dd/yy";
    public final SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
    public static final String overallCategory = "Overall";
    private EditText etDate;
    private EditText etAmount;
    private EditText etCategory;
    private EditText etDescription;
    private Button btnAdd;
    private Context context;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();

        etDate = view.findViewById(R.id.etDate);
        etAmount = view.findViewById(R.id.etAmount);
        etCategory = view.findViewById(R.id.etCategory);
        etDescription = view.findViewById(R.id.etDescription);
        btnAdd = view.findViewById(R.id.btnAdd);

        // clicking the editText view for date will cause a date picker calendar to pop up
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                etDate.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        etDate.setOnClickListener((View v) ->
                new DatePickerDialog(getContext(),datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // on submit, get user input and save transaction details to backend
        btnAdd.setOnClickListener((View v) -> {
            ParseUser currentUser = ParseUser.getCurrentUser();
            BigDecimal amount = new BigDecimal(etAmount.getText().toString());
            String category = etCategory.getText().toString();
            String description = etDescription.getText().toString();
            Date date;
            try {
                date = dateFormat.parse(etDate.getText().toString());
                saveTransaction(currentUser, date, amount, category, description);
                // return to Home view after transaction is saved
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, new TransactionListFragment());
                fragmentTransaction.commit();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveTransaction(ParseUser currentUser, Date date, BigDecimal amount, String category, String description) {
        Transaction transaction = new Transaction();
        transaction.setUser(currentUser);
        transaction.setDate(date);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while adding purchase", e);
                }
                Log.i(TAG, "purchase added to database");
            }
        });
    }
}