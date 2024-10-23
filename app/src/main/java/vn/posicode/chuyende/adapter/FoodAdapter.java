package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.posicode.chuyende.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private OnItemClickListener onItemClickListener; // Thêm interface listener
    private Context context; // Để Glide sử dụng context

    // Constructor
    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context; // Lưu lại context
        this.foodList = foodList;
    }

    // Tạo interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Thêm setter cho listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.foodPrice.setText("$" + food.getPrice());  // Có thể định dạng lại giá trị tiền tệ nếu cần

        String imageUrl = food.getImage(); // Lấy URL hình ảnh từ getImage()
        Log.d("FoodAdapter", "Image URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng Glide để tải ảnh từ URL
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.banhmi)  // Hiển thị ảnh mặc định khi đang tải
                    .error(R.drawable.anhloi)   // Hiển thị ảnh lỗi khi không tải được ảnh
                    .into(holder.foodImage);
        } else {
            // Đặt hình ảnh mặc định nếu không có URL
            holder.foodImage.setImageResource(R.drawable.banhmi);
        }

        // Thêm sự kiện click cho từng item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodPrice;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imageViewFood);
            foodName = itemView.findViewById(R.id.textViewName);
            foodPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}
