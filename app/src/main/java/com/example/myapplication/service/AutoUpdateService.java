package com.example.myapplication.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.WeatherActivity;
import com.example.myapplication.json.Weather;
import com.example.myapplication.util.HttpUtil;
import com.example.myapplication.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i("AutoUpdateServive.java","onBind方法。。。。。。");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //定时更新
//    public int onStartCommand(Intent intent , int flags , int startId){
//        Log.i("AutoUpdateServive.java","onStartCommand方法。。。。。。");
//        // 更新天气信息和背景图片
//        updateWeather();
//        updateBingPic();
//
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int anHour = 8 * 60 * 60 * 1000;   // 8小时毫秒数  8 * 60 * 60 * 1000
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        // intent的基本作用：启动活动、启动服务和传递广播，startActivity(intent)会立即执行;
//        Intent i = new Intent(this , AutoUpdateService.class);
//        // 对Intent进行封装，使得Intent不会立即执行，而是需要满足一定条件
//        PendingIntent pi = PendingIntent.getService(this,0,i,PendingIntent.FLAG_IMMUTABLE);
//        // 取消原来的Intent
//        manager.cancel(pi);
//        // 定时执行新的Intent，第一次参数为AlarmManager 的工作类型
//        // ELAPSED_REALTIME_WAKEUP 同样表示让定时任务的触发时间从系统开机开始算起，但会唤醒CPU
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
//        return super.onStartCommand(intent , flags , startId);
//    }

    // 更新（缓存器）天气信息
//    private  void updateWeather(){
//        Log.i("AutoUpdateServive.java","updateWeather方法。。。。。。");
//        // 从缓存器中读取原来的天气信息
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherString = prefs.getString("weather",null);
//        if(weatherString != null){
//            Weather oldWeather = Utility.handleWeatherResponse(weatherString);
//            String oldWeatherId = oldWeather.basic.weatherId;
//            // 向服务器中申请最新的天气数据
//            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + oldWeatherId + "&key=a136695a8ee64c489c96633344d3393c";
//            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    // 申请失败，后台命令行打印异常信息，缓存器不做变化
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    // 申请成功，判断数据状态，并更新缓存器内容
//                    String responseText = response.body().string();
//                    Weather newWeather = Utility.handleWeatherResponse(responseText);
//                    if(newWeather != null && "ok".equals(newWeather.status)){
//                        SharedPreferences.Editor editor = PreferenceManager
//                                .getDefaultSharedPreferences(AutoUpdateService.this).edit();
//                        editor.putString("weather" , responseText);  //注意，这里存的是Sring类型数据
//                        editor.apply();
//                    }
//                }
//            });
//        }
//    }

    // 更新(缓存器)背景图片
    private  void updateBingPic(){
        Log.i("AutoUpdateServive.java","updateBingPic方法。。。。。。");
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String bingPicResponse = response.body().string();
                String bingPic = Utility.handleBingPicResponse(bingPicResponse);
                // 将返回的数据存到缓存器中
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic" , bingPic);
                editor.apply();
            }
        });
    }


}