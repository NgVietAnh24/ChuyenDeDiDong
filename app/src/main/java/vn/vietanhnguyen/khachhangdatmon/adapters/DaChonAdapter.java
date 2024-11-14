package vn.vietanhnguyen.khachhangdatmon.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class DaChonAdapter extends RecyclerView.Adapter<DaChonAdapter.ViewHolder> {
    private List<MonAn> listMonDaChon;
    private OnMonDaChonUpdateListener listener;
    private FirebaseFirestore firestore;
    private String tableId;

    public interface OnMonDaChonUpdateListener {
        void onMonDaChonUpdated(int newCount);
    }

    public DaChonAdapter(List<MonAn> listMonDaChon, OnMonDaChonUpdateListener listener, FirebaseFirestore firestore, String tableId) {
        this.listMonDaChon = listMonDaChon;
        this.listener = listener;
        this.firestore = firestore;
        this.tableId = tableId;
    }

    @NonNull
    @Override
    public DaChonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mon_da_chon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaChonAdapter.ViewHolder holder, int position) {
        MonAn monAn = listMonDaChon.get(position);
        if (monAn.getSoLuong() == 0) {
            monAn.setSoLuong(1);
        }

        holder.tvTenMon.setText(monAn.getName());
        holder.tvGia.setText(String.valueOf(monAn.getPrice()));
        holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));

        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage()) // Lấy URL hình ảnh từ đối tượng MonAn
                .placeholder(R.drawable.image_error)
                .into(holder.imgFood);

        // Xử lý tăng số lượng
        holder.btnTang.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvSoLuong.getText().toString());
            currentQuantity++;  // Tăng số lượng
            holder.tvSoLuong.setText(String.valueOf(currentQuantity));
            monAn.setSoLuong(currentQuantity);  // Cập nhật số lượng trong MonAn
            listMonDaChon.set(position, monAn); // Đảm bảo lưu lại vào danh sách
            Log.d("DaChonAdapter", "Số lượng hiện tại của món " + monAn.getName() + ": " + monAn.getSoLuong());
        });

        // Xử lý giảm số lượng
        holder.btnGiam.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvSoLuong.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;  // Giảm số lượng
                holder.tvSoLuong.setText(String.valueOf(currentQuantity));
                monAn.setSoLuong(currentQuantity);  // Cập nhật số lượng trong MonAn
                listMonDaChon.set(position, monAn); // Đảm bảo lưu lại vào danh sách
                Log.d("DaChonAdapter", "Số lượng hiện tại của món " + monAn.getName() + ": " + monAn.getSoLuong());
            }
        });

        // Xử lý xóa món ăn
        holder.btnXoa.setOnClickListener(v -> {

            // Gọi phương thức xóa món ăn khỏi Firestore
            xoaMonAnTuFirestore(tableId, monAn);

            // Xóa món ăn khỏi danh sách hiển thị
            listMonDaChon.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listMonDaChon.size());

            // Gọi listener để cập nhật số lượng món đã chọn
            if (listener != null) {
                listener.onMonDaChonUpdated(listMonDaChon.size());
            }
        });


    }

    @Override
    public int getItemCount() {
        return listMonDaChon != null ? listMonDaChon.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvGia;
        AppCompatButton btnTang, btnGiam, btnXoa;
        ShapeableImageView imgFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAnDaChon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGia = itemView.findViewById(R.id.tvGiaMonAnDaChon);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

    private void xoaMonAnTuFirestore(String tableId, MonAn monAn) {
        // Tạo một Map để lưu thông tin món ăn cần xóa
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", monAn.getId());
        foodData.put("name", monAn.getName());
        foodData.put("price", monAn.getPrice());
        foodData.put("soLuong", monAn.getSoLuong());
        foodData.put("image", monAn.getImage());

        // Cập nhật danh sách món đã chọn trong Firestore
        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.arrayRemove(foodData))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DaChonAdapter", "Món ăn đã được xóa khỏi Firestore: " + monAn.getName());
                    } else {
                        Log.d("DaChonAdapter", "Lỗi khi xóa món ăn khỏi Firestore: ", task.getException());
                    }
                });
    }
}

