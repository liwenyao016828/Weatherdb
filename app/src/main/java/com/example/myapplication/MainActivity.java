package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

//把 AppCompatActivity 改为Activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();    // 隐藏标题栏


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather" , null) != null){
//             //读取缓存，如非空，直接跳转到WeatherActivity显示缓存天气数据
            Intent intent = new Intent(this , WeatherActivity.class);
            startActivity(intent);
            finish();

            //用于测试天气数据的显示情况，每次加载时清除缓存
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.clear();
//            editor.commit();
        }
    }
}