package vn.posicode.chuyende.models;

public class MonDaBan{
    private String tenMon;
    private int soLuong;
    private int tongTien;
    private String ngay;

    public MonDaBan() {
    }

    public MonDaBan(String tenMon, int soLuong, int tongTien, String ngay) {
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
        this.ngay = ngay;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }
}
