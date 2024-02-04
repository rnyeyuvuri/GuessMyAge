package com.example.guessmyage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    private ListView historyListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);

        // retrieve data
        List<String> historyList = retrieveHistoryData();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);
    }

    private List<String> retrieveHistoryData() {
        SharedPreferences preferences = getSharedPreferences("HistoryPreferences", Context.MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("historySet", new HashSet<>());
        return new ArrayList<>(historySet);
    }


}
