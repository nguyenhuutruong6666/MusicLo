package com.example.musiclo.models;

public class YeuThich {
    private int id;
    private int idNguoiDung;
    private int idBaiHat;

    public YeuThich() {}

    public YeuThich(int id, int idNguoiDung, int idBaiHat) {
        this.id = id;
        this.idNguoiDung = idNguoiDung;
        this.idBaiHat = idBaiHat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdNguoiDung() { return idNguoiDung; }
    public void setIdNguoiDung(int idNguoiDung) { this.idNguoiDung = idNguoiDung; }

    public int getIdBaiHat() { return idBaiHat; }
    public void setIdBaiHat(int idBaiHat) { this.idBaiHat = idBaiHat; }
}
