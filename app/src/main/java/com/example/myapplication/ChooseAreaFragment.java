package com.example.myapplication;

//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.myapplication.db.City;
import com.example.myapplication.db.County;
import com.example.myapplication.db.MyCity;
import com.example.myapplication.db.Province;
import com.example.myapplication.util.HttpUtil;
import com.example.myapplication.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Address;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class ChooseAreaFragment extends Fragment {
    public static  final  int LEVEL_PROVINCE = 0;
    public static  final  int LEVEL_CITY = 1;
    public static  final  int LEVEL_COUNTY = 2;

    // view中的控件
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog; //进度对话框

    // 当前状态相关
    private List<String> datalist = new ArrayList<>();   //用于存放当前需要显示的地名
    private int currentLevel;    //当前显示的数据等级

//    // 修改前的全局变量的声明语句
//    private Province selectedProvince;
//    private City selectedCity;
//    private List<Province> provinceList;     //暂存当前加载的省级数据
//    private List<City> cityList;  //暂存当前加载的市级数据
//    private List<County> countyList;  //暂存当前加载的县级数据

    // 修改后的全局变量的声明语句
    private MyCity selectedProvince;
    private MyCity selectedCity;
    private List<MyCity> provinceList;     //暂存当前加载的省级数据
    private List<MyCity> cityList;  //暂存当前加载的市级数据
    private List<MyCity> countyList;  //暂存当前加载的县级数据



    private boolean isFirstGo = true;



    /*
    在OnCreateView()方法中，首先获取一些控件的实例，然后初始化ArrayAdapter，并将它设置为ListView的适配器
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 动态加载layout文件
        View view = inflater.inflate(R.layout.choose_area,container,false);
        // 获取相应控件
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //利用适配器完成数据到listview的传递
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        requireActivity().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if(event.getTargetState() == Lifecycle.State.CREATED){

                    // 分别给back_button和listview添加点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //如果当前为province列表，则点击列表时获取点击的省，并搜索下面的市级信息
                            if(currentLevel == LEVEL_PROVINCE){
                                selectedProvince = provinceList.get(i);
                                queryCity();
                            }
                            //如果当前为city列表，则点击列表时获取点击的市，并搜索下面的县级信息
                            else if(currentLevel == LEVEL_CITY){
                                selectedCity = cityList.get(i);
                                queryCounty();
                            }
                            //如果当前为county列表，则点击列表时，启动WeatherActivity，并传递weatherID用于加载数据
                            else if(currentLevel == LEVEL_COUNTY){
//                                String weatherId = countyList.get(i).getWeatherId();
                                int cityId = countyList.get(i).getServerCityId();
                                // 如果当前在MainActivity中，则点击县时切换到WeatherActivity，显示天气信息
                                if(getActivity() instanceof  MainActivity){
                                    //通过Intent实现界面之间的数据传递
                                    Intent intent = new Intent(getActivity() , WeatherActivity.class);
                                    //传递weatherId
//                                    intent.putExtra("weather_id" , weatherId);
                                    intent.putExtra("weather_id" , String.valueOf(cityId));
                                    //启动WeatherActivity
                                    startActivity(intent);
                                    //关闭当前的活动
                                    getActivity().finish();
                                }
                                // 如果当前在WeatherActivity中，则关闭滑动菜单，显示天气刷新进度条，并从服务器中获取天气数据
                                else if(getActivity() instanceof WeatherActivity){
                                    WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                                    weatherActivity.drawerLayout.closeDrawers();
                                    weatherActivity.swipeRefresh.setRefreshing(true);
//                                    weatherActivity.requestWeather(weatherId);
                                    weatherActivity.requestWeather(String.valueOf(cityId));
                                }
                            }
                        }
                    });
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //如果当前是市级信息，则点击返回时，显示省级信息
                            if(currentLevel == LEVEL_CITY){
                                queryProvinces();
                            }
                            //如果当前是县级信息，则点击返回时，显示市级信息
                            else if(currentLevel == LEVEL_COUNTY){
                                queryCity();
                            }
                        }
                    });

                    //默认初始界面加载省级数据
                    queryProvinces();

                    requireActivity().getLifecycle().removeObserver(this);
                }
            }
        });
    }

        /*
    新版Android已经弃用onActivityCreated方法，未来可能被移除
     */
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        // 分别给back_button和listview添加点击事件
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //如果当前为province列表，则点击列表时获取点击的省，并搜索下面的市级信息
//                if(currentLevel == LEVEL_PROVINCE){
//                    selectedProvince = provinceList.get(i);
//                    queryCity();
//                }
//                //如果当前为city列表，则点击列表时获取点击的市，并搜索下面的县级信息
//                else if(currentLevel == LEVEL_CITY){
//                    selectedCity = cityList.get(i);
//                    queryCounty();
//                }
//            }
//        });
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //如果当前是市级信息，则点击返回时，显示省级信息
//                if(currentLevel == LEVEL_CITY){
//                    queryCounty();
//                }
//                //如果当前是县级信息，则点击返回时，显示市级信息
//                else if(currentLevel == LEVEL_COUNTY){
//                    queryCity();
//                }
//            }
//        });
//
//        //默认初始界面加载省级数据
//        queryProvinces();
//    }


    /*
    从服务器中加载目标种类的数据
    为什么有两个参数？  --- 因为address只包含了上一级的id，type用于判断是否是目标数据
     */
//    private void queryFromService(String address , final String type){
//        showProgressDialog();
//        //调用HttpUtil.sendOkHttpRequest()方法，从服务器中加载数据
//        HttpUtil.sendOkHttpRequest(address, new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                //服务器加载数据失败时，回到主线程处理需要执行的操作
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        clossProgressDialog();
//                        Toast.makeText(getContext() , "加载失败" , Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String responseText = response.body().string();
//                //判断是否是目标数据，如果是，则Utility,java中相应的方法进行数据解析并存储到数据库中
//                boolean result = false;
//                if("province".equals(type)){
//                    result = Utility.handleProvinceResponse(responseText);
//                }else if("city".equals(type)){
//                    // 这里需要传入provinceId，因此需要定义一个变量selectedProvince表示当前选中的province
//                    result = Utility.handleCityResponse(responseText,selectedProvince.getProvinceCode());
//                }
//                else if("county".equals(type)){
//                    result = Utility.handleCountyResponse(responseText,selectedCity.getCityCode());
//                }
//
//                //如果数据完成解析，并存放在了数据库中，则需要重新在页面加载数据
//                if(result){
//                    //由于要牵扯到UI操作，因此必须在主线程中调用界面方法，runOnUiThread()方法实现从子线程切换到主线程
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            clossProgressDialog(); // 关闭进度对话框
//                            if("province".equals(type)){
//                                queryProvinces();   //显示省列表
//                            }
//                            else if("city".equals(type)){
//                                queryCity();
//                            }
//                            else if("county".equals(type)){
//                                queryCounty();
//                            }
//                        }
//                    });
//
//                }
//            }
//        });
//    }

    private  void  queryFromMyService(String address , final String type , int pId){
        showProgressDialog();
        HttpUtil.doPostCityFrom(address, type, pId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //服务器加载数据失败时，回到主线程处理需要执行的操作
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clossProgressDialog();
                        Toast.makeText(getContext() , "加载失败" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                //判断是否是目标数据，如果是，则Utility,java中相应的方法进行数据解析并存储到数据库中
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    // 这里需要传入provinceId，因此需要定义一个变量selectedProvince表示当前选中的province
                    result = Utility.handleCityResponse(responseText,selectedProvince.getServerCityId());
                }
                else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getServerCityId());
                }
                //如果数据完成解析，并存放在了数据库中，则需要重新在页面加载数据
                if(result){
                    //由于要牵扯到UI操作，因此必须在主线程中调用界面方法，runOnUiThread()方法实现从子线程切换到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clossProgressDialog(); // 关闭进度对话框
                            if("province".equals(type)){
                                queryProvinces();   //显示省列表
                            }
                            else if("city".equals(type)){
                                queryCity();
                            }
                            else if("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });

                }
            }
        });
    }

    /*
    1、查询所有的省，优先从数据库中查找，如果没有，再到服务器中查找
    2、查找到之后完成相应的界面显示
     */
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);   //显示省列表是，backButton不显示且不占用布局空间

        //在数据库中查询Province表中所有数据
