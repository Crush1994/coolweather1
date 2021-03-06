package com.example.coolweather.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.ui.activity.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather",null)!=null){
            Intent lIntent = new Intent(this, WeatherActivity.class);
            startActivity(lIntent);
            finish();
        }
    }
}
