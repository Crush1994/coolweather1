package com.example.coolweather.gson;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
