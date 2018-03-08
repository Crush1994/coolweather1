package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
