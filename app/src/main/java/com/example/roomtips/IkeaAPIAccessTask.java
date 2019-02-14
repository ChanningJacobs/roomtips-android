package com.example.roomtips;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

public class IkeaAPIAccessTask extends AsyncTask<String, Void, ArrayList<Product>> {
    private final String TAG = "IkeaAPIAccessTask";

    @Override
    protected ArrayList<Product> doInBackground(String... params) {
        FurnitureFragment.products = NativeAPIKt.getSuggestionsIkea(params[0], 10, 0, 9999, true, 2);
        return FurnitureFragment.products;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> products) {
        for (Product product : products) {
            Log.d(TAG, "Found: " + product.getName());
        }
    }
}
