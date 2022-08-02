package com.example.coinage.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.coinage.MainActivity;
import com.example.coinage.R;
import com.example.coinage.models.Transaction;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
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
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    private EditText etDate;
    private TextInputEditText tiAmount;
    private AutoCompleteTextView tiCategory;
    private TextInputEditText tiDescription;
    private Button btnAdd;
    private ImageView ivScan;
    private Context context;

    private PyObject merchant;
    private PyObject date;
    private PyObject total;
    private PyObject confidence;

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

        tiAmount = view.findViewById(R.id.tiAmount);
        etDate = view.findViewById(R.id.etDate);

        tiCategory = view.findViewById(R.id.tiCategory);
        tiDescription = view.findViewById(R.id.tiDescription);
        btnAdd = view.findViewById(R.id.btnAdd);
        ivScan = view.findViewById(R.id.ivScan);

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

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // prevent keyboard from popping up
                if(hasFocus) {
                    etDate.setInputType(InputType.TYPE_NULL);
                    new DatePickerDialog(getContext(),datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        // category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categoriesAdd,
                R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiCategory.setAdapter(adapter);
        // disable keyboard input
        tiCategory.setInputType(InputType.TYPE_NULL);

        // on submit, get user input and save transaction details to backend
        btnAdd.setOnClickListener((View v) -> {
            ParseUser currentUser = ParseUser.getCurrentUser();
            String stringDate = etDate.getText().toString();
            if (stringDate.equals("")) {
                Toast.makeText(getContext(), "Date of purchase should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String stringAmount = tiAmount.getText().toString();
            if (stringAmount.equals("")) {
                Toast.makeText(getContext(), "Purchase cost should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String category = tiCategory.getEditableText().toString();
            if (category.equals("")) {
                Toast.makeText(getContext(), "Category should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            String description = tiDescription.getText().toString();
            if (description.equals("")) {
                Toast.makeText(getContext(), "Description should not be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            Date date;
            try {
                date = dateFormat.parse(stringDate);
                BigDecimal amount = new BigDecimal(stringAmount);
                saveTransaction(currentUser, date, amount, category, description);
                // return to Home view after transaction is saved
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                // show dialog
                new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                        .setTitle("How did you feel about that purchase?")
                        .setNeutralButton("Regretting it...", (dialog, which) -> {
                            Log.i(TAG, "User is unhappy with purchase");
                        })
                        .setPositiveButton("Good buy!", (dialog, which) -> {
                            Log.i(TAG, "User is happy with purchase");
                        })
                        .show();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        });

        // scan a receipt
        ivScan.setOnClickListener((View v) -> {
            scanReceipt();
        });

        // if redirected to this fragment via long clicking, scan receipt
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getBoolean(MainActivity.class.getSimpleName())) {
            scanReceipt();
        }
    }

    // prevent first text field from automatically being selected
    @Override
    public void onResume() {
        super.onResume();
        View current = getActivity().getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    // async call to receipt scanner
    private class ApiCall extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            if (! Python.isStarted()) {
                Python.start(new AndroidPlatform(context));
            }
            Python py = Python.getInstance();
            PyObject receiptScanner = py.getModule("receiptScanner");
            receiptScanner.callAttr("apiResults", photoFile.getAbsolutePath());
            // get desired fields from receipt scanner
            merchant = receiptScanner.callAttr("getMerchant");
            date = receiptScanner.callAttr("getDate");
            total = receiptScanner.callAttr("getTotal");
            // confidence is low if all fields empty, medium if one or two are, high if none are
            confidence = receiptScanner.callAttr("getConfidence");
            return (confidence.toString());
        }

        @Override
        protected void onPostExecute(String confidence) {
            if (confidence.equals("low")) {
                Log.i(TAG, "receipt scanner confidence low");
                Toast.makeText(context,"Failed to scan receipt, retake photo for better results!", Toast.LENGTH_SHORT).show();
            } else if (confidence.equals("medium")) {
                Log.i(TAG, "receipt scanner confidence medium");
                Toast.makeText(context,"Receipt unclear, retake photo for better results!", Toast.LENGTH_SHORT).show();
            }
            // update add transactions form with api results
            getView().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            try {
                etDate.setText(dateFormat.format(dateFormat.parse(date.toString())));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            tiAmount.setText(total.toString());
            tiDescription.setText(merchant.toString() + " purchase");
        }
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

    private void scanReceipt() {
        // launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a file reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap file object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // safe to use intent if result is not null
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // start image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

        getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        // async api call
        ApiCall apiCall = new ApiCall();
        apiCall.execute();
    }

    // returns file for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // get safe storage director for photos
        // use getExternalFilesDir on Context to access package-specific directories
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}