//        provinceList = LitePal.findAll(Province.class);
        // 在数据库中查询MyCity表中所有省级数据，省级数据的type = 1
        provinceList = LitePal.where("type = 1").find(MyCity.class);


        //如果数据库中有数据，则直接获取所有的省；如果数据库中没有数据，则从服务器中加载
        if(provinceList.size()>0){
            datalist.clear();
//            for(Province province : provinceList){    //使用for-each循环遍历list元素，此时元素不可修改
//                datalist.add(province.getProvinceName());
//            }
            for(MyCity province : provinceList){    //使用for-each循环遍历list元素，此时元素不可修改
                datalist.add(province.getCityName());
            }
            //页面显示
            adapter.notifyDataSetChanged();   //无需刷新activity，通知适配器更新列表的数据
            listView.setSelection(0);     //默认选中第一个列表元素
            currentLevel = LEVEL_PROVINCE;   //更改当前访问状态

        }else{

            //服务器中加载完成后，需要重新执行queryProvince(),实现省名的显示，因此需要修改queryFromService中的内容
//            String address = "http://guolin.tech/api/china";    // 郭林老师的服务器
//            queryFromService(address , "province");

            // 获取本地服务器省级数据
            String baseUrl = Utility.getBaseUrl();
            int pId;
            if(selectedProvince != null){
                pId = selectedProvince.getpId();
            }
            else
                pId = 1;
            queryFromMyService(baseUrl , "province" , pId);
        }
    }
    /*
    1、查询所有的市，优先从数据库中查找，如果没有，再到服务器中查找
    2、查找到之后完成相应的界面显示
     */
    private void queryCity(){
        titleText.setText(selectedProvince.getCityName());
        backButton.setVisibility(View.VISIBLE);   // 当显示市级列表时，backButton正常显示

        // 在数据库中查询City表中属于当前省级地名下的所有市里数据
        // cityList = LitePal.where("provinceCode = ? ", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        // 通过pId查询某省级单位下的市级数据
        cityList = LitePal.where("pid = ? ", String.valueOf(selectedProvince.getServerCityId())).find(MyCity.class);

        //如果数据库中有数据，则直接获取所有的市；如果数据库中没有数据，则从服务器中加载
        if(cityList.size()>0){
            datalist.clear();
//            for (City city : cityList){
//                datalist.add(city.getCityName());
//            }
            for (MyCity city : cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
//            int provinceCode = selectedProvince.getProvinceCode();
//            String address = "http://guolin.tech/api/china/" + provinceCode;
//            queryFromService(address , "city");

            int pId = selectedProvince.getServerCityId();
            String url = Utility.getBaseUrl();
            queryFromMyService(url , "city" , pId);

        }
    }
    /*
    1、查询所有的县，优先从数据库中查找，如果没有，再到服务器中查找
    2、查找到之后完成相应的界面显示
     */
    private  void queryCounty(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

//        countyList = LitePal.where("cityCode = ?",String.valueOf(selectedCity.getCityCode())).find(County.class);
        countyList = LitePal.where("pid = ?",String.valueOf(selectedCity.getServerCityId())).find(MyCity.class);

        //如果数据库中有数据，则直接获取所有的市；如果数据库中没有数据，则从服务器中加载
        if(countyList.size()>0){
            datalist.clear();
//            for (County county : countyList){
//                datalist.add(county.getCountyName());
//            }
            for (MyCity county : countyList){
                datalist.add(county.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
//            int provinceCode = selectedProvince.getProvinceCode();
//            int cityCode = selectedCity.getCityCode();
//            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
//            queryFromService(address , "county");
            String url = Utility.getBaseUrl();
            int pid = selectedCity.getServerCityId();
            queryFromMyService(url , "county" , pid);
        }
    }

    /*显示加载进度对话框*/
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);   //点击外部位置，无法取消对话框
        }
        progressDialog.show();
    }

    /* 关闭加载进度对话框*/
    private  void  clossProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
