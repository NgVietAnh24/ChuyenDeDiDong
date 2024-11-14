package vn.vietanhnguyen.khachhangdatmon.activities;

import static android.content.ContentValues.TAG;
import static vn.vietanhnguyen.khachhangdatmon.activities.DanhSachChonMon.btnMonDaChon;

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

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.adapters.DaChonAdapter;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class DanhSachDaChon extends AppCompatActivity {
    public static ImageView btnBack;
    public static AppCompatButton btnHuyDon, btnThanhToan, btnBackHome;
    public static RecyclerView listChonMon;
    public static EditText edtGhiChu;
    public static View overlayView;
    private TextView txtDanhSachMon;

    private FirebaseFirestore firestore;
    private DaChonAdapter daChonAdapter;
    private List<MonAn> listMonAnDaChon;

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
                            // Cập nhật thành công
                            Intent intent1 = new Intent(DanhSachDaChon.this, Home.class);
                            startActivity(intent1);
                            DanhSachChonMon.btnBack.setVisibility(View.VISIBLE);
                            xoaTatCaMonDaChon(tableId);
                            Log.d("DanhSachDaChon", "Trạng thái bàn đã được cập nhật thành 'Trống'");
                            // Có thể thêm logic nếu muốn thông báo cho người dùng hoặc cập nhật UI.
                        } else {
                            // Lỗi khi cập nhật trạng thái bàn
                            Log.d("DanhSachDaChon", "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                        }
                    });

            // Bạn có thể thêm logic để cập nhật giao diện nếu cần.
        });

        // Xử lý sự kiện cho nút Thanh toán
        btnThanhToan.setOnClickListener(v -> {
            String ghiChu = edtGhiChu.getText().toString();

            // Thực hiện hành động thanh toán tại đây
            Toast.makeText(this, "Đang thanh toán", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(this, ThanhToan.class);
//            intent1.putParcelableArrayListExtra("monDaChon", (ArrayList<MonAn>) listMonAnDaChon);
            intent1.putParcelableArrayListExtra("monDaChon", (ArrayList<? extends Parcelable>) listMonAnDaChon);
            intent1.putExtra("id", tableId);
            intent1.putExtra("ghiChu", ghiChu);
            startActivity(intent1);
        });

        btnBack.setOnClickListener(v -> {
            suaSoLuongMonAn(tableId, listMonAnDaChon);
            finish();
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DanhSachDaChon.this, Home.class);
                startActivity(intent1);
                btnThanhToan.setVisibility(View.VISIBLE);
                btnHuyDon.setVisibility(View.VISIBLE);
                edtGhiChu.setEnabled(true);
                overlayView.setVisibility(View.GONE);
                btnBackHome.setVisibility(View.GONE);
                xoaTatCaMonDaChon(tableId);
            }
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

    private void suaSoLuongMonAn(String tableId, List<MonAn> listMonAnDaChon) {
        // Cập nhật số lượng món ăn trong danh sách đã chọn
        List<Map<String, Object>> updatedFoods = new ArrayList<>();

        for (MonAn monAn : listMonAnDaChon) {
            Map<String, Object> foodData = new HashMap<>();
            foodData.put("id", monAn.getId());
            foodData.put("name", monAn.getName());
            foodData.put("price", monAn.getPrice());
            foodData.put("soLuong", monAn.getSoLuong());
            foodData.put("image", monAn.getImage());
            updatedFoods.add(foodData); // Thêm vào danh sách cập nhật
        }

        // Cập nhật danh sách món đã chọn vào Firestore
        firestore.collection("tables").document(tableId)
                .update("selectedFoods", updatedFoods)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Số lượng món ăn đã được cập nhật thành công");
                        } else {
                            Log.d(TAG, "Lỗi khi cập nhật số lượng món ăn: ", task.getException());
                        }
                    }
                });
    }

    private void layDanhSachMonDaChon(String tableId) {
        Log.d(TAG, "Lấy danh sách món đã chọn cho bàn: " + tableId);
        firestore.collection("tables").document(tableId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<Map<String, Object>> selectedFoods = (List<Map<String, Object>>) document.get("selectedFoods");
                                if (selectedFoods != null) {
                                    listMonAnDaChon.clear();
                                    for (Map<String, Object> foodData : selectedFoods) {
                                        MonAn monAn = new MonAn();
                                        // Lấy các giá trị từ Map
                                        monAn.setId((String) foodData.get("id"));
                                        monAn.setName((String) foodData.get("name"));
                                        monAn.setPrice((String) foodData.get("price"));
                                        monAn.setSoLuong(((Long) foodData.get("soLuong")).intValue());
                                        monAn.setImage((String) foodData.get("image"));
                                        listMonAnDaChon.add(monAn); // Thêm món ăn vào danh sách
                                    }
                                    daChonAdapter.notifyDataSetChanged(); // Cập nhật adapter
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
