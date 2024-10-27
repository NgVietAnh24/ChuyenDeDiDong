package vn.posicode.chuyende.models;

public class Ban {
    private String tid;
    private String tenBan;
    private String moTa;
    private int state;
    private int datTruocId;

    public Ban(){}

    public Ban(String tenBan, String moTa, int state, int datTruocId) {
        this.tenBan = tenBan;
        this.moTa = moTa;
        this.state = state;
        this.datTruocId = datTruocId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDatTruocId() {
        return datTruocId;
    }

    public void setDatTruocId(int datTruocId) {
        this.datTruocId = datTruocId;
    }

    public static String getTrangThai(Ban ban){
        switch (ban.state){
            case 0:
                return "Trống";
            case 1:
                return "Đã đặt";
            case 2:
                return "Đang sử dụng";
            default:
                return "Không xác định";
        }
    }
}

class TrangThai extends Ban{
    private String tenTrangThai;

    public TrangThai(String tenBan, String moTa, int state, int datTruocId, String tenTrangThai) {
        super(tenBan, moTa, state, datTruocId);
        this.tenTrangThai = tenTrangThai;
    }

    public String getTenTrangThai() {
        return tenTrangThai;
    }

    public void setTenTrangThai(String tenTrangThai) {
        this.tenTrangThai = tenTrangThai;
    }
}

class DatTruoc extends Ban{
    private String tenNguoiDat;
    private String soDT;

    public DatTruoc(String tenBan, String moTa, int state, int datTruocId, String tenNguoiDat, String soDT) {
        super(tenBan, moTa, state, datTruocId);
        this.tenNguoiDat = tenNguoiDat;
        this.soDT = soDT;
    }

    public String getTenNguoiDat() {
        return tenNguoiDat;
    }

    public void setTenNguoiDat(String tenNguoiDat) {
        this.tenNguoiDat = tenNguoiDat;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }
}
