package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
