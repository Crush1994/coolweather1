package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 * 省份表
 */

public class Province extends DataSupport {

    private int id;
    private String provinceName;
    private int provinceCode;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }



}
