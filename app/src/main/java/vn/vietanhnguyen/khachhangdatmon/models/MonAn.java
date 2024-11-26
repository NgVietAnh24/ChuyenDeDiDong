package vn.vietanhnguyen.khachhangdatmon.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class MonAn implements Parcelable {
    private String id, name, price, image, category_id, category_name, status, time, ban_id, documentId, statusHuy, ghiChu;
    private int soLuong, soLuongDaLay;

    public MonAn() {
    }

    public MonAn(String id, String name, String price, String image, String category_id, String category_name) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category_id = category_id;
        this.category_name = category_name;
    }

    protected MonAn(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        image = in.readString();
        category_id = in.readString();
        category_name = in.readString();
        status = in.readString();
        soLuong = in.readInt();
        time = in.readString();
        ban_id = in.readString();
        documentId = in.readString();
        statusHuy = in.readString();
        ghiChu = in.readString();
        soLuongDaLay = in.readInt();
    }




    public static final Creator<MonAn> CREATOR = new Creator<MonAn>() {
        @Override
        public MonAn createFromParcel(Parcel in) {
            return new MonAn(in);
        }

        @Override
        public MonAn[] newArray(int size) {
            return new MonAn[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(category_id);
        dest.writeString(category_name);
        dest.writeString(status);
        dest.writeInt(soLuong);
        dest.writeString(time);
        dest.writeString(ban_id);
        dest.writeString(documentId);
        dest.writeString(statusHuy);
        dest.writeString(ghiChu);
        dest.writeInt(soLuongDaLay);
    }

    // Ghi đè equals và hashCode để so sánh các đối tượng MonAn
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MonAn monAn = (MonAn) obj;
        return Objects.equals(id, monAn.id); // So sánh theo id hoặc các thuộc tính phù hợp khác
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Tính toán hashCode theo id
    }

    // Các phương thức getter và setter


    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getStatusHuy() {
        return statusHuy;
    }

    public void setStatusHuy(String statusHuy) {
        this.statusHuy = statusHuy;
    }

    public int getSoLuongDaLay() {
        return soLuongDaLay;
    }

    public void setSoLuongDaLay(int soLuongDaLay) {
        this.soLuongDaLay = soLuongDaLay;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getBan_id() {
        return ban_id;
    }

    public void setBan_id(String ban_id) {
        this.ban_id = ban_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

}
