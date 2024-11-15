package vn.posicode.chuyende.DanhSachMonAn;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class SelectedFood implements Parcelable {
    private String id;           // ID của selected food
    private String foodId;       // ID của món ăn gốc
    private String tableId;      // ID của bàn
    private String foodName;     // Tên món ăn
    private double price;        // Giá của món ăn
    private String imageUrl;     // URL hình ảnh
    private int quantity;        // Số lượng
    private String status;       // Trạng thái (Ví dụ: "Chưa được đặt", "Đã chọn làm", "Đã lấy")

    // Constructor
    public SelectedFood() {
        // Constructor mặc định
    }

    public SelectedFood(String foodId, String tableId, String foodName, double price, String imageUrl) {
        this.foodId = foodId;
        this.tableId = tableId;
        this.foodName = foodName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = 1;
        this.status = " Chưa được đặt";
    }

    // Các getter và setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Phương thức kiểm tra tính hợp lệ
    public boolean isValid() {
        return foodId != null && !foodId.isEmpty() &&
                foodName != null && !foodName.isEmpty() &&
                price >= 0 &&
                quantity > 0;
    }

    // Phương thức cập nhật trạng thái
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    // Phương thức in thông tin
    @Override
    public String toString() {
        return "SelectedFood{" +
                "id='" + id + '\'' +
                ", foodId='" + foodId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }

    // Phương thức so sánh
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SelectedFood that = (SelectedFood) obj;

        return foodId != null ? foodId.equals(that.foodId) : that.foodId == null;
    }

    // Các phương thức Parcelable
    protected SelectedFood(Parcel in) {
        id = in.readString();
        foodId = in.readString();
        tableId = in.readString();
        foodName = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        quantity = in.readInt();
        status = in.readString();
    }

    public static final Creator<SelectedFood> CREATOR = new Creator<SelectedFood>() {
        @Override
        public SelectedFood createFromParcel(Parcel in) {
            return new SelectedFood(in);
        }

        @Override
        public SelectedFood[] newArray(int size) {
            return new SelectedFood[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(foodId);
        dest.writeString(tableId);
        dest.writeString(foodName);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeInt(quantity);
        dest.writeString(status);
    }

    // Phương thức chuyển đổi từ Food sang SelectedFood
    public static SelectedFood fromFood(Food food, String tableId) {
        SelectedFood selectedFood = new SelectedFood(
                food.getId(),
                tableId,
                food.getName(),
                food.getPrice(),
                food.getImage()
        );
        return selectedFood;
    }

    // Phương thức chuyển đổi sang Map để lưu vào Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> selectedFoodMap = new HashMap<>();
        selectedFoodMap.put("ban_id", tableId);
        selectedFoodMap.put("mon_an_id", foodId);
        selectedFoodMap.put("ten_mon_an", foodName);
        selectedFoodMap.put("gia", price);
        selectedFoodMap.put("hinh_anh", imageUrl);
        selectedFoodMap.put("so_luong", quantity);
        selectedFoodMap.put("trang_thai", status);
        return selectedFoodMap;
    }

    // Phương thức tĩnh để tạo SelectedFood từ DocumentSnapshot
    public static SelectedFood fromDocumentSnapshot(DocumentSnapshot document) {
        SelectedFood selectedFood = new SelectedFood();
        selectedFood.setId(document.getId());
        selectedFood.setFoodId(document.getString("mon_an_id"));
        selectedFood.setTableId(document.getString("ban_id"));
        selectedFood.setFoodName(document.getString("ten_mon_an"));
        selectedFood.setPrice(document.contains("gia") ? document.getDouble("gia") : 0.0);
        selectedFood.setImageUrl(document.getString("hinh_anh"));
        selectedFood.setQuantity(document.contains("so_luong") ? document.getLong("so_luong").intValue() : 1);
        selectedFood.setStatus(document.getString("trang_thai"));
        return selectedFood;
    }
}