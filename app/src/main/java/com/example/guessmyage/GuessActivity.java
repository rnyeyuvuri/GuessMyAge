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
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        // UI elements
        nameEditText = findViewById(R.id.nameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        guessButton = findViewById(R.id.guessButton);
        saveButton = findViewById(R.id.saveButton);

        // sets click listener for "guess" button
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                if (!name.isEmpty()) {
                    makeAgifyRequest(name);
                } else {
                    Toast.makeText(GuessActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // sets click listener for "save" button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String age = resultTextView.getText().toString().replaceAll("[^0-9]", "");

                if (!name.isEmpty() && !age.isEmpty()) {
                    String message = "If your name was " + name + ", you would be predicted to be " + age + " years old!";
                    resultTextView.setText(message);

                    // Save history
                    saveToHistory(message);
                    Toast.makeText(GuessActivity.this, "Data saved to history", Toast.LENGTH_SHORT).show();
                } else if (age.isEmpty()) {
                    String message = "Woah, " + name + " is so rare that we can't predict an age for you.";
                    resultTextView.setText(message);
                } else {
                    // edit this
                    Toast.makeText(GuessActivity.this, "No data to save", Toast.LENGTH_SHORT).show();
                }
                nameEditText.getText().clear();
            }
        });
    }

    // Using Volley to to implement the API
    private void makeAgifyRequest(String name) {
        String url = "https://api.agify.io?name=" + name;

        // new Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // JSON object request to fetch data from API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int age = response.getInt("age");

                            // Displays results
                            String message = "Hello " + nameEditText.getText().toString() +
                                    ", your age is predicted to be " + age;
                            resultTextView.setText(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Note for me: Toast class in Android allows program to give small messages to user
                            Toast.makeText(GuessActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // add error handling
                        Toast.makeText(GuessActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

        // adding request (to queue so that it will then be taken to execution)
        requestQueue.add(jsonObjectRequest);
    }

    // Save entry in history via SharedPreferences (extra feature)
    // Let's see if this works
    private void saveToHistory(String entry) {
        SharedPreferences preferences = getSharedPreferences("HistoryPreferences", Context.MODE_PRIVATE);
        Set<String> historySet = preferences.getStringSet("historySet", new HashSet<>());
        historySet.add(entry);
        preferences.edit().putStringSet("historySet", historySet).apply();
    }
}