package vn.posicode.chuyende.models;

public class ThongKeModels {
    String tk_id;
   // int tong_tien_nam;
    int tong_tien_thang;
    int nam;
    int thang;
    String hd_id;

    public ThongKeModels() {
    }

    public ThongKeModels(String tk_id, int tong_tien_thang, int nam, int thang, String hd_id) {
        this.tk_id = tk_id;
        this.tong_tien_thang = tong_tien_thang;
        this.nam = nam;
        this.thang = thang;
        this.hd_id = hd_id;
    }

    public String getTk_id() {
        return tk_id;
    }

    public void setTk_id(String tk_id) {
        this.tk_id = tk_id;
    }

    public int getTong_tien_thang() {
        return tong_tien_thang;
    }

    public void setTong_tien_thang(int tong_tien_thang) {
        this.tong_tien_thang = tong_tien_thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public String getHd_id() {
        return hd_id;
    }

    public void setHd_id(String hd_id) {
        this.hd_id = hd_id;
    }
}
