package com.example.myapplication.myjson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    public  String cityName;
    public  String weatherId;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
