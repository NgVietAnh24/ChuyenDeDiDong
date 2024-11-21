package vn.posicode.chuyende.DanhSachMonAn;

import static android.content.ContentValues.TAG;

import static vn.posicode.chuyende.DanhSachMonAn.DanhSachChonMon.btnMonDaChon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.ChiTietHoaDon.InvoiceDetailActivity;
import vn.posicode.chuyende.R;
import vn.posicode.chuyende.TrangThaiDanhSachBan.TableListActivity;
import vn.posicode.chuyende.models.MonAn;

public class DanhSachDaChon extends AppCompatActivity {
    public static ImageView btnBack;
    public static AppCompatButton btnHuyDon, btnThanhToan, btnBackHome;
    public static RecyclerView listChonMon;
    public static EditText edtGhiChu;
    public static View overlayView;
    private TextView txtDanhSachMon;

    private FirebaseFirestore firestore;
    private DaChonAdapter daChonAdapter;
    private List<Food> listMonAnDaChon;

    private String tableId;
    private String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_da_chon);
        Event();

        firestore = FirebaseFirestore.getInstance();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        tableId = intent.getStringExtra("id");
        tableName = intent.getStringExtra("name");
        listMonAnDaChon = intent.getParcelableArrayListExtra("mon"); // Nhận danh sách món đã chọn

        // Khởi tạo danh sách nếu null
        if (listMonAnDaChon == null) {
            listMonAnDaChon = new ArrayList<>();
        }

        // Gọi phương thức để lấy danh sách món đã chọn từ Firebase
        layDanhSachMonDaChon(tableId);

        txtDanhSachMon.setText("Danh sách món: " + tableName);

        // Khởi tạo RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listChonMon.setLayoutManager(layoutManager);

        // Khởi tạo adapter và gán danh sách món đã chọn
        daChonAdapter = new DaChonAdapter(listMonAnDaChon, new DaChonAdapter.OnMonDaChonUpdateListener() {
            @Override
            public void onMonDaChonUpdated(int newCount) {
                updateSelectedCount(newCount); // Cập nhật số lượng mới
            }
        }, firestore, tableId);

        listChonMon.setAdapter(daChonAdapter);

        // Xử lý sự kiện cho nút Hủy đơn
        btnHuyDon.setOnClickListener(v -> {
            // Thực hiện hành động hủy đơn tại đây
            Toast.makeText(this, "Đơn đã bị hủy", Toast.LENGTH_SHORT).show();

            // Cập nhật trạng thái bàn thành "Trống" trong Firestore
            firestore.collection("tables").document(tableId)
                    .update("status", "Trống")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent1 = new Intent(DanhSachDaChon.this, TableListActivity.class);
                            startActivity(intent1);
                            DanhSachChonMon.btnBack.setVisibility(View.VISIBLE);
                            xoaTatCaMonDaChon(tableId);
                            Log.d("DanhSachDaChon", "Trạng thái bàn đã được cập nhật thành 'Trống'");
                        } else {
                            Log.d("DanhSachDaChon", "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                        }
                    });
        });

        // Xử lý sự kiện cho nút Thanh toán
        btnThanhToan.setOnClickListener(v -> {
            String ghiChu = edtGhiChu.getText().toString();
            taoHoaDon(tableId, ghiChu); // Gọi phương thức tạo hóa đơn
        });

        btnBack.setOnClickListener(v -> {
            suaSoLuongMonAn(tableId, listMonAnDaChon);
            finish();
        });

        btnBackHome.setOnClickListener(view -> {
            Intent intent1 = new Intent(DanhSachDaChon.this, TableListActivity.class);
            startActivity(intent1);
            btnThanhToan.setVisibility(View.VISIBLE);
            btnHuyDon.setVisibility(View.VISIBLE);
            edtGhiChu.setEnabled(true);
            overlayView.setVisibility(View.GONE);
            btnBackHome.setVisibility(View.GONE);
            xoaTatCaMonDaChon(tableId);
        });
    }

    private void taoHoaDon(String tableId, String ghiChu) {
        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("ban_id", tableId);
        invoiceData.put("ghi_chu", ghiChu);
        invoiceData.put("ngay_tao", System.currentTimeMillis());
        invoiceData.put("tinh_trang", "Chưa thanh toán");

        // Tính tổng tiền
        double total = 0;
        for (Food food : listMonAnDaChon) {
            // Chuyển đổi giá từ String sang double
            double price = Double.parseDouble(food.getPrice()); // Chuyển đổi
            total += price * food.getSoLuong(); // Nhân với số lượng
        }
        invoiceData.put("tong_tien", total);

        // Tạo hóa đơn trong Firestore
        firestore.collection("invoices").add(invoiceData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Hóa đơn đã được tạo với ID: " + documentReference.getId());
                    Toast.makeText(DanhSachDaChon.this, "Hóa đơn đã được tạo", Toast.LENGTH_SHORT).show();
                    // Chuyển sang màn hình chi tiết hóa đơn
                    Intent intent = new Intent(DanhSachDaChon.this, InvoiceDetailActivity.class);
                    intent.putExtra("invoiceId", documentReference.getId());
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tạo hóa đơn: ", e);
                    Toast.makeText(DanhSachDaChon.this, "Lỗi khi tạo hóa đơn", Toast.LENGTH_SHORT).show();
                });
    }

    private void xoaTatCaMonDaChon(String tableId) {
        firestore.collection("tables").document(tableId)
                .update("selectedFoods", FieldValue.delete())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ThanhToan", "Đã xóa tất cả các món đã chọn khỏi Firestore.");
                    } else {
                        Log.d("ThanhToan", "Lỗi khi xóa món đã chọn khỏi Firestore: ", task.getException());
                    }
                });
    }

    private void suaSoLuongMonAn(String tableId, List<Food> listMonAnDaChon) {
        List<Map<String, Object>> updatedFoods = new ArrayList<>();

        for (Food monAn : listMonAnDaChon) {
            Map<String, Object> foodData = new HashMap<>();
            foodData.put("id", monAn.getId());
            foodData.put("name", monAn.getName());
            foodData.put("price", monAn.getPrice());
            foodData.put("soLuong", monAn.getSoLuong());
            foodData.put("image", monAn.getImage());

            // Thêm trạng thái
            foodData.put("trangThai",
                    monAn.getTrangThai() != null ?
                            monAn.getTrangThai() :
                            "Chưa làm"
            );

            updatedFoods.add(foodData);
        }

        firestore.collection("tables").document(tableId)
                .update("selectedFoods", updatedFoods)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Số lượng và trạng thái món ăn đã được cập nhật thành công");
                    } else {
                        Log.d(TAG, "Lỗi khi cập nhật số lượng và trạng thái món ăn: ", task.getException());
                    }
                });
    }

    private void layDanhSachMonDaChon(String tableId) {
        Log.d(TAG, "Lấy danh sách món đã chọn cho bàn: " + tableId);
        firestore.collection("tables").document(tableId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<Map<String, Object>> selectedFoods = (List<Map<String, Object>>) document.get("selectedFoods");
                            if (selectedFoods != null) {
                                listMonAnDaChon.clear();
                                for (Map<String, Object> foodData : selectedFoods) {
                                    Food monAn = new Food();
                                    monAn.setId((String) foodData.get("id"));
                                    monAn.setName((String) foodData.get("name"));
                                    monAn.setPrice(((String) foodData.get("price")));
                                    monAn.setSoLuong(((Long) foodData.get("soLuong")).intValue());
                                    monAn.setImage((String) foodData.get("image"));

                                    // Thêm phần trạng thái
                                    monAn.setTrangThai(
                                            foodData.containsKey("trangThai") ?
                                                    (String) foodData.get("trangThai") :
                                                    "Chưa làm"  // Giá trị mặc định nếu không có
                                    );

                                    listMonAnDaChon.add(monAn);
                                }
                                daChonAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Danh sách món đã chọn: " + listMonAnDaChon.toString());
                            } else {
                                Log.d(TAG, "Không có món nào đã chọn.");
                            }
                        } else {
                            Log.d(TAG, "Tài liệu không tồn tại.");
                        }
                    } else {
                        Log.d(TAG, "Lỗi khi lấy danh sách món đã chọn: ", task.getException());
                    }
                });
    }
    private void updateSelectedCount(int newCount) {
        String buttonText = "Món đã chọn (" + newCount + ")";
        btnMonDaChon.setText(buttonText);
    }

    private void Event() {
        txtDanhSachMon = findViewById(R.id.tvDanhSachBan);
        btnBack = findViewById(R.id.btnBack);
        btnHuyDon = findViewById(R.id.btnHuyDon);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        listChonMon = findViewById(R.id.listChonMon);
        edtGhiChu = findViewById(R.id.edtGhichu);
        btnBackHome = findViewById(R.id.btnBackHome);
        overlayView = findViewById(R.id.overlayView);
    }
}