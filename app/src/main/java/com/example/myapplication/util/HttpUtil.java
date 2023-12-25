package com.example.myapplication.util;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // 链接服务器，获取城市数据
    public  static  void  doPostCityFrom(String address, final String type, int pId, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("requestType",type)
                .add("pId",String.valueOf(pId))
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // 连接服务器，获取天气数据
    public  static  void  doPostWeatherJSON(String address, String json, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json , JSON);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void sendOkHttpRequest(String address, Callback callback){

        OkHttpClient client = new OkHttpClient();    //创建OkHttpClient实例
        Request request = new Request.Builder().url(address).build();     //创建请求对象，向address发送网络请求

        //enqueue方法为异步方法，不会卡顿；execute方法为同步方法
        client.newCall(request).enqueue(callback);      //创建newCall实例，发送网络请求，并获取服务器返回的数据
    }

//    public  static  void  doPostJson(String address, final String type, int pId,Callback callback){
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("requestType",type)
//                .add("pId",String.valueOf(pId))
//                .build();
//        Request request = new Request.Builder()
//                .url(address)
//                .post(requestBody)
//                .build();
//        client.newCall(request).enqueue(callback);
//    }
}
