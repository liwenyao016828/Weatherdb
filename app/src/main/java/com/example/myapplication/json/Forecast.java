package com.example.myapplication.json;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    //返回数据为数组，这里值定义单日天气实体类，然后在声明实体类引用时使用集合类型
    public String date;
    @SerializedName("cond")
    public More more;
    @SerializedName("tmp")
    public Temperature temperature;

    public class  More{
        @SerializedName("txt_d")
        public String info;
    }
    public  class  Temperature{
        public String max;
        public String min;
    }
}
