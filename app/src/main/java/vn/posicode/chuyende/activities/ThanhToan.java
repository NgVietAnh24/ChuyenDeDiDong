package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapters.MonAnThanhToanAdapter;
import vn.posicode.chuyende.models.MonAn;

public class ThanhToan extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView tvMaHD, tvTenKH, tvSoDT, tvGhiChu, tvTongTien, tvTime, tvDate;
    private Button btnThanhToan;
    private RecyclerView recyclerViewMonAn;

    private FirebaseFirestore firestore;
    private long tongTien;
    MonAnThanhToanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        Event();

        firestore = FirebaseFirestore.getInstance();
        List<MonAn> monDaChon = getIntent().getParcelableArrayListExtra("monDaChon");

        // Kiểm tra danh sách món ăn
        if (monDaChon == null || monDaChon.isEmpty()) {
            Toast.makeText(this, "Không có món ăn nào được chọn!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu không có món ăn
            return;
        }

        // Tính tổng tiền
        tongTien = 0;
        for (MonAn monAn : monDaChon) {
            long gia = Long.parseLong(monAn.getPrice());
            tongTien += gia * monAn.getSoLuong();
        }
        String totalString = String.format("%d", tongTien);
        tvTongTien.setText("Tổng tiền: " + totalString + " VND");

        // Khởi tạo adapter và gán cho RecyclerView
        adapter = new MonAnThanhToanAdapter(monDaChon);
        recyclerViewMonAn.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMonAn.setAdapter(adapter);

        // Lấy ban_id từ Intent
        String banId = getIntent().getStringExtra("id");
        Log.d("NNN","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+banId);
        if (banId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin bàn!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu không có ban_id
            return;
        }

// Truy vấn thông tin khách hàng từ Firestore dựa trên ban_id
        firestore.collection("reservations")
                .whereEqualTo("tableId", banId) // Thay "ban_id" bằng tên trường trong Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            // Lấy thông tin khách hàng từ Firestore
                            String tenKH = document.getString("name");
                            String soDT = document.getString("phone");

                            // Kiểm tra xem tên và số điện thoại có hợp lệ không
                            if (tenKH == null || soDT == null) {
                                Toast.makeText(this, "Thông tin khách hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            String ghiChu = getIntent().getStringExtra("ghiChu");

                            Date now = new Date();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String time = timeFormat.format(now);
                            String date = dateFormat.format(now);

                            String maHD = "HD" + System.currentTimeMillis(); // Mã hóa đơn dựa trên thời gian hiện tại

                            // Hiển thị thông tin
                            tvMaHD.setText("Mã hóa đơn: " + maHD);
                            tvTenKH.setText("Tên khách hàng: " + tenKH);
                            tvSoDT.setText("Số điện thoại: " + soDT);
                            tvGhiChu.setText("Ghi chú:" + ghiChu);
                            tvTongTien.setText("Tổng tiền: " + totalString + " VND");
                            tvTime.setText(time);
                            tvDate.setText(date);

                            btnThanhToan.setOnClickListener(v -> {
                                Map<String, Object> invoiceData = new HashMap<>();
                                invoiceData.put("ban_id", getIntent().getStringExtra("id"));
                                invoiceData.put("tong_tien", tongTien);
                                invoiceData.put("gio_tao", time);
                                invoiceData.put("ngay_tao", date);
                                invoiceData.put("tinh_trang", "Chưa thanh toán");
                                invoiceData.put("ten_khach_hang", tenKH);
                                invoiceData.put("so_dt", soDT);
                                invoiceData.put("ghi_chu", ghiChu);

                                firestore.collection("invoices").document(maHD)
                                        .set(invoiceData)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(this, "Hoàn tất thanh toán ở quầy thu ngân!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("ThanhToan", "Lỗi khi lưu thông tin thanh toán: ", task1.getException());
                                            }
                                        });

                                for (MonAn monAn : monDaChon) {
                                    Map<String, Object> invoiceItemData = new HashMap<>();
                                    invoiceItemData.put("ten_mon_an", monAn.getName());
                                    invoiceItemData.put("gia", Integer.parseInt(monAn.getPrice()));
                                    invoiceItemData.put("so_luong", monAn.getSoLuong());
                                    invoiceItemData.put("hoa_don_id", maHD);

                                    String documentId = maHD + "_" + monAn.getName();

                                    firestore.collection("invoice_items").document(documentId)
                                            .set(invoiceItemData)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    showAlert("Chúc quý khách ngon miệng ☺️");
                                                } else {
                                                    Log.d("ThanhToan", "Lỗi khi lưu thông tin thanh toán: ", task1.getException());
                                                }
                                            });
                                }
                            });
                        } else {
                            Log.e("ThanhToan", "Không tìm thấy thông tin khách hàng");
                            Toast.makeText(this, "Không tìm thấy thông tin khách hàng!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("ThanhToan", "Lỗi khi truy vấn thông tin khách hàng: ", task.getException());
                        Toast.makeText(this, "Lỗi khi truy vấn thông tin khách hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                    btnThanhToan.setVisibility(View.GONE);
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void Event() {
        btnBack = findViewById(R.id.backButton);
        tvMaHD = findViewById(R.id.invoiceIdTextView);
        tvTenKH = findViewById(R.id.customerNameTextView);
        tvSoDT = findViewById(R.id.customerPhoneTextView);
        tvGhiChu = findViewById(R.id.noteTextView);
        tvTongTien = findViewById(R.id.totalTextView);
        tvTime = findViewById(R.id.invoiceTimeTextView);
        tvDate = findViewById(R.id.invoiceDateTextView);
        btnThanhToan = findViewById(R.id.paymentButton);
        recyclerViewMonAn = findViewById(R.id.itemsListView);
    }
}