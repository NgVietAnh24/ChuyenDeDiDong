package adapter;

public class Food {
    private String name;
    private String price;
    private int imageResId; // Resource ID cho ảnh trong drawable
    private String imageUrl; // Đường dẫn URL hoặc file path cho ảnh

    // Constructor sử dụng imageResId (tương tự như bạn đã làm)
    public Food(String name, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.imageUrl = null; // Khi sử dụng resource ID thì không cần URL
    }

    // Constructor sử dụng imageUrl
    public Food(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageResId = 0; // Khi sử dụng URL thì không cần resource ID
    }

    // Getter và Setter cho tên và giá
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

    // Getter và Setter cho resource ID
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    // Getter và Setter cho imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Hàm kiểm tra xem sử dụng ảnh từ resource hay URL
    public boolean hasImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
