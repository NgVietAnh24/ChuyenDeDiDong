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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
            intent1.putParcelableArrayListExtra("monDaChon", (ArrayList<? extends Parcelable>) listMonAnDaChon);
            intent1.putExtra("id", tableId);
            intent1.putExtra("ghiChu", ghiChu);
            startActivity(intent1);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnBackHome.setOnClickListener(view -> {
            Intent intent1 = new Intent(DanhSachDaChon.this, Home.class);
            startActivity(intent1);
            btnThanhToan.setVisibility(View.VISIBLE);
            btnHuyDon.setVisibility(View.VISIBLE);
            edtGhiChu.setEnabled(true);
            overlayView.setVisibility(View.GONE);
            btnBackHome.setVisibility(View.GONE);
            xoaTatCaMonDaChon(tableId);
        });
    }

    private void xoaTatCaMonDaChon(String tableId) {
        firestore.collection("selectedFoods").document(tableId)
                .update("selectedFoods", FieldValue.delete())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ThanhToan", "Đã xóa tất cả các món đã chọn khỏi Firestore.");
                    } else {
                        Log.d("ThanhToan", "Lỗi khi xóa món đã chọn khỏi Firestore: ", task.getException());
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
                                MonAn monAn = document.toObject(MonAn.class); // Chuyển đổi tài liệu thành đối tượng MonAn
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
        btnBackHome = findViewById(R.id.btnBackHome);
        overlayView = findViewById(R.id.overlayView);
    }
}