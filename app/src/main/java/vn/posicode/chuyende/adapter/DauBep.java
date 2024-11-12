package vn.posicode.chuyende.adapter;

public class DauBep {
    private String ten;
    private String soLuong;
    private String thoiGian;

    public DauBep() {
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public DauBep(String ten, String soLuong, String thoiGian) {
        this.ten = ten;
        this.soLuong = soLuong;
        this.thoiGian = thoiGian;
    }
}
