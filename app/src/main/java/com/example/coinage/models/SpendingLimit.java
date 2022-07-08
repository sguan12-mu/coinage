package com.example.coinage.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("SpendingLimit")
public class SpendingLimit extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_CATEGORY = "category";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
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
}
