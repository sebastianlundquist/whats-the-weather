package com.sebastianlundquist.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityInput;
    TextView weatherText;

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder stringBuilder = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    stringBuilder.append(current);
                    data = reader.read();
                }

                return stringBuilder.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String temperature = Math.round(Double.parseDouble(jsonObject.getJSONObject("main").getString("temp")) - 272.15) + "°C";
                String message = "Temperature: " + temperature + "\r\n";

                JSONArray arr = new JSONArray(weatherInfo);
                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }
                }
                if (!message.equals("")) {
                    weatherText.setText(message);
                }
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't get weather.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityInput.getText().toString(), "UTF-8");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b159d555c4223100daa75f840f787863");
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityInput.getWindowToken(), 0);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't get weather.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        weatherText = findViewById(R.id.weatherText);
    }
}
