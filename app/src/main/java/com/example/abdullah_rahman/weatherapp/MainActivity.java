package com.example.abdullah_rahman.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{
    String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid=44db6a862fba0b067b1930da0d769e98";
    LocationManager locationManager;
    String provider;
    TextView current;
    float latt;
    float lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider= locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);
        DownloadTask task = new DownloadTask();
        if(location!=null) {
            onLocationChanged(location);
            task.execute("http://api.openweathermap.org/data/2.5/forecast?lat=" + String.valueOf(latt) + "&lon=" + String.valueOf(lng) + "&units=metric&appid=44db6a862fba0b067b1930da0d769e98");

        }else{
            Log.i("error","failed");
        }
        Log.i("position", String.valueOf(latt) + " " + String.valueOf(lng));


    }


    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider,400,1,this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latt =(float) location.getLatitude();
        lng = (float) location.getLongitude();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection urlConnection;
            String rawData ="";

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!=-1){
                    rawData+=(char)data;
                    data = reader.read();
                }
                Log.i("data",rawData);
                return rawData;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String rawData) {
            super.onPostExecute(rawData);
            String City ="";
            String[] dates;
            ArrayList<String> dateArrayList = new ArrayList<>();
            Date date = new Date();
            ArrayList<Integer> imageId = new ArrayList<>();
            try {
                JSONObject forecast = new JSONObject(rawData);
                JSONObject city = forecast.getJSONObject("city");
                City = city.optString("name");
                Log.i("NAME",City);


                final JSONArray list = forecast.optJSONArray("list");

                for (int i=0;i<list.length();i++){
                    JSONObject hour = list.getJSONObject(i);
                    JSONObject main = hour.getJSONObject("main");
                    String dayTemp = main.optString("temp");
                    JSONArray weather = hour.optJSONArray("weather");
                    JSONObject weatherObj = weather.getJSONObject(0);
                    date.setTime(Long.parseLong(hour.optString("dt"))*1000);
                    Log.i("date", String.valueOf(date));
                    String dateString = String.valueOf(date);

                    String WeatherOption = weatherObj.optString("main");
                    if(WeatherOption.equals("Rain")) {
                        imageId.add(R.drawable.rain);
                    }else{
                        imageId.add(R.drawable.sunny_icon);
                    }


                    dateArrayList.add("Date: "+dateString.substring(0, dateString.length() - 24) + "\n" +"FORECAST: "+ weatherObj.optString("description") + "\n" + dayTemp + " C");
                }
                dates = dateArrayList.toArray(new String[dateArrayList.size()]);


                current = (TextView)findViewById(R.id.currrent);
                String fullDate = dateArrayList.get(0);
                int offset = 39;
                String Display = fullDate;

                current.setText(Display);

                ListView listView = (ListView)findViewById(R.id.listView);
                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                  //      (getApplicationContext(),R.layout.rowlayout,R.id.txt,dates);
                customList arrayAdapter = new customList(MainActivity.this,dates,imageId);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i("clicked", "click");
                        Intent i = new Intent(getApplicationContext(),detailView.class);

                        Bundle extras = new Bundle();
                        extras.putString("data",rawData);
                        extras.putString("pos",String.valueOf(position));
                        i.putExtras(extras);
                        startActivity(i);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
