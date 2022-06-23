package com.example.coinage;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    // initializes parse sdk as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
