package com.example.coinage;

import android.app.Application;

import com.example.coinage.models.Budget;
import com.example.coinage.models.Transaction;
import com.parse.Parse;
import com.parse.ParseObject;

// initializes parse sdk as soon as the application is created
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models
        ParseObject.registerSubclass(Transaction.class);
        ParseObject.registerSubclass(Budget.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
