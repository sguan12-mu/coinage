package com.example.coinage.models;

import android.icu.text.SimpleDateFormat;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Transaction")
public class Transaction extends ParseObject {
    public static final String myFormat="MM/dd/yy";
    public final SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
    public static final String KEY_USER = "user";
    public static final String KEY_DATE = "date";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_DESCRIPTION = "description";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getDate() {
        return String.valueOf(dateFormat.format(getDate(KEY_DATE)));
    }

    public void setDate(Date date) {
        put(KEY_DATE, date);
    }

    public Number getAmount() {
        return getNumber(KEY_AMOUNT);
    }

    public void setAmount(Number amount) {
        put(KEY_AMOUNT, amount);
    }

    public String getCategory() {
        return getString(KEY_CATEGORY);
    }

    public void setCategory(String category) {
        put(KEY_CATEGORY, category);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }
}
