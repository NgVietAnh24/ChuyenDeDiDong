package vn.vietanhnguyen.khachhangdatmon.models;

public class ItemHoaDon {
    private int gia;
    private String hoaDonId;
    private int soLuong;
    private String tenMonAn;

    public ItemHoaDon() {
    }

    public ItemHoaDon(int gia, String hoaDonId, int soLuong, String tenMonAn) {
        this.gia = gia;
        this.hoaDonId = hoaDonId;
        this.soLuong = soLuong;
        this.tenMonAn = tenMonAn;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(String hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenMonAn() {
        return tenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        this.tenMonAn = tenMonAn;
    }
}
