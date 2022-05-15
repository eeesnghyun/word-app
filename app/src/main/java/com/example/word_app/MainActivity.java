package com.example.word_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String DATA_URL = "https://eeesnghyun.github.io/word-app/data.json";
    private final static String FLOWER_URL = "https://eeesnghyun.github.io/word-app/flower.json";

    RecyclerView recyclerView;
    WordAdapter wordAdapter;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Loading
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        recyclerView = findViewById(R.id.recyclerView);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        wordAdapter = new WordAdapter();
        recyclerView.setAdapter(wordAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeRequest(DATA_URL);
    }

    //서버로 데이터 요청
    public void makeRequest(String dataUrl) {
        String url = dataUrl;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("응답-> " + response);

                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러-> " + error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);

        println("요청 보냄");
    }

    public void processResponse(String response) {
        Gson gson = new Gson();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("dataList");

            List<WordEntity> list = new ArrayList<WordEntity>();

            for (int i = 0; i < jsonArray.length(); i++) {
                WordEntity word = gson.fromJson(jsonArray.get(i).toString(), WordEntity.class);
                list.add(word);
            }

            Collections.shuffle(list);      //응답받은 데이터를 랜덤으로 배열

            for (WordEntity word : list) {
                wordAdapter.addItem(word);
            }

            wordAdapter.notifyDataSetChanged();

            //println("데이터 카운트 : " + jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void println(String data) {
        Log.d("MainActivity", data);
    }
}