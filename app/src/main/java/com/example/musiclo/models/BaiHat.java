package com.example.musiclo.models;

import java.io.Serializable;

public class BaiHat implements Serializable {
    private String id;
    private String tenBaiHat;
    private String caSi;
    private String theLoai;
    private String moTa;
    private String hinhAnh;
    private String linkBaiHat;

    public BaiHat() {}

    public BaiHat(String id, String tenBaiHat, String caSi, String theLoai,
                  String moTa, String hinhAnh, String linkBaiHat) {
        this.id = id;
        this.tenBaiHat = tenBaiHat;
        this.caSi = caSi;
        this.theLoai = theLoai;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
        this.linkBaiHat = linkBaiHat;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenBaiHat() { return tenBaiHat; }
    public void setTenBaiHat(String tenBaiHat) { this.tenBaiHat = tenBaiHat; }

    public String getCaSi() { return caSi; }
    public void setCaSi(String caSi) { this.caSi = caSi; }

    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getLinkBaiHat() { return linkBaiHat; }
    public void setLinkBaiHat(String linkBaiHat) { this.linkBaiHat = linkBaiHat; }
}
