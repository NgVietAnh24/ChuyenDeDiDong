package vn.vietanhnguyen.khachhangdatmon.models;

public class HoaDon {
    private String banId;
    private String ghiChu;
    private String gioTao;
    private String hoaDonId;
    private String ngayTao;
    private String nvId;
    private String soDt;
    private String tenKhachHang;
    private String tinhTrang;
    private int tongTien;

    public HoaDon() {
    }

    public HoaDon(String banId, String ghiChu, String gioTao, String hoaDonId, String ngayTao, String nvId, String soDt, String tenKhachHang, String tinhTrang, int tongTien) {
        this.banId = banId;
        this.ghiChu = ghiChu;
        this.gioTao = gioTao;
        this.hoaDonId = hoaDonId;
        this.ngayTao = ngayTao;
        this.nvId = nvId;
        this.soDt = soDt;
        this.tenKhachHang = tenKhachHang;
        this.tinhTrang = tinhTrang;
        this.tongTien = tongTien;
    }

    public String getBanId() {
        return banId;
    }

    public void setBanId(String banId) {
        this.banId = banId;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getGioTao() {
        return gioTao;
    }

    public void setGioTao(String gioTao) {
        this.gioTao = gioTao;
    }

    public String getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(String hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getNvId() {
        return nvId;
    }

    public void setNvId(String nvId) {
        this.nvId = nvId;
    }

    public String getSoDt() {
        return soDt;
    }

    public void setSoDt(String soDt) {
        this.soDt = soDt;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }
}
