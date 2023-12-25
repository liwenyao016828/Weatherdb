package com.example.myapplication.myjson;

import java.util.List;

public class MyWeather {
    public Basic basic;
    public Now now;
    public List<DailyForcast> dailyForcastList;
    public AQI aqi;
    public Basic getBasic() {
        return basic;
    }
    public void setBasic(Basic basic) {
        this.basic = basic;
    }
    public Now getNow() {
        return now;
    }
    public void setNow(Now now) {
        this.now = now;
    }
    public List<DailyForcast> getDailyForcastList() {
        return dailyForcastList;
    }
    public void setDailyForcastList(List<DailyForcast> dailyForcastList) {
        this.dailyForcastList = dailyForcastList;
    }
    public AQI getAqi() {
        return aqi;
    }
    public void setAqi(AQI aqi) {
        this.aqi = aqi;
    }
}


