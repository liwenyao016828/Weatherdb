package com.example.myapplication.myjson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("obsTime")
    public  String updateTime;
    @SerializedName("temp")
    public  String temperature;
    @SerializedName("text")
    public String info;
}
