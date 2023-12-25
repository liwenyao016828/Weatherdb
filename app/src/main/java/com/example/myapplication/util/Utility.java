package com.example.myapplication.util;

import android.text.TextUtils;

import com.example.myapplication.db.City;
import com.example.myapplication.db.County;
import com.example.myapplication.db.MyCity;
import com.example.myapplication.db.Province;
import com.example.myapplication.json.Weather;
import com.example.myapplication.myjson.MyWeather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public  static  boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                int rootId = getRootId();
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if(code == Constant.OK_STATUS){
                    JSONArray allProvinces = jsonObject.getJSONArray("data");   //获取所有的省名数组
                    //遍历所有的省
                    for(int i = 0; i < allProvinces.length(); i++){
                        JSONObject provinceObject = allProvinces.getJSONObject(i);   //获取第i个省对象

                        //修改前
                        //Province province = new Province();   //创建省实例
                        // province.setProvinceName(provinceObject.getString("name"));  //获取省名
                        // province.setProvinceCode(provinceObject.getInt("id"));  //获取省ID
                        // province.save();   //数据库完成数据添加

                        // 修改后
                        MyCity province = new MyCity();   //创建省实例
                        province.setCityName(provinceObject.getString("cityname"));    //获取省名
                        province.setServerCityId(provinceObject.getInt("id"));     //获取省ID
                        province.setpId(rootId);    // 省的pId = 国家的id = 1
                        province.setType(provinceObject.getInt("type"));
                        province.save();   //数据库完成数据添加
                    }
                    return true;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public  static  boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if(code == Constant.OK_STATUS){
                    JSONArray allCities = jsonObject.getJSONArray("data");   //获取所有的市名数组
                    //遍历所有的市
                    for(int i = 0; i < allCities.length(); i++){
                        JSONObject cityObject = allCities.getJSONObject(i);   //获取第i个市对象

                        //修改前
                        // City city = new City();   //创建省实例
                        // city.setCityName(cityObject.getString("name"));  //获取市名
                        // city.setCityCode(cityObject.getInt("id"));  //获取市ID
                        // city.setProvinceCode(provinceId);    //获取市所属省级ID

                        // 修改后
                        MyCity city = new MyCity();
                        city.setCityName(cityObject.getString("cityname"));
                        city.setServerCityId(cityObject.getInt("id"));
                        city.setType(cityObject.getInt("type"));
                        city.setpId(provinceId);
                        city.save();   //数据库完成数据添加
                    }
                    return true;
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public  static  boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if(code == Constant.OK_STATUS){
                    JSONArray allCounties = jsonObject.getJSONArray("data");   //获取所有的县名数组
                    //遍历所有县
                    for(int i = 0; i < allCounties.length(); i++){
                        JSONObject countyObject = allCounties.getJSONObject(i);
                        // 修改前
                        // County county = new County();
                        // county.setCountyName(countyObject.getString("name"));
                        // county.setWeatherId(countyObject.getString("weather_id"));
                        // county.setCityCode(cityId);

                        // 修改后
                        MyCity county = new MyCity();
                        county.setCityName(countyObject.getString("cityname"));
                        county.setServerCityId(countyObject.getInt("id"));
                        county.setType(countyObject.getInt("type"));
                        county.setpId(cityId);
                        county.save();
                    }
                    return true;
                }
            }catch (JSONException e){
                e.printStackTrace();;
            }
        }
        return false;
    }

    // 将返回的JSON数据解析为Weather实体类
    public static MyWeather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObject_data = jsonObject.getJSONObject("data");
//            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");

            //HeWeather数组中只有一个元素
//            String weatherContent = jsonArray.getJSONObject(0).toString();
            String weatherContent = jsonObject_data.toString();
            //返回Weather实例对象
//            return new Gson().fromJson(weatherContent , Weather.class);
            return new Gson().fromJson(weatherContent , MyWeather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  解析返回的json数据并返回图片地址
     */

    public static String handleBingPicResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("images");
            JSONObject jsonObject1=jsonArray.getJSONObject(0);
            String url=jsonObject1.getString("url");
            String bingPic="http://cn.bing.com"+url;
            return bingPic;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // 设置数据表的初始index
    public static int getRootId() {
        // 省：pId = 1 ；市：pId = 省id ；县：pId = 市id ；
        List<MyCity> china = LitePal.where("pid = ?", String.valueOf(0)).find(MyCity.class);
        // 若有国家，则从国家开始， 省的pId = 国家的id = 1
        if (china != null) {
            if ( china.size() == 1) {
                return china.get(0).getId();
            }
        }
        return -1;
    }
    // 访问本地服务器------- 城市数据
    public static String getBaseUrl() {
        return Constant.WEB_SITE + Constant.QUERY_CITY;
    }
    // 访问本地服务器------- 天气数据
    public static String getWeatherBaseUrl() {
        return Constant.WEB_SITE + Constant.QUERY_WEATHER;
    }

}
