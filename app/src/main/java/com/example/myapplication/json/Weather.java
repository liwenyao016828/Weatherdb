package com.example.myapplication.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    //创建Weather总的实体类，来引用天气相关的其他实体类
    public Basic basic;
    public AQI aqi;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;    //Forecast仅为单日天气预测实体类，这里通过集合引用多个forecast
    public Now now;
    public Suggestion suggestion;
    public String status;    //返回数据中的字段，用于表示响应是否成功，如成功返回"ok"，否则返回错误信息
}

