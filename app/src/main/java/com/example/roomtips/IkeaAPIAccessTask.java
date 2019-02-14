package com.example.roomtips;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

public class IkeaAPIAccessTask extends AsyncTask<String, Void, ArrayList<Product>> {
    private final String TAG = "IkeaAPIAccessTask";

    @Override
    protected ArrayList<Product> doInBackground(String... params) {
        FurnitureFragment.products = NativeAPIKt.getSuggestionsIkea(params[0], 10, 0, 9999, false, 4);
        return FurnitureFragment.products;
    }

    @Override
    protected void onPostExecute(ArrayList<Product> products) {
        for (Product product : products) {
            Log.d(TAG, "Found: " + product.getName());
        }
        Log.d("OUR PRODUCTS", "HELLO TEST FIRST");
        FurnitureFragment.products = products;
        Log.d("OUR PRODUCTS", "HELLO TEST");
        Log.d("OUR PRODUCTS", "" + FurnitureFragment.products);
        Log.d("OUR PRODUCTS", "HELLO TEST THIRD");
        FurnitureFragment.adapter.mValues = FurnitureFragment.products;
        FurnitureFragment.adapter.notifyDataSetChanged();
        Log.d("OUR PRODUCTS", "HELLO TEST LAST");
    }
}
