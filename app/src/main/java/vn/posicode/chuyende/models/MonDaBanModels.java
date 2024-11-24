package vn.posicode.chuyende.models;

public class MonDaBanModels {
    private String tenMon;
    private int soLuong;
    private int tongTien;
    private int gia;
    private String ngay;

    public MonDaBanModels() {
    }

    public MonDaBanModels(String tenMon, int soLuong, int gia, int tongTien, String ngay) {
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.tongTien = tongTien;
        this.gia = gia;
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

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }
}
