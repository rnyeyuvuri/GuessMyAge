package com.example.guessmyage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class GuessActivity extends AppCompatActivity {

    private EditText nameEditText;
    private TextView resultTextView;
    private Button guessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // sets content view activity_guess.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        // UI elements
        nameEditText = findViewById(R.id.nameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        guessButton = findViewById(R.id.guessButton);

        // sets click listener for "guess" button
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pulls whatever name was entered from edit text.
                String name = nameEditText.getText().toString();
                if (!name.isEmpty()) {
                    // this method was used to fetch data from API
                    makeAgifyRequest(name); // see below for implementation
                } else {
                    Toast.makeText(GuessActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // adding navigation to history page
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuessActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    // Using Volley library to essentially make a network request to the Agify API
    // The response is then parsed and result is displayed in 'resultTextView'
    // Notes: entry is saved to history via saveToHistory method
    private void makeAgifyRequest(String name) {
        String url = "https://api.agify.io?name=" + name;

        // Volley request queue
        // About Volley: Android networking library
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // JSON object request to fetch data from API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                // when/if request is successful
                new Response.Listener<JSONObject>() {
                    // Parses JSON response, gets predicted age, displays message
                    // additional functionalities: display message, save entry to history
                    // clears 'nameEditText'
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int age = response.getInt("age");
                            // Displays results
                            String message = "Hello " + nameEditText.getText().toString() +
                                    ", your age is predicted to be " + age;
                            resultTextView.setText(message);
                            saveToHistory(name, String.valueOf(age));
                            nameEditText.getText().clear();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Note for me: Toast class in Android allows program to give small messages to user
                            Toast.makeText(GuessActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                // if error occurs during request
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GuessActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

        // adding request to queue (so that it will then be taken to execution)
        requestQueue.add(jsonObjectRequest);
    }

    // Save entry in history via SharedPreferences (extra feature)
    private void saveToHistory(String name, String age) {
        SharedPreferences preferences = getSharedPreferences("HistoryPreferences", Context.MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("historySet", new HashSet<>());
        String entry = "Hello " + name + ", your age is predicted to be " + age;
        historySet.add(entry);
        preferences.edit().putStringSet("historySet", historySet).apply();
    }
}