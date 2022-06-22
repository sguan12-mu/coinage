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
                .applicationId("DZcZ5t4E5OwIQLRyPNYvprBQyD1G0Oy5nQlqrtpz")
                .clientKey("rjp5DDCIPgdeIkkh5cbW5WlTD2SxObooqcDh5FZQ")
                .server("https://parseapi.back4app.com")
                .build());
    }
}
