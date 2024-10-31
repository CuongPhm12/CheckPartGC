package com.example.checkpartgc;

import static com.example.checkpartgc.api.ApiService.gson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkpartgc.model.PartItem;
import com.example.checkpartgc.model.PartNoList_Adapter_Recycler;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PartNoListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PartNoList_Adapter_Recycler adapter;
    private List<PartItem> partNoList = new ArrayList<>();
    Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_part_no_list);

        recyclerView = findViewById(R.id.recyclerViewData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);

        adapter = new PartNoList_Adapter_Recycler(partNoList);
        recyclerView.setAdapter(adapter);

        Intent myIntent = getIntent();
        Bundle myBundle = myIntent.getBundleExtra("myPackage");
        String result = myBundle != null ? myBundle.getString("list") : null;

        if (result != null) {
            Type listType = new TypeToken<ArrayList<PartItem>>() {
            }.getType();
            partNoList = gson.fromJson(result, listType);

            adapter = new PartNoList_Adapter_Recycler(partNoList);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}