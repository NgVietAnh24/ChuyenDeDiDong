package vn.vietanhnguyen.khachhangdatmon.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class MonAn implements Parcelable {
    private String id, name, price, image, category_id, category_name;
    private int soLuong;

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
        soLuong = in.readInt();
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
        dest.writeString(id != null ? id : "");
        dest.writeString(name != null ? name : "");
        dest.writeString(price != null ? price : "");
        dest.writeString(image != null ? image : "");
        dest.writeString(category_id != null ? category_id : "");
        dest.writeString(category_name != null ? category_name : "");
        dest.writeInt(soLuong);
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
