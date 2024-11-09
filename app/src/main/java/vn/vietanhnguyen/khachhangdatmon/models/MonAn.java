package vn.vietanhnguyen.khachhangdatmon.models;

public class MonAn {
    private String id, name, price, image, category_id;

    public MonAn() {
    }

    public MonAn(String price, String id, String name, String image, String category_id) {
        this.price = price;
        this.id = id;
        this.name = name;
        this.image = image;
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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
