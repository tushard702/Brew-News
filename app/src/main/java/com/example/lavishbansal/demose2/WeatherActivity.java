package com.example.lavishbansal.demose2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;

    public void getWeather (View view){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        String  city = editText.getText().toString();
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=df86520792bc37c59b685781d088f188");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.result);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.news:
                        Intent intent1 = new Intent(WeatherActivity.this, newspaper.class);
                        startActivity(intent1);
                        break;

                    case R.id.magzine:
                        Intent intent2 = new Intent(WeatherActivity.this, MagazineActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.user:
                        Intent intent3 = new Intent(WeatherActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.weather:
                        break;
                }
                return false;
            }
        });

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");
                String temprature = jsonObject.getString("main");
                JSONArray array = new JSONArray(weather);
                JSONObject currTemp = new JSONObject(temprature);
                double tempInKevin = Double.parseDouble(currTemp.getString("temp"));
                double tempinCelcius = tempInKevin  - 273.15;

                for (int i=0; i<array.length(); i++){
                    JSONObject jsonpart = array.getJSONObject(i);
                    String result = "";
                    result += jsonpart.getString("main") + "\n" + jsonpart.getString("description");
                    result += "\nTemprature: " + String.format("%.1f",tempinCelcius) + "°C";
                    textView.setText(result);
                }
            }

            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
