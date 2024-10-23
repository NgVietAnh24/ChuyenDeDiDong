package vn.posicode.chuyende.DanhSachMonAn;

public class Food {
    private String name;
    private String price;
    private int imageResId;
    private String category;

    public Food(String name, String price, int imageResId, String category) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getCategory() {
        return category;
    }
}
