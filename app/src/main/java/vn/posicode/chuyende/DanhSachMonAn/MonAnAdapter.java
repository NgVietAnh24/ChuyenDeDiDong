package vn.posicode.chuyende.DanhSachMonAn;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;


public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.ViewHolder> {
    private List<Food> list;
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore firestore;
    private String tableId;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Food monAn);
    }

    public MonAnAdapter(List<Food> list, OnItemClickListener onItemClickListener, FirebaseFirestore firestore, String tableId, Context context) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.firestore = firestore;
        this.tableId = tableId;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food monAn = list.get(position);
        holder.tvTenMon.setText(monAn.getName());
        holder.tvGiaMon.setText(monAn.getPrice() + " VND");
        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mon_an, parent, false);
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

    private void luuMonAnDaChon(Food monAn) {
        if (tableId == null) {
            Log.e(TAG, "Table ID is null. Cannot save selected food.");
            Toast.makeText(context, "Lỗi: ID bàn không hợp lệ.", Toast.LENGTH_SHORT).show();
            return; // Trả về nếu tableId là null
        }

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", monAn.getId());
        foodData.put("name", monAn.getName());
        foodData.put("price", monAn.getPrice());
        foodData.put("soLuong", 1);
        foodData.put("image", monAn.getImage());

        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.arrayUnion(foodData)) // Sử dụng arrayUnion để thêm món ăn vào mảng
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Đã thêm món: " + monAn.getName(), Toast.LENGTH_SHORT).show(); // Sử dụng context từ Activity
                    } else {
                        Log.d(TAG, "Lỗi khi thêm món ăn: ", task.getException());
                    }
                });
    }

    public void filterList(List<Food> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}