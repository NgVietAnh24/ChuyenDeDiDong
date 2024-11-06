package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.CategoryModel;

public class CategoryButtonAdapter extends RecyclerView.Adapter<CategoryButtonAdapter.ButtonViewHolder> {

    private List<CategoryModel> buttonNames;
    private Context context;
    private OnCategoryClickListener onCategoryClickListener;
    private MonAnAdapter foodAdapter; // Biến để giữ tham chiếu đến MonAnAdapter

    public CategoryButtonAdapter(Context context, List<CategoryModel> buttonNames, MonAnAdapter foodAdapter) {
        this.context = context;
        this.buttonNames = buttonNames;
        this.foodAdapter = foodAdapter; // Nhận MonAnAdapter thông qua constructor
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.button_category, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        CategoryModel category = buttonNames.get(position);
        String buttonText = category.getName(); // Lấy tên danh mục
        holder.button.setText(buttonText);

        // Xử lý sự kiện click cho từng button
        holder.button.setOnClickListener(v -> {
            //Log.d("ClickButton",category.getId());
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category.getId(), category.getName()); // Gọi phương thức listener
                // Gọi LocDanhMuc từ MonAnAdapter
                foodAdapter.LocDanhMuc(category.getId(), category.getName()); // Lọc món ăn theo danh mục đã chọn
            }
        });
    }

    @Override
    public int getItemCount() {
        return buttonNames.size();
    }

    // Phương thức để thiết lập listener
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    // Giao diện cho sự kiện click
    public interface OnCategoryClickListener {
        void onCategoryClick(String categoryId, String categoryName);
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.itemButton);
        }
    }
}
