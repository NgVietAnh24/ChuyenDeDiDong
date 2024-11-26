package vn.vietanhnguyen.khachhangdatmon.adapters;

import static android.content.ContentValues.TAG;

import static vn.vietanhnguyen.khachhangdatmon.activities.DanhSachDaChon.edtGhiChu;

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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        if (monAn != null && monAn.getStatus() != null && (monAn.getStatus().equals("Đã xong") || monAn.getStatus().equals("Đang làm"))) {
            holder.btnHuy.setEnabled(false);
            holder.btnHuy.setAlpha(0.5f);
            holder.btnXoa.setAlpha(0.5f);
            holder.btnXoa.setEnabled(false);
            holder.btnLamMon.setAlpha(0.5f);
            holder.btnLamMon.setEnabled(false);
            holder.btnTang.setAlpha(0.5f);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setAlpha(0.5f);
            holder.btnGiam.setEnabled(false);
            holder.btnDaLay.setAlpha(1);
            holder.btnDaLay.setEnabled(true);
        } else if (monAn.getStatus().equals("Đang chuẩn bị")) {
            holder.btnHuy.setEnabled(true);
            holder.btnHuy.setAlpha(1);
            holder.btnLamMon.setAlpha(0.5f);
            holder.btnLamMon.setEnabled(false);
            holder.btnTang.setAlpha(0.5f);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setAlpha(0.5f);
            holder.btnGiam.setEnabled(false);
            holder.btnDaLay.setAlpha(0.5f);
            holder.btnDaLay.setEnabled(false);
        } else {
            holder.btnLamMon.setAlpha(1);
            holder.btnLamMon.setEnabled(true);
            holder.btnHuy.setEnabled(false);
            holder.btnHuy.setAlpha(0.5f);
            holder.btnTang.setAlpha(1);
            holder.btnTang.setEnabled(true);
            holder.btnGiam.setAlpha(1);
            holder.btnGiam.setEnabled(true);
            holder.btnDaLay.setAlpha(0.5f);
            holder.btnDaLay.setEnabled(false);
        }

        long price = Long.parseLong(monAn.getPrice());
        String formattedPrice = NumberFormat.getInstance(Locale.getDefault()).format(price);

        holder.btnDaLay.setText("Đã lấy: " + monAn.getSoLuongDaLay());
        holder.tvTenMon.setText(monAn.getName());
        holder.tvGia.setText(formattedPrice+" đ");
        holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));
        holder.tvStatus.setText(monAn.getStatus());

        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.image_error)
                .into(holder.imgFood);

        // Xử lý tăng số lượng
        holder.btnTang.setOnClickListener(v -> {
            int currentQuantity = monAn.getSoLuong();
            monAn.setSoLuong(currentQuantity + 1);
            holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));
            suaSoLuongMonAn(monAn.getDocumentId(), monAn); // Truyền documentId
            listener.onMonDaChonUpdated(listMonDaChon.size());
        });

        // Xử lý giảm số lượng
        holder.btnGiam.setOnClickListener(v -> {
            int currentQuantity = monAn.getSoLuong();
            if (currentQuantity > 1) {
                monAn.setSoLuong(currentQuantity - 1);
                holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));
                suaSoLuongMonAn(monAn.getDocumentId(), monAn); // Truyền documentId
                listener.onMonDaChonUpdated(listMonDaChon.size());
            }
        });

        // Xử lý xóa món ăn
        holder.btnXoa.setOnClickListener(v -> {
            xoaMonAnTuFirestore(tableId, monAn.getId()); // Truyền vào ID món ăn
            listMonDaChon.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listMonDaChon.size());
            listener.onMonDaChonUpdated(listMonDaChon.size());
        });

        holder.btnLamMon.setOnClickListener(v -> {
            String documentId = monAn.getDocumentId();
            lamMonAn(documentId, "Đang chuẩn bị");
            Log.d("ChefAdapter", "documentId: " + documentId);
        });
        String documentId = monAn.getDocumentId();
        Log.d("ChefAdapter", "documentId: " + documentId);

        holder.btnHuy.setOnClickListener(v -> {
            holder.offbg.setVisibility(View.VISIBLE);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setEnabled(false);
            holder.btnLamMon.setEnabled(false);
            holder.btnXoa.setEnabled(false);
            holder.btnDaLay.setEnabled(false);
        });

        holder.btnDaLay.setOnClickListener(v -> {
            int soLuongDaLay = monAn.getSoLuongDaLay();
            monAn.setSoLuongDaLay(soLuongDaLay);
            suaSoLuongMonAn(monAn.getDocumentId(), monAn); // Truyền documentId

            // Lấy tên và số lượng đã lấy
            String tenMonAn = monAn.getName();
            String soLuongDaLayText =": " + monAn.getSoLuongDaLay();

            String ghiChu = edtGhiChu.getText().toString();

            edtGhiChu.setText(ghiChu + tenMonAn + soLuongDaLayText + "\n");

            updateGhiChu(monAn.getDocumentId(), tenMonAn + soLuongDaLayText);

            listener.onMonDaChonUpdated(listMonDaChon.size());
        });
    }

    @Override
    public int getItemCount() {
        return listMonDaChon != null ? listMonDaChon.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvGia, tvStatus;
        AppCompatButton btnTang, btnGiam, btnXoa, btnLamMon, btnHuy, btnDaLay;
        ShapeableImageView imgFood;
        View offbg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAnDaChon);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvGia = itemView.findViewById(R.id.tvGiaMonAnDaChon);
            tvStatus = itemView.findViewById(R.id.tvTrangThai);
            btnTang = itemView.findViewById(R.id.btnTang);
            btnGiam = itemView.findViewById(R.id.btnGiam);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            btnLamMon = itemView.findViewById(R.id.btnLamMon);
            btnHuy = itemView.findViewById(R.id.btnHuy);
            offbg = itemView.findViewById(R.id.offbg);
            btnDaLay = itemView.findViewById(R.id.btnDaLay);
        }
    }

    private void updateGhiChu(String documentId, String ghiChu) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("ghiChu", ghiChu);

        // Cập nhật trường ghiChu trong tài liệu
        if (documentId != null) {
            firestore.collection("selectedFoods")
                    .document(documentId) // Sử dụng documentId để xác định tài liệu
                    .update(updates)
                    .addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d(TAG, "Ghi chú đã được cập nhật thành công.");
                        } else {
                            Log.e(TAG, "Lỗi cập nhật ghi chú: ", updateTask.getException());
                        }
                    });
        } else {
            Log.e(TAG, "documentId không được phép null");
        }
    }

    private void lamMonAn(String documentId, String newStatus) {
        Log.d("BNM", "=====================================================");
        Log.d("ChefAdapter", "documentId: " + documentId);
        Date now = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm\ndd/MM/yyyy");
        String formattedTime = time.format(now);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Đang chuẩn bị");
        updates.put("time", formattedTime);


        if (documentId != null) {
            // Cập nhật trạng thái trong tài liệu bằng cách sử dụng ID của tài liệu
            firestore.collection("selectedFoods")
                    .document(documentId) // Sử dụng ID của tài liệu để xác định tài liệu
                    .update(updates)
                    .addOnSuccessListener(aVoid -> Log.d("ChefAdapter", "Món ăn đã được cập nhật trạng thái '" + newStatus + "'"))
                    .addOnFailureListener(e -> Log.e("ChefAdapter", "Lỗi khi cập nhật trạng thái món ăn: ", e));
        } else {
            Log.e(TAG, "documentId không được phép null");
        }
    }

    private void xoaMonAnTuFirestore(String tableId, String monAnId) {
        // Tìm tài liệu theo ban_id và monAnId
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", tableId)
                .whereEqualTo("id", monAnId) // Tìm kiếm theo ID món ăn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Xóa tài liệu của món ăn
                            firestore.collection("selectedFoods")
                                    .document(document.getId()) // Lấy ID của tài liệu
                                    .delete() // Xóa tài liệu
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            Log.d(TAG, "Món ăn đã được xóa thành công.");
                                        } else {
                                            Log.e(TAG, "Lỗi xóa món ăn: ", deleteTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Không tìm thấy món ăn hoặc có lỗi: ", task.getException());
                    }
                });
    }

    private void suaSoLuongMonAn(String documentId, MonAn monAn) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("soLuong", monAn.getSoLuong());

        // Cập nhật tài liệu của món ăn theo documentId
        if (documentId != null) {
            firestore.collection("selectedFoods")
                    .document(documentId) // Sử dụng documentId để xác định tài liệu
                    .update(updates)
                    .addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Log.d(TAG, "Số lượng món ăn đã được cập nhật thành công.");
                        } else {
                            Log.e(TAG, "Lỗi cập nhật số lượng món ăn: ", updateTask.getException());
                        }
                    });
        } else {
            Log.e(TAG, "documentId không được phép null");
        }
    }
}