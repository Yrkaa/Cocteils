package com.example.cocteils;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IngredientsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IngredientsFragment(String cocteilId) {
        // Required empty public constructor
        this.cocteilId = cocteilId;
    }

    public IngredientsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IngredientsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IngredientsFragment newInstance(String param1, String param2) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    String cocteilId;

    //Создаем переменные для эл. разметки
    TextView ingredientsTv;
    ProgressBar progressBar3;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Инициализация переменных эл разметки
        ingredientsTv = view.findViewById(R.id.ingredients_tv);
        progressBar3 = view.findViewById(R.id.progressBar3);

        //Запускаем поток для получения списка ингредиентов
        getCocteilIngredients.start();
    }

    //Поток для получения списка ингредиентов
    Thread getCocteilIngredients = new Thread(new Runnable() {
        @Override
        public void run() {
            String ingredients = "";

            HttpURLConnection connection = null;

            try {
                //Подключаемся к серверу
                connection = (HttpURLConnection) new URL("https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i="+cocteilId).openConnection();
                connection.connect();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    //Получаем json
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    //Обрабатываем json
                    JSONObject json = new JSONObject(reader.readLine());
                    JSONArray array = json.getJSONArray("drinks");
                    JSONObject cocteilData = array.getJSONObject(0);

                    //Заполняем список ингредиентов
                    for(int i = 1; i < 15; i++){
                        String ingredient = cocteilData.getString("strIngredient"+i);
                        if (ingredient != "null")
                            ingredients += " "+ingredient+",";
                        else
                            break;

                    }
                    connection.disconnect();
                    progressBar3.setVisibility(View.INVISIBLE);

                    String finalIngredients = ingredients;

                    //Отображаем список ингредиентов
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ingredientsTv.setText(ingredientsTv.getText()+ finalIngredients.substring(0, finalIngredients.length()-1));
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    });
}