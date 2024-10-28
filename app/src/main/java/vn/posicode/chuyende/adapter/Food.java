package vn.posicode.chuyende.adapter;

public class Food {
    private String id;
    private String name;
    private String price;
    private String image;
    private String category_id;

    public Food() {
    }

    public Food(String id, String name, String price, String image, String category_id) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category_id = category_id;
    }
    public Food(String name, String price, String image, String category_id) {
        this.name = name;
        this.price = price;
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