package com.example.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class Unility {
    private static String TAG="数据处理类";

    /*
        解析和处理服务器返回的省级数据
         */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province lProvince = new Province();
                    lProvince.setProvinceName(provinceObject.getString("name"));
                    lProvince.setProvinceCode(provinceObject.getInt("id"));
                    lProvince.save();
                    Log.d(TAG,"省级数据库下载成功");
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
   解析和处理服务器返回的市级数据
    */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City lCity = new City();
                    lCity.setCityName(cityObject.getString("name"));
                    lCity.setCityCode(cityObject.getInt("id"));
                    lCity.setProvinceId(provinceId);
                    lCity.save();
                    Log.d(TAG,"市级数据库下载成功");
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
  解析和处理服务器返回的县级数据
   */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County lCounty = new County();
                    lCounty.setCountyName(countyObject.getString("name"));
                    lCounty.setWeatherId(countyObject.getString("weather_id"));
                    lCounty.setCityId(cityId);
                    lCounty.save();

                }
                Log.d(TAG,"县级数据库下载成功"+allCounties.length());
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
    将返回的JSON数据解析成weather实体类
     */
    public static Weather handlerWeatherResponse(String response){
        try {
            JSONObject lJSONObject = new JSONObject(response);
            JSONArray jsonArray = lJSONObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
