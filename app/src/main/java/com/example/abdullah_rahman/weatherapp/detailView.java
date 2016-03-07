package com.example.abdullah_rahman.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class detailView extends AppCompatActivity {
    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        String rawData = extras.getString("data");
        String position = extras.getString("pos");

        Log.i("pos",position);
        Log.i("data",rawData);

        JSONObject forecast = null;
        try {
            forecast = new JSONObject(rawData);
            JSONArray list = forecast.getJSONArray("list");
            JSONObject hour = list.getJSONObject(Integer.valueOf(position));
            String pressure = "PRESSURE "+hour.optString("pressure");
            String humidity = "HUMIDITY "+hour.optString("humidity");
            JSONObject temperature = hour.getJSONObject("temp");
            String temp = temperature.optString("day")+"*C";
            Log.i("json",temp+pressure+humidity);

            txtView = (TextView) findViewById(R.id.temperature);txtView.setText(temp);
            txtView = (TextView) findViewById(R.id.pressure);txtView.setText(pressure);
            txtView = (TextView) findViewById(R.id.humidity);txtView.setText(humidity);



        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
}
