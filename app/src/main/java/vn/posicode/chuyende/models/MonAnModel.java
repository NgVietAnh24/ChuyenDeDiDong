package vn.posicode.chuyende.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MonAnModel implements Parcelable {
    private String id;
    private String name;
    private String price;
    private String image;
    private String category_name;
    private String category_id;

    public MonAnModel() {
    }

    public MonAnModel(String id, String name, String price, String image, String category_name, String category_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category_name = category_name;
        this.category_id = category_id;
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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    // Implement Parcelable methods
    @Override
    public int describeContents() {
        return 0; // Chúng ta không có đối tượng đặc biệt để mô tả
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(category_name);
        dest.writeString(category_id);
    }

    // Constructor used for Parcelable
    protected MonAnModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        image = in.readString();
        category_name = in.readString();
        category_id = in.readString();
    }

    // Creator to help in creating instances from Parcel
    public static final Creator<MonAnModel> CREATOR = new Creator<MonAnModel>() {
        @Override
        public MonAnModel createFromParcel(Parcel in) {
            return new MonAnModel(in);
        }

        @Override
        public MonAnModel[] newArray(int size) {
            return new MonAnModel[size];
        }
    };
}
