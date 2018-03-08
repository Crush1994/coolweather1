package com.example.coolweather.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.R;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Unility;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class WeatherActivity extends AppCompatActivity {


    private TextView mDateText;

    private TextView mInfoText;

    private TextView mMaxText;

    private TextView mMinText;
    private TextView mTitleCity;

    private TextView mTitleUpdataTime;

    private TextView mDegreeText;

    private TextView mWeatherInfoText;

    private LinearLayout mForecastLayout;

    private TextView mAqiText;

    private TextView mPm25Text;

    private TextView mComfortText;

    private TextView mCarWashText;

    private TextView mSportText;

    private ScrollView mWeatherLayout;
    public static final String TAG="weather";
    private ImageView mBigpicimg;
    private SwipeRefreshLayout mSwipeRefresh;
    private String mWeatherId;
    private String mWeather_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View lDecorView = getWindow().getDecorView();
            lDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mBigpicimg = (ImageView) findViewById(R.id.bing_pic_img);
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdataTime = (TextView) findViewById(R.id.title_updata_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.comfort_text);
        mCarWashText = (TextView) findViewById(R.id.car_wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String lBig_pic = prefs.getString("big_pic", null);
        if (lBig_pic!=null){
            Glide.with(this).load(lBig_pic).into(mBigpicimg);
        }else {
            loadbigpic();
        }
        if (weatherString != null) {
            //有缓存时直接解析天气数据

            Weather lWeather = Unility.handlerWeatherResponse(weatherString);
            mWeatherId = lWeather.basic.weatherId;
            requestWeather(mWeatherId);
            showWeatherInfo(lWeather);
        } else {
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }
    /*
    加载大图
     */
    private void loadbigpic() {
        String requestBigPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBigPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor lEdit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                lEdit.putString("big_pic",bingPic);
                lEdit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBigpicimg);
                    }
                });

            }
        });
    }

    /*
    根据天气id请求城市的天气信息
     */
    private void requestWeather(final String weather_id) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=a9c353408d8244c8a105d51684c7c531";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"接口失败");
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather lWeather = Unility.handlerWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lWeather != null && "ok".equals(lWeather.status)) {
                            SharedPreferences.Editor lEdit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            lEdit.putString("weather", responseText);
                            lEdit.apply();
                            mWeatherId=lWeather.basic.weatherId;
                            showWeatherInfo(lWeather);
                        } else {
                            Log.d(TAG,"成功后失败");
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadbigpic();
    }

    /*
    处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        String lCityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTitleCity.setText(lCityName);
        mTitleUpdataTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
            mDateText=view.findViewById(R.id.date_text);
            mInfoText=view.findViewById(R.id.info_text);
            mMaxText=view.findViewById(R.id.max_text);
            mMinText=view.findViewById(R.id.min_text);
            mDateText.setText(forecast.date);
            mInfoText.setText(forecast.more.info);
            mMaxText.setText(forecast.temperature.max);
            mMinText.setText(forecast.temperature.min);
            mForecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            mAqiText.setText(weather.aqi.city.aqi);
            mPm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;
        mComfortText.setText(comfort);
        mCarWashText.setText(carWash);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}
