package com.example.musiclo.models;

public class YeuThich {
    private int id;
    private int idNguoiDung;
    private String idBaiHat;

    public YeuThich() {}

    public YeuThich(int id, int idNguoiDung, String idBaiHat) {
        this.id = id;
        this.idNguoiDung = idNguoiDung;
        this.idBaiHat = idBaiHat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(int idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    public String getIdBaiHat() { return idBaiHat; }
    public void setIdBaiHat(String idBaiHat) { this.idBaiHat = idBaiHat; }
}
