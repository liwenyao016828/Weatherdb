package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.fonts.Font;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.db.MyCity;
import com.example.myapplication.json.Forecast;
import com.example.myapplication.json.Weather;
import com.example.myapplication.myjson.Basic;
import com.example.myapplication.myjson.DailyForcast;
import com.example.myapplication.myjson.MyWeather;
import com.example.myapplication.service.AutoUpdateService;
import com.example.myapplication.util.HttpUtil;
import com.example.myapplication.util.Utility;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private  TextView titleUpdateTime;
    private  TextView degreeText;
    private  TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;   // 下拉刷新
    private  String mWeatherId;   // 当前天气ID
    public DrawerLayout drawerLayout;   // 滑动菜单
    private Button navButton;   // 城市切换按钮



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        getSupportActionBar().hide();    // 隐藏标题栏

        //初始化控件（获取控件实例，用于数据传递和显示
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        drawerLayout  = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_btn);

        // 设置下拉刷新进度条的颜色
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.swiperRefresh_color));

//        //从本地缓冲中读取天气数据  (SharedPreferences本质以键值对的方式存储数据)
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherString  = prefs.getString("weather",null);
//
//        //当本地缓存非空，则直接解析；当本地缓存为空，则根据获得的天气id，访问服务器查询天气
//        if(weatherString != null){
//            MyWeather weather = Utility.handleWeatherResponse(weatherString);
////            mWeatherId = weather.basic.weatherId;
//            //显示界面
//            showWeatherInfo(weather);
//        }else{
//            mWeatherId = getIntent().getStringExtra("weather_id");
//            //加载完成之前，界面隐藏
//            weatherLayout.setVisibility(View.INVISIBLE);
//            requestWeather(mWeatherId);
//        }

        // 获取当前访问的城市id,然后从服务器中申请天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWeatherId = getIntent().getStringExtra("weather_id");
        //加载完成之前，界面隐藏
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);

        // 设置下拉刷新监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 在本地缓存中获取背景图片数据
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        // 当有缓存时，使用Glide通过链接加载图片;当没有缓存时，则调用loadBingPic()从服务器中加载数据
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else{
            loadBingPic();
        }
    }

    // 从服务器中获取背景图片数据
    private  void  loadBingPic(){
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final  String bingPicResponse = response.body().string();
                String bingPic = Utility.handleBingPicResponse(bingPicResponse);
                // 将返回的数据存到缓存器中
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic" , bingPic);
                editor.apply();
                // 返回主线程，显示获取的背景图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 使用Glide() 方法，通过图片链接加载背景图片数据
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    // 直接加载缓存的天气数据，并显示界面
    private void showWeatherInfo(MyWeather weather){
        String cityname = weather.basic.cityName;
        //只要前面的年月日
        String updateTime = weather.now.updateTime;
        if (updateTime != null) {
            updateTime = updateTime.substring(11,16);
        }
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.info;
        titleCity.setText(cityname);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //预测天气列表
        forecastLayout.removeAllViews();
        for(DailyForcast forecast : weather.dailyForcastList){
            // LayoutInflater作用类似于findViewById，LayoutInflater找layout下的xml布局文件，findViewById找xml布局中的控件
            // LayoutInflater.inflate 用于动态加载界面
            // inflate(int resource , ViewGroup root , boolean attachToRoot)
            // 其中resource表示需要加载的界面 ， root表示需要附加到resource资源文件的根控件， attachToRoot为是否将root附加到布局文件的根视图上
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item , forecastLayout , false);
            TextView dateText = (TextView)view.findViewById(R.id.data_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.info);
            maxText.setText(forecast.tempMax);
            minText.setText(forecast.tempMin);
            // 因为inflate的attachToRoot为false，所以需要手动将动态加载的布局添加到视图中
            forecastLayout.addView(view);
        }

        //空气质量信息   （有才显示）
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.aqi);
            pm25Text.setText(weather.aqi.pm2p5);
        }

        //天气推荐信息
//        String comfort = "舒适度：" + weather.suggestion.comfort.info;
//        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
//        String sport = "运动建议：" + weather.suggestion.sport.info;
//        comfortText.setText(comfort);
//        carWashText.setText(carWash);
//        sportText.setText(sport);

        // 显示布局
        weatherLayout.setVisibility(View.VISIBLE);

        // 激活服务
        // 一旦成功显示天气信息，就启动定义服务，并每8小时自动更新天气信息和背景图片
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);

    }

    //从服务器中检索天气数据
    public void requestWeather(final String cityId){
        // 当前访问的城市名
        String cityName = "";

        // 获取根据当前的cityId,获取城市实例对象
        List<MyCity> countyList = LitePal.where("serverCityId = ?" , String.valueOf(cityId)).find(MyCity.class);
        MyCity city = countyList.get(0);

        //创建JSON对象，用于申请服务器天气数据,同时获取城市名
        String json = null;
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cityId" , cityId);
            if(city != null){
                cityName = city.getCityName();
                jsonObject.put("cityName" , cityName);
            }
            json = jsonObject.toString();
        }catch ( Exception e){
            e.printStackTrace();
        }

        // 存储weather中的basic数据
        final Basic basic = new Basic();
        basic.setWeatherId(cityId);
        basic.setCityName(cityName);

        // 获取天气数据访问地址
        String weatherUrl = Utility.getWeatherBaseUrl();
        // 连接服务器，获取天气数据 ：Now 、DailyForcast 、AQI
        HttpUtil.doPostWeatherJSON(weatherUrl, json,  new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();  //在命令行打印异常信息在程序中出错的位置及原因
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this , "获取天气信息失败1",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final  String responseText = response.body().string();
                // 当服务器返回正常是，获取并处理数据
                if(responseText!=null && response.code()==200){
                    //将返回的JSON数据转换为weather实体类
                    final MyWeather weather = Utility.handleWeatherResponse(responseText);
                    // 单独设置basic部分数据
                    if (weather.getBasic() == null) {
                        weather.setBasic(basic);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 当weather不为空，则存储数据并显示
                            if(weather != null){
                                //返回天气数据正常，则进行数据缓存，避免下次加载时重复请求
//                                SharedPreferences.Editor editor = PreferenceManager
//                                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
//                                editor.putString("weather" , responseText);  //注意，这里存的是Sring类型数据
//                                editor.apply();
                                //显示数据
                                showWeatherInfo(weather);
                            }else{
                                //返回天气数据异常，显示异常提示信息
                                Toast.makeText(WeatherActivity.this , "获取天气信息失败2",Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            }
        });

    }
}