package com.example.cocteils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CocteilPage extends AppCompatActivity {

    //Создание переменных разметки
    ImageView cocteilImageIv;
    TextView cocteilNameTv, cocteilIdTv, noInternetTv;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocteil_page);

        //Инициализация переменных разметки
        cocteilImageIv = findViewById(R.id.cocteil_image_iv);
        cocteilNameTv = findViewById(R.id.cocteil_name_tv);
        cocteilIdTv = findViewById(R.id.cocteil_id_tv);
        progressBar = findViewById(R.id.progressBar2);

        //Получаем данные из extra
        String[] cocteilData = loadExtraData();

        //Доносим полученную информацию до пользователя
        cocteilNameTv.setText(cocteilData[0]);
        cocteilIdTv.setText("Id: "+cocteilData[2]);
        //Подгружаем картинку
        Picasso.get().load(cocteilData[1]).into(cocteilImageIv);
        //Скрываем прогресс бар, когда картинка загружена
        progressBar.setVisibility(View.INVISIBLE);
    }

    //Метод для получения информации из extra
    private String[] loadExtraData(){
        String[] extraDataMassive = new String[3];

        String name = getIntent().getStringExtra("name");
        String imgUrl = getIntent().getStringExtra("imgUrl");
        String id = getIntent().getStringExtra("id");

        extraDataMassive[0] = name;
        extraDataMassive[1] = imgUrl;
        extraDataMassive[2] = id;

        return extraDataMassive;
    }
}