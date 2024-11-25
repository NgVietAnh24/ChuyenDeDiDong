package vn.posicode.chuyende.models;


public class Ban {
    String id;
    String name;
    String description;
    String status;

    public Ban() {
    }

    public Ban(String id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


//class DatTruoc extends Ban {
//    private String tenNguoiDat;
//    private String soDT;
//
//    public DatTruoc(String tenBan, String moTa, String state, int datTruocId, String tenNguoiDat, String soDT) {
//        super(name, moTa, state, datTruocId);
//        this.tenNguoiDat = tenNguoiDat;
//        this.soDT = soDT;
//    }
//
//    public String getTenNguoiDat() {
//        return tenNguoiDat;
//    }
//
//    public void setTenNguoiDat(String tenNguoiDat) {
//        this.tenNguoiDat = tenNguoiDat;
//    }
//
//    public String getSoDT() {
//        return soDT;
//    }
//
//    public void setSoDT(String soDT) {
//        this.soDT = soDT;
//    }
//}
