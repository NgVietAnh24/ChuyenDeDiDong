package vn.posicode.chuyende.adapter;

public class DauBep_Food {
    private String mon_an_id;
    private String ten_mon_an;
    private int gia;
    private String hinh_anh;
    private int so_luong;
    private String trang_thai;
    private  String time;


    public String getMon_an_id() {
        return mon_an_id;
    }

    public void setMon_an_id(String mon_an_id) {
        this.mon_an_id = mon_an_id;
    }

    public String getTen_mon_an() {
        return ten_mon_an;
    }

    public void setTen_mon_an(String ten_mon_an) {
        this.ten_mon_an = ten_mon_an;
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

    public int getSo_luong() {
        return so_luong;
    }

    public void setSo_luong(int so_luong) {
        this.so_luong = so_luong;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DauBep_Food(String mon_an_id, String ten_mon_an, int gia, String hinh_anh, int so_luong, String trang_thai, String time) {
        this.mon_an_id = mon_an_id;
        this.ten_mon_an = ten_mon_an;
        this.gia = gia;
        this.hinh_anh = hinh_anh;
        this.so_luong = so_luong;
        this.trang_thai = trang_thai;
        this.time = time;
    }

    public DauBep_Food() {
    }
}
