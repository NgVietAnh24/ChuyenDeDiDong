package vn.posicode.chuyende.adapter;

public class SelectedFood {
    private String ban_id;
    private int gia;
    private String hinh_anh;
    private String mon_an_id;
    private int so_luong;
    private String ten_mon_an;
    private String trang_thai;

    public String getBan_id() {
        return ban_id;
    }

    public void setBan_id(String ban_id) {
        this.ban_id = ban_id;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getHinh_anh() {
        return hinh_anh;
    }

    public void setHinh_anh(String hinh_anh) {
        this.hinh_anh = hinh_anh;
    }

    public String getMon_an_id() {
        return mon_an_id;
    }

    public void setMon_an_id(String mon_an_id) {
        this.mon_an_id = mon_an_id;
    }

    public int getSo_luong() {
        return so_luong;
    }

    public void setSo_luong(int so_luong) {
        this.so_luong = so_luong;
    }

    public String getTen_mon_an() {
        return ten_mon_an;
    }

    public void setTen_mon_an(String ten_mon_an) {
        this.ten_mon_an = ten_mon_an;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public SelectedFood() {
    }

    public SelectedFood(String trang_thai, String ten_mon_an, int so_luong, String mon_an_id, String hinh_anh, int gia, String ban_id) {
        this.trang_thai = trang_thai;
        this.ten_mon_an = ten_mon_an;
        this.so_luong = so_luong;
        this.mon_an_id = mon_an_id;
        this.hinh_anh = hinh_anh;
        this.gia = gia;
        this.ban_id = ban_id;
    }
}
