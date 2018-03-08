package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
