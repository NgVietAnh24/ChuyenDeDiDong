package vn.vietanhnguyen.khachhangdatmon.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.adapters.ChefAdapter;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class HomeDauBep extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private List<MonAn> listMonAnDaChon;

    private ChefAdapter chefAdapter;
    private RecyclerView listChefs;
    private ImageView btnHistory;
    private String tableId; // Khai báo biến tableId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dau_bep);
        Event();

        // Khởi tạo các biến
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        listMonAnDaChon = new ArrayList<>();

        listChefs.setLayoutManager(new LinearLayoutManager(this));

        // Lấy giá trị ban_id từ Firestore
        layTableId();

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeDauBep.this, LichSuDauBep.class);
                startActivity(intent);
            }
        });
    }

    private void layTableId() {
        // Lấy ban_id từ tài liệu trong bộ sưu tập selectedFoods
        firestore.collection("selectedFoods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                // Lấy giá trị ban_id từ tài liệu
                                tableId = document.getString("ban_id");
                                Log.d(TAG, "Lấy được tableId: " + tableId);

                                // Sau khi lấy được tableId, gọi phương thức để lấy danh sách món đã chọn
                                layDanhSachMonDaChon();
                                break; // Nếu chỉ cần lấy ban_id từ tài liệu đầu tiên, có thể break
                            }
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
                    }
                });
    }

    private void layDanhSachMonDaChon() {
        // Lắng nghe sự thay đổi trong bộ sưu tập selectedFoods
        firestore.collection("selectedFoods")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Lỗi khi lắng nghe thay đổi: ", e);
                        return;
                    }

                    if (value != null) {
                        listMonAnDaChon.clear(); // Xóa danh sách cũ trước khi thêm dữ liệu mới
                        for (DocumentSnapshot document : value.getDocuments()) {
                            MonAn monAn = document.toObject(MonAn.class); // Chuyển đổi tài liệu thành đối tượng MonAn
                            if (monAn != null && monAn.getStatus() != null && (monAn.getStatus().equals("Đang chuẩn bị") || monAn.getStatus().equals("Đang làm"))) {
                                // Lấy ban_id và id từ tài liệu
                                String banId = document.getString("ban_id");
                                String id = document.getId(); // Lấy ID của tài liệu

                                // Ghi log thông tin
                                Log.d(TAG, "Lấy được ban_id: " + banId + ", id: " + id);

                                // Lưu ID của tài liệu vào đối tượng MonAn
                                monAn.setDocumentId(id);
                                Log.d("DOC","}}}}}}}}}}}}}}}}}}}}}"+monAn.getDocumentId());// Lưu ID của tài liệu
                                listMonAnDaChon.add(monAn); // Thêm vào danh sách món ăn đã chọn
                            }
                        }

                        // Sắp xếp danh sách theo thời gian
                        Collections.sort(listMonAnDaChon, (monAn1, monAn2) -> {
                            // Chuyển đổi thời gian sang định dạng có thể so sánh
                            return monAn1.getTime().compareTo(monAn2.getTime());
                        });

                        // Cập nhật adapter
                        if (chefAdapter == null) {
                            // Khởi tạo ChefAdapter với tableId đã lấy
                            chefAdapter = new ChefAdapter(listMonAnDaChon, firestore, tableId);
                            listChefs.setAdapter(chefAdapter);
                        } else {
                            chefAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        }
                    }
                });
    }

    private void Event() {
        listChefs = findViewById(R.id.listChefs);
        btnHistory = findViewById(R.id.btnHistory);
    }
}