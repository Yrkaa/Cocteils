package com.example.cocteils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Создание переменных разметки
    ListView cocteilsList;
    TextView titleTv, noInternetTv;
    ProgressBar progressBar;


    //Метод заполнения списка на основе данных json
    private void loadCocteilsList(JSONArray cocteils){
        //Инициализация массива для названий коктейлей
        ArrayList<String> cocteilsMassive = new ArrayList<>();

        //Заполнение массива для названий коктейлей
        for (int i = 0; i < cocteils.length(); i++) {
            try {
                JSONObject cocteil = cocteils.getJSONObject(i);
                cocteilsMassive.add(cocteil.getString("strDrink"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        //Непосредственно заполнение ListView
        ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, cocteilsMassive);
        cocteilsList.setAdapter(adapter);
    }


    //Метод для обработки нажатия на 1 коктейль
    private void onCocteilClick(JSONArray cocteils){
        cocteilsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String name = ((TextView) view).getText().toString();
                    String imgUrl = cocteils.getJSONObject((int) l).getString("strDrinkThumb");
                    String id = cocteils.getJSONObject((int) l).getString("idDrink");

                    Intent toCocteilPage = new Intent(MainActivity.this, CocteilPage.class);

                    toCocteilPage.putExtra("name", name);
                    toCocteilPage.putExtra("imgUrl", imgUrl);
                    toCocteilPage.putExtra("id", id);

                    startActivity(toCocteilPage);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //Поток для получения данных с сервера, заполнения списка коктейлей, предоставления более подробной информации о каждом из них
    Thread loadDataFromApi = new Thread(new Runnable() {

        @Override
        public void run() {

            //Начало работы потока
            //Проверка наличия интернета
            ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }


            //Получение данных с api при наличии интернета
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

                //Подключение к серверу
                String url = "https://www.thecocktaildb.com/api/json/v1/1/filter.php?a=Non_Alcoholic";
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                        //Получение json в виде строки
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        //Преобразование json строки в объект
                        JSONObject jsonObject = new JSONObject(reader.readLine());

                        //Получение списка с коктейлями
                        JSONArray cocteils = jsonObject.getJSONArray("drinks");

                        //Скрываем прогресс бар
                        progressBar.setVisibility(View.INVISIBLE);

                        //Заполнение ListView названиями коктейлей и обработка нажатий на 1 коктейль
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Заполнение списка коктейлей
                                loadCocteilsList(cocteils);

                                //Обработка на нажатие каждого из них
                                onCocteilClick(cocteils);
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }


            //Обработка отсутствия интернета
            else {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        titleTv.setVisibility(View.INVISIBLE);
                        cocteilsList.setVisibility(View.INVISIBLE);
                        noInternetTv.setVisibility(View.VISIBLE);
                    }
                });
            }
            //Конец работы потока

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация переменных разметки
        cocteilsList = findViewById(R.id.cocteils_list);
        titleTv = findViewById(R.id.title_tv);
        noInternetTv = findViewById(R.id.no_internet_tv);
        progressBar = findViewById(R.id.progressBar);

        //Запуск потока для получения данных с сервера
        loadDataFromApi.start();
    }
}