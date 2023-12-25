package com.example.myapplication.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class MyCity extends LitePalSupport {
    @Column(unique = true)
    private int id;    // 客户端数据库表的id
    @Column(index = true)
    private int serverCityId;    // 服务器端数据库表的id
    @Column(index = true)
    private int pId;
    @Column(index = true)
    private String cityName;
    @Column(index = true)
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerCityId() {
        return serverCityId;
    }

    public void setServerCityId(int serverCityId) {
        this.serverCityId = serverCityId;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
