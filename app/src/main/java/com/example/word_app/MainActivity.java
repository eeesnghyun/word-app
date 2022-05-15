package com.example.word_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final static String DATA_URL   = "https://eeesnghyun.github.io/word-app/data.json";

    WordAdapter wordAdapter;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로딩창 호출
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        wordAdapter = new WordAdapter();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        //가로형 리싸이클러뷰 생성
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
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
            JSONArray dataArray = jsonObject.getJSONArray("dataList");
            JSONArray flowerArray = jsonObject.getJSONArray("flowerList");

            List<WordEntity> list = new ArrayList<WordEntity>();

            for (int i = 0; i < dataArray.length(); i++) {
                WordEntity wordData   = gson.fromJson(dataArray.get(i).toString(), WordEntity.class);
                WordEntity flowerData = gson.fromJson(flowerArray.get(i).toString(), WordEntity.class);

                wordData.setImage(flowerData.getImage());
                wordData.setName(flowerData.getName());
                list.add(wordData);
            }

            Collections.shuffle(list);      //응답받은 데이터를 랜덤으로 배열

            for (WordEntity word : list) {
                wordAdapter.addItem(word);
            }

            wordAdapter.notifyDataSetChanged();

            //println("글 개수 : " + dataArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void println(String data) {
        Log.d("MainActivity", data);
    }

    //화면 캡쳐 버튼 클릭
    public void mOnCaptureClick(View v) {
        View rootView = getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);

        Bitmap screenBitmap = rootView.getDrawingCache();   //캐시를 비트맵으로 변환

        Random random = new Random();
        String filename = "flower_" + String.valueOf(random.nextInt(99999)) +".jpg";

        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures", filename);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();

            rootView.setDrawingCacheEnabled(false);
            File screenShot = file;

            if (screenShot != null) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(screenShot)));
            }

            Toast.makeText(MainActivity.this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "사진이 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}