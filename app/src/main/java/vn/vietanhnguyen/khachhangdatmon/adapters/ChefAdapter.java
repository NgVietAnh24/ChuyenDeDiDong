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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class ChefAdapter extends RecyclerView.Adapter<ChefAdapter.ViewHolder> {
    private List<MonAn> listMonDaChon;
    private FirebaseFirestore firestore;
    private String tableId;

    public ChefAdapter(List<MonAn> listMonDaChon, FirebaseFirestore firestore, String tableId) {
        this.listMonDaChon = listMonDaChon;
        this.firestore = firestore;
        this.tableId = tableId;
    }

    @NonNull
    @Override
    public ChefAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chef, parent, false);
        return new ChefAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChefAdapter.ViewHolder holder, int position) {
        MonAn monAn = listMonDaChon.get(position);

        holder.tvTenMon.setText(monAn.getName());
        holder.tvTime.setText(monAn.getTime());
        holder.tvStatus.setText(monAn.getStatus());
        holder.tvSoLuong.setText(String.valueOf("SL: " + monAn.getSoLuong()));

        if (monAn.getStatus().equals("Đang làm")) {
            holder.btnDangLam.setEnabled(false);
            holder.btnDangLam.setAlpha(0.5f);
            holder.btnDaXong.setAlpha(1);
            holder.btnDaXong.setEnabled(true);
        } else if (monAn.getStatus().equals("Đã xong")) {
            holder.btnDaXong.setAlpha(0.5f);
            holder.btnDangLam.setAlpha(0.5f);
            holder.btnDaXong.setEnabled(false);
            holder.btnDangLam.setEnabled(false);
        }else {
            holder.btnDangLam.setAlpha(1);
            holder.btnDaXong.setAlpha(0.5f);
            holder.btnDaXong.setEnabled(false);
            holder.btnDangLam.setEnabled(true);
        }

        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.image_error)
                .into(holder.imgFood);

        // Xử lý nút "Đang làm"
        holder.btnDangLam.setOnClickListener(v -> {
            String documentId = monAn.getDocumentId(); // Giả sử bạn đã lưu documentId trong lớp MonAn
            updateFoodStatus(documentId, "Đang làm");
        });

        // Xử lý nút "Đã xong"
        holder.btnDaXong.setOnClickListener(v -> {
            String documentId = monAn.getDocumentId(); // Giả sử bạn đã lưu documentId trong lớp MonAn
            updateFoodStatus(documentId, "Đã xong");
        });
    }

    @Override
    public int getItemCount() {
        return listMonDaChon.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvTime, tvStatus;
        AppCompatButton btnDangLam, btnDaXong;
        ShapeableImageView imgFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAnDaChon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            btnDangLam = itemView.findViewById(R.id.btnDangLam);
            btnDaXong = itemView.findViewById(R.id.btnDaXong);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvTrangThai);
        }
    }

    private void updateFoodStatus(String documentId, String newStatus) {
        if (documentId == null) {
            Log.e("ChefAdapter", "documentId không được phép null");
            return; // Ngừng thực hiện nếu documentId là null
        }

        // Ghi log thông tin để kiểm tra
        Log.d("ChefAdapter", "documentId: " + documentId);

        // Tạo một bản đồ để cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);

        // Cập nhật trạng thái trong tài liệu bằng cách sử dụng ID của tài liệu
        firestore.collection("selectedFoods")
                .document(documentId) // Sử dụng ID của tài liệu để xác định tài liệu
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("ChefAdapter", "Món ăn đã được cập nhật trạng thái '" + newStatus + "'"))
                .addOnFailureListener(e -> Log.e("ChefAdapter", "Lỗi khi cập nhật trạng thái món ăn: ", e));
    }
}