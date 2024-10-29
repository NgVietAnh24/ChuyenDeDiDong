package vn.posicode.chuyende .DanhSachMonAn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.posicode.chuyende.R;

public class SelectedFoodActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout selectedFoodLayout;
    private Button btnCancelOrder, btnPay;
    private EditText etNote;
    private TextView tvTableName;

    private ArrayList<Food> selectedFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);

        // Khởi tạo các view
        btnBack = findViewById(R.id.btnQuayLai);
        selectedFoodLayout = findViewById(R.id.selected_food_list);
        btnCancelOrder = findViewById(R.id.btnHuyDon);
        btnPay = findViewById(R.id.btnThanhToan);
        etNote = findViewById(R.id.etGhiChu);
        tvTableName = findViewById(R.id.tvTieuDe);

        // Gán sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nhận danh sách món đã chọn và tên bàn từ Intent
        selectedFoodList = getIntent().getParcelableArrayListExtra("selectedFoodList");
        String tableName = getIntent().getStringExtra("tableName");

        // Hiển thị tên bàn
        tvTableName.setText("Tên bàn: " + tableName);

        // Hiển thị danh sách món đã chọn
        if (selectedFoodList != null) {
            for (Food food : selectedFoodList) {
                addFoodView(food); // Thêm món ăn vào layout
            }
        }

        // Gán sự kiện cho nút hủy đơn
        btnCancelOrder.setOnClickListener(v -> {
            updateTableStatus("Trống");
            Toast.makeText(this, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        });

        // Gán sự kiện cho nút thanh toán
        btnPay.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thanh toán", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        });
    }

    // Hàm thêm món ăn vào layout
    private void addFoodView(Food food) {
        View foodView = LayoutInflater.from(this).inflate(R.layout.selected_food_item, selectedFoodLayout, false);

        ImageView imgFood = foodView.findViewById(R.id.imgMonAn);
        TextView tvFoodName = foodView.findViewById(R.id.tvTenMonAn);
        TextView tvFoodPrice = foodView.findViewById(R.id.tvGiaMonAn);
        TextView tvStatus = foodView.findViewById(R.id.tvTrangThai);
        TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);

        imgFood.setImageResource(food.getImageResId());
        tvFoodName.setText(food.getName());
        tvFoodPrice.setText(food.getPrice());
        tvStatus.setText("Chưa được đặt."); // Trạng thái mặc định
        tvQuantity.setText("1"); // Số lượng mặc định

        // Gán sự kiện cho nút chọn làm món
        foodView.findViewById(R.id.btnChonLam).setOnClickListener(v -> {
            tvStatus.setText("Đã chọn làm."); // Cập nhật trạng thái
            Toast.makeText(this, "Đã chọn làm món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        // Gán sự kiện cho nút đã lấy món
        foodView.findViewById(R.id.btnDaLay).setOnClickListener(v -> {
            tvStatus.setText("Đã lấy."); // Cập nhật trạng thái
            Toast.makeText(this, "Đã lấy món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        // Gán sự kiện cho nút xóa món
        foodView.findViewById(R.id.btnXoa).setOnClickListener(v -> {
            Toast.makeText(this, "Đã xóa món " + food.getName(), Toast.LENGTH_SHORT).show();
            selectedFoodLayout.removeView(foodView); // Xóa món ăn khỏi layout
        });

        // Gán sự kiện cho nút tăng số lượng
        foodView.findViewById(R.id.btnTang).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString()) + 1;
            tvQuantity.setText(String.valueOf(quantity)); // Cập nhật số lượng
        });

        // Gán sự kiện cho nút giảm số lượng
        foodView.findViewById(R.id.btnGiam).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 1) { // Không giảm xuống dưới 1
                quantity--;
                tvQuantity.setText(String.valueOf(quantity)); // Cập nhật số lượng
            }
        });

        selectedFoodLayout.addView(foodView); // Thêm món ăn vào layout
    }

    private void updateTableStatus(String status) {
        String tableName = tvTableName.getText().toString().replace("Tên bàn: ", "");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("status", status);

                        firestore.collection("tables")
                                .document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Trạng thái bàn đã được cập nhật thành " + status);

                                    // Gửi broadcast để cập nhật UI
                                    Intent broadcastIntent = new Intent("UPDATE_TABLE_STATUS");
                                    broadcastIntent.putExtra ("tableName", tableName);
                                    broadcastIntent.putExtra("status", status);
                                    sendBroadcast(broadcastIntent);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Lỗi khi cập nhật trạng thái bàn", e);
                                    Toast.makeText(SelectedFoodActivity.this,
                                            "Có lỗi xảy ra khi cập nhật trạng thái bàn",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tìm thông tin bàn",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error finding table", e);
                });
    }
}