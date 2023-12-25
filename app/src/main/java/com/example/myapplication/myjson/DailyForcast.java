package com.example.myapplication.myjson;

import com.google.gson.annotations.SerializedName;

public class DailyForcast {
    @SerializedName("fxDate")
    public  String date;

    @SerializedName("textDay")
    public  String info;

    public  String tempMax;

    public  String tempMin;

}
