package vn.posicode.chuyende.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.Ban;
import vn.posicode.chuyende.models.MonAn;


public class BanChonAdapter extends RecyclerView.Adapter<BanChonAdapter.ViewHolder> {
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private List<Ban> list;
    private OnItemClickListener onItemClickListener;
    Context context;

    public interface OnItemClickListener{
        void onItemClick(Ban ban);
    }

    public BanChonAdapter(List<Ban> list, OnItemClickListener onItemClickListener, Context context) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull BanChonAdapter.ViewHolder holder, int position) {
        Ban ban = list.get(position);
        holder.tvTenBan.setText(ban.getName());
        holder.tvMoTa.setText(ban.getDescription());



        // Lấy số lượng món ăn đã chọn từ Firestore
        listenerRegistration = firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", ban.getId()) // Giả sử bạn có phương thức getId() trong Ban
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("BanAdapter", "Lỗi khi lấy số lượng món ăn: ", e);
                        holder.tvTB.setText("0"); // Nếu có lỗi, hiển thị 0
                        return;
                    }

                    int tongSoLuong = 0;
                    int tongSGhiChu = 0;
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            Long soLuong = document.getLong("soLuongDaLay"); // Lấy số lượng từ Firestore
                            Long soLuongGhiChu = document.getLong("sLGhiChu"); // Lấy số lượng ghi chú từ Firestore
                            if (soLuong != null) {
                                tongSoLuong += soLuong.intValue();
                            }
                            if (soLuongGhiChu != null) {
                                tongSGhiChu += soLuongGhiChu.intValue();
                            }
                        }
                        // Tính tổng số lượng sau khi trừ
                        Log.d("XXXXXXXX","+++++++++++++++++++++++++ == :"+tongSoLuong);
                        Log.d("XXXXXXXX","+++++++++++++++++++++++++ == :"+tongSGhiChu);
                        int finalTongSoLuong = tongSoLuong - tongSGhiChu;
                        // Hiển thị tổng số lượng lên tvTB
                        holder.tvTB.setText("( " + String.valueOf(finalTongSoLuong) + " )");
                    }
                });

        String state = ban.getStatus();
        Log.d("BanAdapter", "State: " + state); // Log để kiểm tra giá trị state

        if (ban.getStatus() != null) {
            if(ban.getStatus().equals("Trống")){
                holder.vState.setBackgroundResource(R.drawable.rounded_trang_thai);
            }else if(ban.getStatus().equals("Đã đặt")){
                holder.vState.setBackgroundResource(R.drawable.rounded_trang_thai_da_dat);
            }else if(ban.getStatus().equals("Đang sử dụng")) {
                holder.vState.setBackgroundResource(R.drawable.rounded_trang_thai_dang_su_dung);
            }

        } else {
            // Xử lý trường hợp state là null
            holder.vState.setBackgroundResource(R.drawable.item_background_selector); // Hoặc một màu khác phù hợp
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(ban.getStatus().equals("Trống")) {
                onItemClickListener.onItemClick(ban);
//                }else {
//                    showAlert("Đã có khách");
//                    Toast.makeText(Home.this,"c", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }




    @NonNull
    @Override
    public BanChonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_chon, parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBan, tvMoTa, tvTB;
        View vState;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBan = itemView.findViewById(R.id.tvTenBan);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            vState = itemView.findViewById(R.id.vState);
            tvTB = itemView.findViewById(R.id.tvTB);
        }
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}