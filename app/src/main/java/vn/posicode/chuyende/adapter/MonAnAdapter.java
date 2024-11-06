package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.MonAnViewHolder> {
    private final Context context;
    private final List<Food> foodList;
    private final List<Food> foodListFull; // Danh sách đầy đủ để lưu lại

//    // Constructor
//    public MonAnAdapter(Context context, List<Food> foodList) {
//        this.context = context;
//        this.foodList = foodList;
//        foodListFull = new ArrayList<>(foodList); // Lưu lại danh sách ban đầu
//    }
// Constructor
public MonAnAdapter(Context context, List<Food> foodList) {
    this.context = context;
    // Đảm bảo foodList và foodListFull không bị null
    this.foodList = (foodList != null) ? foodList : new ArrayList<>();
    this.foodListFull = new ArrayList<>(this.foodList); // Sao chép foodList ban đầu vào foodListFull

}

    @NonNull
    @Override
    public MonAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho item món ăn
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new MonAnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonAnViewHolder holder, int position) {
        // Gán dữ liệu cho từng item
        Food food = foodList.get(position);
        holder.foodNameTextView.setText(food.getName());
        holder.foodPriceTextView.setText(food.getPrice());

        // Tải ảnh từ URL và hiển thị
        Glide.with(context)
                .load(food.getImage()) // URL của ảnh
                .into(holder.foodImageView); // ImageView để hiển thị ảnh
        Log.d("MonAnAdapter", "Initial foodList size: " + this.foodList.size());
        Log.d("MonAnAdapter", "Initial foodListFull size: " + this.foodListFull.size());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void LocDanhMuc(String categoryId, String categoryName) {
        // Kiểm tra nếu foodListFull chưa có dữ liệu thì sao chép từ foodList
        if (foodListFull.isEmpty()) {
            foodListFull.addAll(foodList);
        }

        // Xóa danh sách hiện tại để cập nhật lại
        foodList.clear();
        Log.d("LocDanhMuc", "Category ID được chọn: " + categoryId + ", Category Name: " + categoryName);

        // Nếu categoryName là "Tất cả" hoặc không có ID, hiển thị toàn bộ danh sách
        if (categoryName == null || categoryName.isEmpty() || categoryName.equalsIgnoreCase("Tất cả")) {
            foodList.addAll(foodListFull);
        } else {
            // Lọc theo ID danh mục
            for (Food food : foodListFull) {
                Log.d("LocDanhMuc", "Food Category ID: " + food.getCategory_id());
                if (categoryId.equals(food.getCategory_id())) {
                    foodList.add(food);
                }
            }
        }

        Log.d("LocDanhMuc", "Size of filtered foodList: " + foodList.size());
        notifyDataSetChanged();  // Cập nhật lại giao diện
    }







    // ViewHolder cho món ăn
    public static class MonAnViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView foodPriceTextView;
        ImageView foodImageView; // Khai báo ImageView để hiển thị ảnh

        public MonAnViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo các view con
            foodNameTextView = itemView.findViewById(R.id.textViewName);
            foodPriceTextView = itemView.findViewById(R.id.textViewPrice);
            foodImageView = itemView.findViewById(R.id.imageViewFood); // Khởi tạo ImageView
        }
    }
}
