package com.example.coinage.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coinage.R;
import com.example.coinage.models.SpendingLimit;
import com.example.coinage.models.Transaction;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

// exporting transactions to a google sheet after user completes google oauth
public class GoogleSheetsFragment extends Fragment {
    public static final String TAG = "GoogleSheetsFragment";

    private SignInButton btnGoogleSignIn;
    private Button btnGoogleSignOut;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 0;

    public static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";
    public static final String CREDENTIALS_TYPE = "authorized_user";
    private static final String RANGE = "Sheet1!A:D";
    private static final String VALUE_INPUT_OPTION = "USER_ENTERED";
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String serverAuthCode;
    private JSONObject credentialsJSON;
    private List<List<Object>> spreadsheetValues;

    public GoogleSheetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_sheets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CLIENT_ID = getString(R.string.google_client_id);
        CLIENT_SECRET = getString(R.string.google_client_secret);

        // set create and edit spreadsheet scopes
        Scope spreadsheetScope = new Scope("https://www.googleapis.com/auth/spreadsheets");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                // request server auth code (with parameter true) to get refresh token
                .requestServerAuthCode(CLIENT_ID, true)
                .requestScopes(spreadsheetScope)
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setOnClickListener((View v) -> {
            signIn();
        });

        btnGoogleSignOut = view.findViewById(R.id.btnGoogleSignOut);
        btnGoogleSignOut.setOnClickListener((View v) -> {
            signOut();
            btnGoogleSignOut.setVisibility(View.INVISIBLE);
        });
    }

    private void signIn() {
        Log.i(TAG, "attempting signin");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(signInTask);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            serverAuthCode = account.getServerAuthCode();
            // Signed in successfully
            Log.i(TAG, "OAuth successful");

            // create the values list that will be written to google sheets
            spreadsheetValues = new ArrayList<>();
            spreadsheetValues.add(Arrays.asList("Date", "Category", "Amount", "Description"));
            // query all transactions and add them to values array
            ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
            query.whereEqualTo(SpendingLimit.KEY_USER, ParseUser.getCurrentUser());
            query.addDescendingOrder("date");
            query.findInBackground(new FindCallback<Transaction>() {
                @Override
                public void done(List<Transaction> transactions, ParseException e) {
                    // check for errors
                    if (e != null) {
                        Log.e(TAG, "Issue with getting transactions", e);
                        return;
                    }
                    for (Transaction transaction : transactions) {
                        spreadsheetValues.add(Arrays.asList(transaction.getDate(), transaction.getCategory(),
                                transaction.getAmount(), transaction.getDescription()));
                    }
                }
            });

            getView().findViewById(R.id.progressBarGoogle).setVisibility(View.VISIBLE);
            // create and write to google sheets asynchronously
            GoogleSheetsFragment.ExportTransactions exportTransactions = new GoogleSheetsFragment.ExportTransactions();
            exportTransactions.execute();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "OAuth unsuccessful, failed code=" + e.getStatusCode());
        }
    }

    private void createSpreadsheet(HttpRequestInitializer requestInitializer) throws IOException, JSONException {
        // Create the sheets API client
        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Sheets samples")
                .build();

        // Create new spreadsheet with a title
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                .setTitle("Coinage Transaction History"));

        spreadsheet = service.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();

        // Prints the new spreadsheet id
        String spreadsheetId = spreadsheet.getSpreadsheetId();
        Log.i(TAG, "Spreadsheet ID: " + spreadsheetId);

        UpdateValuesResponse result = null;

        try {
            // Updates the values in the specified range.
            ValueRange requestBody = new ValueRange()
                    .setValues(spreadsheetValues);
            result = service.spreadsheets().values().update(spreadsheetId, RANGE, requestBody)
                    .setValueInputOption(VALUE_INPUT_OPTION)
                    .execute();
            Log.i(TAG, result.getUpdatedCells() + " cells updated");
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            Log.e(TAG, String.valueOf(error.getMessage()));
        }
    }

    // export transactions to google sheets asynchronously
    private class ExportTransactions extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // use the server auth code to exchange for the refresh token
                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    TOKEN_SERVER_URL,
                    CLIENT_ID,
                    CLIENT_SECRET,
                    serverAuthCode,
                    "")  // Specify the same redirect URI that you use with your web
                    // app. If you don't have a web version of your app, you can
                    // specify an empty string.
                    .execute();
                String refreshToken = tokenResponse.getAccessToken();

                // create google credentials for the http request
                credentialsJSON = new JSONObject();
                credentialsJSON.put("type", CREDENTIALS_TYPE);
                credentialsJSON.put("refresh_token", refreshToken);
                credentialsJSON.put("client_id", CLIENT_ID);
                credentialsJSON.put("client_secret", CLIENT_SECRET);

                InputStream credentialsInputStream = new ByteArrayInputStream(credentialsJSON.toString().getBytes());
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentialsInputStream);
                HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                        googleCredentials);

                // create and populate spreadsheet
                createSpreadsheet(requestInitializer);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                Log.i(TAG, "Spreadsheet created successfully!");
                btnGoogleSignOut.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.progressBarGoogle).setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Transactions exported successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                }
            });
    }
}