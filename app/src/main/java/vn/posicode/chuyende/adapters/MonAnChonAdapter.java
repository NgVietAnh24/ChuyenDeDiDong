package vn.posicode.chuyende.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.MonAn;

public class MonAnChonAdapter extends RecyclerView.Adapter<MonAnChonAdapter.ViewHolder> {
    private List<MonAn> list;
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore firestore;
    private String tableId;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(MonAn monAn);
    }

    public MonAnChonAdapter(List<MonAn> list, OnItemClickListener onItemClickListener, FirebaseFirestore firestore, String tableId, Context context) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.firestore = firestore;
        this.tableId = tableId;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = list.get(position);

        long price = Long.parseLong(monAn.getPrice());
        String formattedPrice = NumberFormat.getInstance(Locale.getDefault()).format(price);

        holder.tvTenMon.setText(monAn.getName());
        holder.tvGiaMon.setText(formattedPrice + " VND");
        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.image_error)
                .error(R.drawable.image_error)
                .into(holder.imgFood);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(monAn);
                // Lưu món ăn vào Firestore ngay lập tức
                luuMonAnDaChon(monAn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mon_an_chon, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgFood;
        TextView tvTenMon, tvGiaMon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAn);
            tvGiaMon = itemView.findViewById(R.id.tvGiaMonAn);
        }
    }

    private void luuMonAnDaChon(MonAn monAn) {
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", monAn.getId());
        foodData.put("ban_id", tableId);
        foodData.put("name", monAn.getName());
        foodData.put("price", monAn.getPrice());
        foodData.put("soLuong", 1);
        foodData.put("time", "");
        foodData.put("status", "");
        foodData.put("image", monAn.getImage());

        // Tạo một tài liệu mới trong bảng selected_foods
        firestore.collection("selectedFoods").document() // Tạo một tài liệu mới với ID tự động
                .set(foodData) // Sử dụng set để lưu dữ liệu
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Đã thêm món: " + monAn.getName(), Toast.LENGTH_SHORT).show(); // Sử dụng context từ Activity
                    } else {
                        Log.d(TAG, "Lỗi khi thêm món ăn: ", task.getException());
                    }
                });
    }

    public void filterList(List<MonAn> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}


