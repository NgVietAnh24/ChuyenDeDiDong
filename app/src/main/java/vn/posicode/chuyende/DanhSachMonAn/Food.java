package vn.posicode.chuyende.DanhSachMonAn;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
    private String id;
    private String name;
    private String price;
    private String image;
    private String categoryId;
    private String categoryName;
    private String tableId;
    private int soLuong;
    private String status;
    private int soLuongDaLay;
    private long time;

    public Food() {
        this.status = "Chưa làm";
        this.soLuong = 1;
        this.soLuongDaLay = 0;
        this.time = System.currentTimeMillis();
    }

    public Food(String id, String name, String price, String image,
                String categoryId, String categoryName, String tableId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tableId = tableId;
        this.status = "Chưa làm";
        this.soLuong = 1;
        this.soLuongDaLay = 0;
        this.time = System.currentTimeMillis();
    }

    // Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getStatus() {
        return status != null ? status : "Chưa làm";
    }
    public void setStatus(String status) { this.status = status; }

    public int getSoLuongDaLay() { return soLuongDaLay; }
    public void setSoLuongDaLay(int soLuongDaLay) { this.soLuongDaLay = soLuongDaLay; }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    // Phương thức hỗ trợ
    public double getPriceAsDouble() {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Parcelable implementation
    protected Food(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        image = in.readString();
        categoryId = in.readString();
        categoryName = in.readString();
        tableId = in.readString();
        soLuong = in.readInt();
        status = in.readString();
        soLuongDaLay = in.readInt();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(categoryId);
        dest.writeString(categoryName);
        dest.writeString(tableId);
        dest.writeInt(soLuong);
        dest.writeString(status);
        dest.writeInt(soLuongDaLay);
        dest.writeLong(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    // Phương thức hỗ trợ quản lý trạng thái
    public void capNhatTrangThai(String trangThaiMoi) {
        String[] trangThaiHopLe = {
                "Chưa làm",
                "Đang làm",
                "Đã lấy",
                "Hoàn thành"
        };

        for (String tt : trangThaiHopLe) {
            if (tt.equals(trangThaiMoi)) {
                this.status = trangThaiMoi;
                return;
            }
        }

        this.status = "Chưa làm";
    }

    public boolean kiemTraTrangThai(String trangThaiCanKiemTra) {
        return this.status.equals(trangThaiCanKiemTra);
    }

    // Phương thức hỗ trợ tăng số lượng đã lấy
    public void tangSoLuongDaLay(int soLuong) {
        this.soLuongDaLay += soLuong;
        // Đảm bảo không vượt quá số lượng ban đầu
        if (this.soLuongDaLay > this.soLuong) {
            this.soLuongDaLay = this.soLuong;
        }
    }

    // Kiểm tra xem đã lấy đủ số lượng chưa
    public boolean daDayDuSoLuong() {
        return this.soLuongDaLay >= this.soLuong;
    }

    // Phương thức so sánh
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return id.equals(food.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Phương thức toString để dễ debug
    @Override
    public String toString() {
        return "Food{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", soLuong=" + soLuong +
                ", status='" + status + '\'' +
                ", soLuongDaLay=" + soLuongDaLay +
                '}';
    }
}