package com.example.guessmyage;

// Import necessary packages
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// Main activity class for the guessing game
public class GuessActivity extends AppCompatActivity {

    // UI elements
    private EditText nameEditText;
    private TextView resultTextView;
    private Button guessButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        // Initialize UI elements
        nameEditText = findViewById(R.id.nameEditText);
        resultTextView = findViewById(R.id.resultTextView);
        guessButton = findViewById(R.id.guessButton);
        saveButton = findViewById(R.id.saveButton);

        // Listens to 'click' for the guess button
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered name from the EditText
                String name = nameEditText.getText().toString();
                if (!name.isEmpty()) {
                    // Execute an AsyncTask to fetch data from the Agify API
                    new AgifyTask().execute(name);
                } else {
                    Toast.makeText(GuessActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sets up click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered name and age result from the UI
                String name = nameEditText.getText().toString();
                String age = resultTextView.getText().toString().replaceAll("[^0-9]", "");

                if (!name.isEmpty() && !age.isEmpty()) {
                    // Save name/age matching for offline viewing (Note for me: Implement this in future)
                    Toast.makeText(GuessActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GuessActivity.this, "No data to save", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // AsyncTask fetches data from Agify API in the background
    private class AgifyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... names) {
            String name = names[0];
            try {
                // Build the URL for the Agify API using the entered name
                URL url = new URL("https://api.agify.io?name=" + name);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read the API response
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            super.onPostExecute(jsonResult);
            if (jsonResult != null) {
                try {
                    // Parse the JSON response from the Agify API
                    JSONObject jsonObject = new JSONObject(jsonResult);
                    int age = jsonObject.getInt("age");
                    // Display the result in the UI
                    String message = "Hello " + nameEditText.getText().toString() +
                            ", your age is predicted to be " + age;
                    resultTextView.setText(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Display a toast if fetching data fails
                Toast.makeText(GuessActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Example of sharing data to other apps using the Android share sheet
    private void shareData(String data) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}