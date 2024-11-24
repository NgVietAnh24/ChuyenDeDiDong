package vn.posicode.chuyende.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.DauBepFoodActivity;

public class DauBepFoodAdapter extends RecyclerView.Adapter<DauBepFoodAdapter.ViewHolder> {
    private final List<DauBep_Food> selectedFoodList; // Danh sách món ăn
    private final DauBepFoodActivity activity;        // Activity liên quan

    public DauBepFoodAdapter(List<DauBep_Food> selectedFoodList, DauBepFoodActivity activity) {
        this.selectedFoodList = selectedFoodList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_daubep
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daubep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DauBep_Food food = selectedFoodList.get(position);

        // Set dữ liệu cho các TextView
        holder.tvTen.setText(food.getTenMonAn());
        holder.tvSoLuong.setText("Số lượng: " + food.getSoLuong());

        // Định dạng thời gian từ long sang HH:mm
        try {
            long time = Long.parseLong(food.getTime());
            String formattedTime = new SimpleDateFormat("HH:mm").format(new Date(time));
            holder.tvThoiGian.setText("Thời gian: " + formattedTime);
        } catch (NumberFormatException e) {
            holder.tvThoiGian.setText("Thời gian: Không xác định");
        }

        // Load hình ảnh với Glide
        if (food.getHinhAnh() != null && !food.getHinhAnh().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(food.getHinhAnh())
                    .placeholder(R.drawable.anhloi) // Hình mặc định khi tải
                    .into(holder.imageAnh);
        } else {
            holder.imageAnh.setImageResource(R.drawable.anhloi);
        }

        // Xử lý sự kiện nút "Đã xong"
        holder.btnDaxong.setOnClickListener(v -> {
            activity.showQuantityDialog(food.getMonAnId());
        });

        // Xử lý sự kiện nút "Đang làm"
        holder.btnDangLam.setOnClickListener(v -> {
            updateFoodStatus(food.getMonAnId(), "Đang làm");
            Toast.makeText(holder.itemView.getContext(), "Cập nhật trạng thái: Đang làm", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return selectedFoodList.size();
    }

    // Cập nhật trạng thái món ăn trong Firebase
    private void updateFoodStatus(String foodId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cập nhật trạng thái món ăn trong Firebase
        db.collection("foodChefs").document(foodId)
                .update("trang_thai", status) // Cập nhật trường "trang_thai"
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                });
    }

    // ViewHolder cho RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvSoLuong, tvThoiGian;
        Button btnDaxong, btnDangLam;
        ImageView imageAnh;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các view từ layout item_daubep
            tvTen = itemView.findViewById(R.id.tvTen);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            btnDaxong = itemView.findViewById(R.id.btnDaxong);
            btnDangLam = itemView.findViewById(R.id.btnDangLam);
            imageAnh = itemView.findViewById(R.id.imageAnh);
        }
    }
}
