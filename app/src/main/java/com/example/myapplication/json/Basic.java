package com.example.myapplication.json;

import com.google.gson.annotations.SerializedName;

public class Basic {
    //使用@SerializedName("city")注解方法，让JSON字段和Java字段之间建立映射关系
    @SerializedName("city")
    public  String cityName;
    @SerializedName("id")
    public  String weatherId;
    public  Update update;

    public class Update{
        @SerializedName("loc")
        public  String updateTime;
    }
}
