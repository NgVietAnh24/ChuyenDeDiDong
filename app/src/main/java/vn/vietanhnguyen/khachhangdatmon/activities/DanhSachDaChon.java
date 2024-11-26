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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.adapters.DaChonAdapter;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class DanhSachDaChon extends AppCompatActivity {
    public static ImageView btnBack, btnInvoice;
    public static AppCompatButton btnHuyDon, btnThanhToan, btnBackHome, btnLamAll;
    public static RecyclerView listChonMon;
    public static EditText edtGhiChu;
    public static View overlayView;
    private TextView txtDanhSachMon;

    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
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
        listMonAnDaChon = new ArrayList<>(); // Khởi tạo danh sách món đã chọn

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

       layGhiChuTheoBanId(tableId);

        // Xử lý sự kiện cho nút Hủy đơ
        btnHuyDon.setOnClickListener(v -> {
            // Thực hiện hành động hủy đơn tại đây
            Toast.makeText(this, "Đơn đã bị hủy", Toast.LENGTH_SHORT).show();

            // Cập nhật trạng thái bàn thành "Trống" trong Firestore
            firestore.collection("tables").document(tableId)
                    .update("status", "Trống")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Cập nhật thành công
                            Log.d("DanhSachDaChon", "Trạng thái bàn đã được cập nhật thành 'Trống'");

                            // Gọi phương thức xóa tất cả món đã chọn sau khi cập nhật trạng thái bàn thành công
                            xoaMonDaChonTheoBanId(tableId);

                            // Chuyển đến màn hình Home
                            Intent intent1 = new Intent(DanhSachDaChon.this, Home.class);
                            startActivity(intent1);
                            DanhSachChonMon.btnBack.setVisibility(View.VISIBLE);
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
            intent1.putParcelableArrayListExtra("monDaChon", (ArrayList<? extends Parcelable>) listMonAnDaChon);
            intent1.putExtra("id", tableId);
            intent1.putExtra("ghiChu", ghiChu);
            startActivity(intent1);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnLamAll.setOnClickListener(v ->
        {
            capNhatTrangThaiMonDaChon(tableId,"Đang chuẩn bị");
        });
    }

    private void capNhatTrangThaiMonDaChon(String banId, String trangThai) {

        Date now = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm\ndd/MM/yyyy");
        String formattedTime = time.format(now);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Đang chuẩn bị");
        updates.put("time", formattedTime);

        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", banId)
                .whereEqualTo("status", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Cập nhật trạng thái cho từng món ăn
                            firestore.collection("selectedFoods").document(document.getId())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("CapNhatTrangThai", "Đã cập nhật trạng thái cho món: " + document.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("CapNhatTrangThai", "Lỗi khi cập nhật trạng thái cho món: ", e);
                                    });
                        }
                        Toast.makeText(DanhSachDaChon.this, "Đã cập nhật tất cả món ăn thành 'Đang chuẩn bị'", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("CapNhatTrangThai", "Lỗi khi lấy danh sách món đã chọn: ", task.getException());
                    }
                });
    }

    private void layGhiChuTheoBanId(String banId) {
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", banId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("GhiChuError", "Lỗi khi lấy ghi chú: ", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        StringBuilder ghiChuBuilder = new StringBuilder();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String ghiChu = document.getString("ghiChu");
                            if (ghiChu != null) {
                                ghiChuBuilder.append(ghiChu).append("\n");
                            }
                        }
                        edtGhiChu.setText(ghiChuBuilder.toString());
                    }
                });
    }

    private void xoaMonDaChonTheoBanId(String banId) {
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", banId) // Filter by ban_id
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Delete each document that matches the query
                            firestore.collection("selectedFoods").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("XoaMonDaChon", "Đã xóa món: " + document.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("XoaMonDaChon", "Lỗi khi xóa món: ", e);
                                    });
                        }
                    } else {
                        Log.w("XoaMonDaChon", "Lỗi khi lấy danh sách món đã chọn: ", task.getException());
                    }
                });
    }

    private void layDanhSachMonDaChon(String tableId) {
        Log.d(TAG, "Lấy danh sách món đã chọn cho bàn: " + tableId);
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", tableId) // Lọc theo tableId
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Lỗi khi lấy danh sách món đã chọn: ", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            listMonAnDaChon.clear(); // Xóa danh sách hiện tại
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String id = document.getId();
                                MonAn monAn = document.toObject(MonAn.class);
                                monAn.setDocumentId(id);// Chuyển đổi tài liệu thành đối tượng MonAn
                                listMonAnDaChon.add(monAn); // Thêm món ăn vào danh sách
                            }
                            daChonAdapter.notifyDataSetChanged(); // Cập nhật adapter
                            Log.d(TAG, "Danh sách món đã chọn: " + listMonAnDaChon.toString());
                        } else {
                            Log.d(TAG, "Không có món nào đã chọn.");
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
//        btnBackHome = findViewById(R.id.btnBackHome);
        overlayView = findViewById(R.id.overlayView);
        btnLamAll = findViewById(R.id.btnLamAll);
    }
}