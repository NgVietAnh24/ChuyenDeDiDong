package vn.vietanhnguyen.khachhangdatmon.adapters;

import static android.content.ContentValues.TAG;

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

import java.text.SimpleDateFormat;
import java.util.Date;
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
        if (monAn.getStatus().equals("Đang làm") || monAn.getStatus().equals("Đã xong")) {
            holder.btnHuy.setEnabled(true);
            holder.btnHuy.setAlpha(1);
            holder.btnXoa.setAlpha(0.5f);
            holder.btnXoa.setEnabled(false);
            holder.btnLamMon.setAlpha(0.5f);
            holder.btnLamMon.setEnabled(false);
            holder.btnTang.setAlpha(0.5f);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setAlpha(0.5f);
            holder.btnGiam.setEnabled(false);
        } else if (monAn.getStatus().equals("Đang chuẩn bị")) {
            holder.btnHuy.setEnabled(false);
            holder.btnHuy.setAlpha(0.5f);
            holder.btnLamMon.setAlpha(0.5f);
            holder.btnLamMon.setEnabled(false);
            holder.btnTang.setAlpha(0.5f);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setAlpha(0.5f);
            holder.btnGiam.setEnabled(false);
        }else {
            holder.btnHuy.setEnabled(false);
            holder.btnHuy.setAlpha(0.5f);
        }

        holder.tvTenMon.setText(monAn.getName());
        holder.tvGia.setText(String.valueOf(monAn.getPrice()));
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
            suaSoLuongMonAn(tableId, monAn);
            listener.onMonDaChonUpdated(listMonDaChon.size());
        });

        // Xử lý giảm số lượng
        holder.btnGiam.setOnClickListener(v -> {
            int currentQuantity = monAn.getSoLuong();
            if (currentQuantity > 1) {
                monAn.setSoLuong(currentQuantity - 1);
                holder.tvSoLuong.setText(String.valueOf(monAn.getSoLuong()));
                suaSoLuongMonAn(tableId, monAn);
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
            lamMonAn(tableId, monAn);
        });

        holder.btnHuy.setOnClickListener(v -> {
            holder.offbg.setVisibility(View.VISIBLE);
            holder.btnTang.setEnabled(false);
            holder.btnGiam.setEnabled(false);
            holder.btnLamMon.setEnabled(false);
            holder.btnXoa.setEnabled(false);
            holder.btnDaLay.setEnabled(false);
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

    private void lamMonAn(String tableId, MonAn monAn) {
        Date now = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        String formattedTime = time.format(now);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Đang chuẩn bị");
        updates.put("time", formattedTime);

        // Tìm tài liệu theo ban_id và id món ăn
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", tableId)
                .whereEqualTo("id", monAn.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Cập nhật trạng thái trong tài liệu
                            firestore.collection("selectedFoods")
                                    .document(document.getId()) // Lấy ID của tài liệu
                                    .update(updates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d(TAG, "Trạng thái món ăn đã được cập nhật thành công.");
                                            // Cập nhật lại trạng thái trên giao diện nếu cần
                                            monAn.setStatus("Đang chuẩn bị"); // Cập nhật trạng thái trong đối tượng món ăn
                                            notifyItemChanged(listMonDaChon.indexOf(monAn)); // Cập nhật lại item trong RecyclerView
                                        } else {
                                            Log.e(TAG, "Lỗi cập nhật trạng thái món ăn: ", updateTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Không tìm thấy món ăn để cập nhật trạng thái: ", task.getException());
                    }
                });
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


    private void suaSoLuongMonAn(String tableId, MonAn monAn) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("soLuong", monAn.getSoLuong());

        // Cập nhật tài liệu của món ăn
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", tableId)
                .whereEqualTo("id", monAn.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Cập nhật số lượng trong tài liệu
                            firestore.collection("selectedFoods")
                                    .document(document.getId()) // Lấy ID của tài liệu
                                    .update(updates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d(TAG, "Số lượng món ăn đã được cập nhật thành công.");
                                        } else {
                                            Log.e(TAG, "Lỗi cập nhật số lượng món ăn: ", updateTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Không tìm thấy món ăn để cập nhật số lượng: ", task.getException());
                    }
                });
    }
}