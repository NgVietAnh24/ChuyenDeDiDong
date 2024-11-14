package vn.posicode.chuyende.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class MonDaChonModel implements Parcelable {
    private String ban_id;
    private String tenMon;
    private String giaMon;
    private String image;
    private String thanhTien;
    private int soLuong;
    private Timestamp thoiGianGuiMon;
   private MonAnModel monAn;

    public MonDaChonModel(int soLuong, MonAnModel monAn) {
        this.soLuong = soLuong;
        this.monAn = monAn;
    }

    // Constructor to create an object with food details
    public MonDaChonModel(String ban_id, String tenMon, String giaMon, String image, String thanhTien, int soLuong, Timestamp thoiGianGuiMon) {
        this.ban_id = ban_id;
        this.tenMon = tenMon;
        this.giaMon = giaMon;
        this.image = image;
        this.thanhTien = thanhTien;
        this.soLuong = soLuong;
        this.thoiGianGuiMon = thoiGianGuiMon;
    }

    // Constructor to create an object from a MonAnModel (when a user selects a dish)
    public MonDaChonModel(MonAnModel monAn, int quantity) {
        this.tenMon = monAn.getName();
        this.giaMon = monAn.getPrice();
        this.image = monAn.getImage();
        this.soLuong = quantity;
        this.thanhTien = String.valueOf(Integer.parseInt(giaMon) * quantity);
        this.thoiGianGuiMon = Timestamp.now();
    }

    // Getters and Setters
    public String getBan_id() {
        return ban_id;
    }

    public void setBan_id(String ban_id) {
        this.ban_id = ban_id;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getGiaMon() {
        return giaMon;
    }

    public void setGiaMon(String giaMon) {
        this.giaMon = giaMon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(String thanhTien) {
        this.thanhTien = thanhTien;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        this.thanhTien = String.valueOf(Integer.parseInt(giaMon) * soLuong);
    }

    public Timestamp getThoiGianGuiMon() {
        return thoiGianGuiMon;
    }

    public void setThoiGianGuiMon(Timestamp thoiGianGuiMon) {
        this.thoiGianGuiMon = thoiGianGuiMon;
    }

    public MonAnModel getMonAn() {
        return monAn;
    }

    public void setMonAn(MonAnModel monAn) {
        this.monAn = monAn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ban_id);
        dest.writeString(tenMon);
        dest.writeString(giaMon);
        dest.writeString(image);
        dest.writeString(thanhTien);
        dest.writeInt(soLuong);
        dest.writeParcelable(thoiGianGuiMon, flags);
    }

    protected MonDaChonModel(Parcel in) {
        ban_id = in.readString();
        tenMon = in.readString();
        giaMon = in.readString();
        image = in.readString();
        thanhTien = in.readString();
        soLuong = in.readInt();
        thoiGianGuiMon = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<MonDaChonModel> CREATOR = new Creator<MonDaChonModel>() {
        @Override
        public MonDaChonModel createFromParcel(Parcel in) {
            return new MonDaChonModel(in);
        }

        @Override
        public MonDaChonModel[] newArray(int size) {
            return new MonDaChonModel[size];
        }
    };


}
