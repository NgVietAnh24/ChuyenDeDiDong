package vn.posicode.chuyende.DanhSachMonAn;

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

import vn.posicode.chuyende.R;

public class DaChonAdapter extends RecyclerView.Adapter<DaChonAdapter.ViewHolder> {
    private List<Food> listMonDaChon;
    private OnMonDaChonUpdateListener listener;
    private FirebaseFirestore firestore;
    private String tableId;

    // Interface để cập nhật số lượng món đã chọn
    public interface OnMonDaChonUpdateListener {
        void onMonDaChonUpdated(int newCount);
    }

    // Constructor
    public DaChonAdapter(List<Food> listMonDaChon,
                         OnMonDaChonUpdateListener listener,
                         FirebaseFirestore firestore,
                         String tableId) {
        this.listMonDaChon = listMonDaChon;
        this.listener = listener;
        this.firestore = firestore;
        this.tableId = tableId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mon_da_chon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food monAn = listMonDaChon.get(position);

        // Đảm bảo số lượng luôn ít nhất là 1
        monAn.setSoLuong(Math.max(monAn.getSoLuong(), 1));

        // Thiết lập thông tin món ăn
        holder.tvTenMon.setText(monAn.getName());
        holder.tvGia.setText(String.valueOf(monAn.getPrice()));
        holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));
        holder.tvTrangThai.setText(monAn.getStatus());

        // Tải hình ảnh
        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgFood);

        // Xử lý tăng số lượng
        holder.btnTang.setOnClickListener(v -> {
            int currentQuantity = monAn.getSoLuong();
            currentQuantity++;
            monAn.setSoLuong(currentQuantity);
            holder.tvSoLuong.setText(String.valueOf(currentQuantity));
            capNhatSoLuongMonAnTrongFirestore(monAn);
        });

        // Xử lý giảm số lượng
        holder.btnGiam.setOnClickListener(v -> {
            int currentQuantity = monAn.getSoLuong();
            if (currentQuantity > 1) {
                currentQuantity--;
                monAn.setSoLuong(currentQuantity);
                holder.tvSoLuong.setText(String.valueOf(currentQuantity));
                capNhatSoLuongMonAnTrongFirestore(monAn);
            }
        });

        // Xử lý xóa món ăn
        holder.btnXoa.setOnClickListener(v -> {
            xoaMonAnTuFirestore(monAn);
            listMonDaChon.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listMonDaChon.size());

            if (listener != null) {
                listener.onMonDaChonUpdated(listMonDaChon.size());
            }
        });

        // Xử lý nút Chọn làm
        holder.btnChonLam.setOnClickListener(v ->
                capNhatTrangThaiMonAn(position, "Đang làm"));

        // Xử lý nút Đã lấy
        holder.btnDaLay.setOnClickListener(v ->
                capNhatTrangThaiMonAn(position, "Đã lấy"));
    }

    @Override
    public int getItemCount() {
        return listMonDaChon != null ? listMonDaChon.size() : 0;
    }

    // Cập nhật trạng thái món ăn
    private void capNhatTrangThaiMonAn(int position, String trangThai) {
        if (position >= 0 && position < listMonDaChon.size()) {
            Food monAn = listMonDaChon.get(position);
            monAn.setStatus(trangThai);
            notifyItemChanged(position);
            capNhatTrangThaiTrenFirestore(monAn);
        }
    }

    // Cập nhật trạng thái trên Firestore
    private void capNhatTrangThaiTrenFirestore(Food monAn) {
        Map<String, Object> foodData = taoMapMonAn(monAn);

        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.arrayRemove(foodData))
                .addOnSuccessListener(aVoid ->
                        firestore.collection("tables").document(tableId)
                                .update("selectedFoods", FieldValue.arrayUnion(foodData))
                                .addOnSuccessListener(unused ->
                                        Log.d("DaChonAdapter", "Cập nhật trạng thái thành công"))
                                .addOnFailureListener(e ->
                                        Log.e("DaChonAdapter", "Lỗi cập nhật trạng thái", e))
                )
                .addOnFailureListener(e ->
                        Log.e("DaChonAdapter", "Lỗi xóa món ăn cũ", e));
    }

    // Cập nhật số lượng trên Firestore
    private void capNhatSoLuongMonAnTrongFirestore(Food monAn) {
        Map<String, Object> foodData = taoMapMonAn(monAn);

        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.arrayRemove(foodData))
                .addOnSuccessListener(aVoid ->
                        firestore.collection("tables").document(tableId)
                                .update("selectedFoods", FieldValue.arrayUnion(foodData))
                                .addOnSuccessListener(unused ->
                                        Log.d("DaChonAdapter", "Cập nhật số lượng thành công"))
                                .addOnFailureListener(e ->
                                        Log.e("DaChonAdapter", "Lỗi cập nhật số lượng", e))
                )
                .addOnFailureListener(e ->
                        Log.e("DaChonAdapter", "Lỗi xóa món ăn cũ", e));
    }

    // Xóa món ăn khỏi Firestore
    private void xoaMonAnTuFirestore(Food monAn) {
        Map<String, Object> foodData = taoMapMonAn(monAn);

        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.arrayRemove(foodData))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DaChonAdapter", "Món ăn đã được xóa khỏi Firestore: " + monAn.getName());
                    } else {
                        Log.e("DaChonAdapter", "Lỗi khi xóa món ăn khỏi Firestore: ", task.getException());
                    }
                });
    }

    // Tạo Map cho món ăn
    private Map<String, Object> taoMapMonAn(Food monAn) {
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", monAn.getId());
        foodData.put("name", monAn.getName());
        foodData.put("price", monAn.getPrice());
        foodData.put("soLuong", monAn.getSoLuong());
        foodData.put("image", monAn.getImage());
        foodData.put("trangThai", monAn.getStatus());
        return foodData;
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvGia, tvTrangThai;
        AppCompatButton btnTang, btnGiam, btnXoa, btnChonLam, btnDaLay;
        ShapeableImageView imgFood;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAnDaChon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGia = itemView.findViewById(R.id.tvGiaMonAnDaChon);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            btnChonLam = itemView.findViewById(R.id.btnChonLam);
            btnDaLay = itemView.findViewById(R.id.btnDaLay);
        }
    }
}