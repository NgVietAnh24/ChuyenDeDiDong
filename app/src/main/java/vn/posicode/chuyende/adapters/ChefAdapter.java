package vn.posicode.chuyende.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.MonAn;


public class ChefAdapter extends RecyclerView.Adapter<ChefAdapter.ViewHolder> {
    private List<MonAn> listMonDaChon;
    private FirebaseFirestore firestore;
    private String tableId;
    Context context;

    public ChefAdapter(List<MonAn> listMonDaChon, FirebaseFirestore firestore, String tableId,Context context) {
        this.listMonDaChon = listMonDaChon;
        this.firestore = firestore;
        this.tableId = tableId;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chef, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = listMonDaChon.get(position);

        holder.tvTenMon.setText(monAn.getName());
        holder.tvTime.setText(monAn.getTime());
        holder.tvStatus.setText(monAn.getStatus());
        holder.tvSoLuong.setText(String.valueOf("SL: " + monAn.getSoLuong()));

//        // Lấy số lượng đã xong từ model
    int soLuongDaXong = monAn.getSoLuongDaLay(); // Thay thế bằng phương thức thích hợp nếu cần
    holder.btnDaXong.setText("Đã xong: " + soLuongDaXong); // Hiển thị số lượng đã xong

        // Cập nhật trạng thái nút
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
        } else {
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

        holder.itemView.setOnLongClickListener(v -> {
            showDialog(monAn.getDocumentId());
            Log.d("AAAAAAAAAAAA","ssssssssssssss"+monAn.getDocumentId());
            return true; // Trả về true để chỉ ra rằng sự kiện đã được xử lý
        });

        // Xử lý nút "Đã xong"
        holder.btnDaXong.setOnClickListener(v -> {
            showCompleteDialog(monAn);
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

    private void showCompleteDialog(MonAn monAn) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_sl);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        EditText etQuantity = dialog.findViewById(R.id.etQuantity);
        Button btnComplete = dialog.findViewById(R.id.btnComplete);
        Button btnTang = dialog.findViewById(R.id.tang);
        Button btnGiam = dialog.findViewById(R.id.giam);

        // Hiển thị số lượng đã lấy hiện tại
        tvTitle.setText("Số lượng đã xong: " + monAn.getName());
        etQuantity.setText(String.valueOf(monAn.getSoLuongDaLay()));

        btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityStr = etQuantity.getText().toString().trim();
                int newQuantity;

                // Kiểm tra xem người dùng có nhập số lượng hay không
                if (quantityStr.isEmpty()) {
                    newQuantity = 0; // Nếu không có giá trị, đặt mặc định là 0
                } else {
                    newQuantity = Integer.parseInt(quantityStr);
                }

                newQuantity++; // Tăng số lượng
                etQuantity.setText(String.valueOf(newQuantity)); // Cập nhật lại giá trị vào EditText
            }
        });

        btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityStr = etQuantity.getText().toString().trim();
                int newQuantity;

                // Kiểm tra xem người dùng có nhập số lượng hay không
                if (quantityStr.isEmpty()) {
                    newQuantity = 0; // Nếu không có giá trị, đặt mặc định là 0
                } else {
                    newQuantity = Integer.parseInt(quantityStr);
                }

                if (newQuantity > 0) { // Đảm bảo số lượng không âm
                    newQuantity--; // Giảm số lượng
                }

                etQuantity.setText(String.valueOf(newQuantity)); // Cập nhật lại giá trị vào EditText
            }
        });

        btnComplete.setOnClickListener(v -> {
            String quantityStr = etQuantity.getText().toString().trim();
            if (!quantityStr.isEmpty()) {
                int newQuantity = Integer.parseInt(quantityStr);
                int currentQuantity = monAn.getSoLuong(); // Số lượng hiện tại
                int existingQuantity = monAn.getSoLuongDaLay(); // Số lượng đã lấy trước đó

                // Tính số lượng đã xong mới
                int completedQuantity = existingQuantity + newQuantity;

                if (completedQuantity <= currentQuantity) {
                    // Cập nhật số lượng đã xong
                    monAn.setSoLuongDaLay(completedQuantity); // Cập nhật số lượng đã xong
                    Toast.makeText(context, "Số lượng đã xong: " + completedQuantity, Toast.LENGTH_SHORT).show();

                    // Cập nhật Firestore
                    String documentId = monAn.getDocumentId(); // Giả sử bạn có phương thức này
                    if (completedQuantity == currentQuantity) {
                        updateFoodStatusDaLay(documentId, "Đã xong", completedQuantity); // Cập nhật trạng thái nếu đã xong
                    } else {
                        updateFoodStatusDaLay(documentId, monAn.getStatus(), completedQuantity); // Cập nhật trạng thái và số lượng đã xong
                    }

                    notifyDataSetChanged(); // Làm mới RecyclerView để hiển thị số lượng mới
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Số lượng đã xong không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Vui lòng nhập số lượng!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showDialog(String documentId) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_note);

        EditText etQuantity = dialog.findViewById(R.id.edtQuantity);

        // Lắng nghe thay đổi từ Firestore
        firestore.collection("selectedFoods").document(documentId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChefAdapter", "Lỗi khi lấy dữ liệu: ", e);
                        Toast.makeText(context, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Lấy giá trị của trường sLChef
                        String sLChef = (String) documentSnapshot.get("sLChef");
                        Log.d("AAAAAAAAAAAA", "ssssssssssssss " + sLChef);

                        if (sLChef != null) {
                            // Hiển thị giá trị của sLChef trong EditText
                            etQuantity.setText(sLChef);
                        } else {
                            etQuantity.setText("Không có ghi chú nào!");
                        }
                    } else {
                        Toast.makeText(context, "Không tìm thấy tài liệu!", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog.show();
    }

    private void updateFoodStatusDaLay(String documentId, String status, int completedQuantity) {
        // Cập nhật trạng thái và số lượng đã xong trong Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("soLuongDaLay", completedQuantity); // Cập nhật số lượng đã xong

        firestore.collection("selectedFoods")
                .document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "Cập nhật thành công!");
                })
                .addOnFailureListener(e -> {
                    Log.w("TAG", "Cập nhật không thành công", e);
                });
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