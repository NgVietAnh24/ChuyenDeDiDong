package vn.posicode.chuyende.adapter;

public class Food {
    private String id;
    private String name;
    private String price;
    private String image;

    // Constructor không tham số (Firebase cần sử dụng)
    public Food() {
        // Constructor mặc định để Firebase sử dụng khi deserializing
    }

    // Constructor rút gọn (không bao gồm id)
    public Food(String name, String price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // Constructor đầy đủ (bao gồm id)
    public Food(String id, String name, String price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // Getter và Setter cho id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho price
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    // Getter và Setter cho image
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}