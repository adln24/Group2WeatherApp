package com.example.group2weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV, searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE =1;
    private  String cityName;
    private JSONArray response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV =  findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idTVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show();
                {else{
                    Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                }
                }
                    finish();
                }
            }
        }

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals(" ")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "USER CITY NOT FOUND..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/current.json?key=50a429d0956d4126a18151015240201 &q="+cityName+"&days=1&aqi=\n" +
                "yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.listener<JSONObject)
            @Override
            public void onResponse(JSONObject response){
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            weatherRVModalArrayList.clear();

            try {
                String temperature = response.getJSONObject("current").getString("temp_c");
                temperatureTV.setText(temperature+"*c");
                int isDay = response.getJSONObject("current").getInt("is_day");
                String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                Picasso.get().load("http".concat(conditionIcon)).info(iconIV);
                conditionTV.setText(condition);
                if(isDay==1){
                    //morning
                    Picasso.get().load("https://cdn6.f-cdn.com/contestentries/329593/10695369/569a73ba941d5_thumb900.jpg").into(backIV);
                }else {
                    Picasso.get().load("https://wallpapercave.com/wp/wp3404597.jpg").into(backIV);
                }

                JSONObject forecastObj = response.getJSONObject("forecast")

            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }, new Response.ErrorListener(){
        @Override
        public void onErrorResponse(VolleyError error){
            Toast.makeText(MainActivity.this,"Please enter valid city name..", Toast.LENGTH_SHORT).show();
        }
    });
requestQueue.add(jsonObjectRequest);

}