package com.example.myapplication.json;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;     //舒适度
    @SerializedName("cw")
    public CarWash carWash;     //洗车信息
    public  Sport sport;     //运动信息

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public  String info;
    }
    public class  Sport{
        @SerializedName("txt")
        public  String info;
    }
}
