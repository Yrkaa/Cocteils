package com.example.cocteils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView cocteilsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cocteilsList = findViewById(R.id.cocteils_list);
    }

    Thread loadDataFromApi = new Thread(new Runnable() {
        @Override
        public void run() {
            ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
                String url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic";
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    if(HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });
}