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
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableSet;

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
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String serverAuthCode;
    private JSONObject credentialsJSON;

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
            // create a spreadsheet asynchronously
            GoogleSheetsFragment.CreateSpreadsheet createSpreadsheet = new GoogleSheetsFragment.CreateSpreadsheet();
            createSpreadsheet.execute();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "OAuth unsuccessful, failed code=" + e.getStatusCode());
        }
    }

    // create a spreadsheet asynchronously
    private class CreateSpreadsheet extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
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
                credentialsJSON.put("type", "authorized_user");
                credentialsJSON.put("refresh_token", refreshToken);
                credentialsJSON.put("client_id", CLIENT_ID);
                credentialsJSON.put("client_secret", CLIENT_SECRET);

                InputStream credentialsInputStream = new ByteArrayInputStream(credentialsJSON.toString().getBytes());
                GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentialsInputStream);
                HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                        googleCredentials);

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
                Log.i(TAG, "Spreadsheet ID: " + spreadsheet.getSpreadsheetId());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute() {
            Log.i(TAG, "Spreadsheet created successfully!");
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