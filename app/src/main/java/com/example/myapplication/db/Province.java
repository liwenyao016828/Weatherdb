package com.example.myapplication.db;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    //Province类中每个字段对应Province表中每一列
    private int id;
    private String provinceName;   //省名
    private int provinceCode;    //省代号
    public Province(){

    }
    //生成相应的getter()和setter()方法
    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
