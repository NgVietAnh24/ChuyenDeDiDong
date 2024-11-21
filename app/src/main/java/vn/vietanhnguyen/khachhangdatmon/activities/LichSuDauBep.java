package vn.vietanhnguyen.khachhangdatmon.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class LichSuDauBep extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private List<MonAn> listMonAnDaChon;
    private List<MonAn> filteredMonAnList; // Danh sách đã lọc

    private ChefAdapter chefAdapter;
    private RecyclerView listChefs;
    private ImageButton btnback;
    private EditText edtSearch;
    private String tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_dau_bep);
        Event();

        // Khởi tạo các biến
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        listMonAnDaChon = new ArrayList<>();
        filteredMonAnList = new ArrayList<>(); // Khởi tạo danh sách đã lọc

        listChefs.setLayoutManager(new LinearLayoutManager(this));

        // Lấy giá trị ban_id từ Firestore
        layTableId();

        btnback.setOnClickListener(view -> finish());

        // Thêm TextWatcher cho EditText tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void layTableId() {
        firestore.collection("selectedFoods").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    if (document.exists()) {
                        tableId = document.getString("ban_id");
                        Log.d(TAG, "Lấy được tableId: " + tableId);
                        layDanhSachMonDaChon();
                        break;
                    }
                }
            } else {
                Log.e(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
            }
        });
    }

    private void layDanhSachMonDaChon() {
        firestore.collection("selectedFoods").addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w(TAG, "Lỗi khi lắng nghe thay đổi: ", e);
                return;
            }

            if (value != null) {
                listMonAnDaChon.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    MonAn monAn = document.toObject(MonAn.class);
                    if (monAn != null && monAn.getStatus().equals("Đã xong")) {
                        String banId = document.getString("ban_id");
                        String id = document.getId();
                        Log.d(TAG, "Lấy được ban_id: " + banId + ", id: " + id);
                        monAn.setDocumentId(id);
                        listMonAnDaChon.add(monAn);
                    }
                }

                // Sắp xếp danh sách theo thời gian
                Collections.sort(listMonAnDaChon, (monAn1, monAn2) -> {
                    return monAn1.getTime().compareTo(monAn2.getTime());
                });

                // Cập nhật adapter
                filteredMonAnList.clear();
                filteredMonAnList.addAll(listMonAnDaChon); // Khởi tạo danh sách đã lọc
                if (chefAdapter == null) {
                    chefAdapter = new ChefAdapter(filteredMonAnList, firestore, tableId);
                    listChefs.setAdapter(chefAdapter);
                } else {
                    chefAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void filter(String text) {
        filteredMonAnList.clear();
        if (text.isEmpty()) {
            filteredMonAnList.addAll(listMonAnDaChon); // Nếu không có gì để tìm kiếm, hiển thị tất cả
        } else {
            for (MonAn monAn : listMonAnDaChon) {
                // Kiểm tra tên món ăn và thời gian
                if (monAn.getName().toLowerCase().contains(text.toLowerCase()) || monAn.getTime().toLowerCase().contains(text.toLowerCase())) {
                    filteredMonAnList.add(monAn);
                }
            }
        }
        chefAdapter.notifyDataSetChanged();
    }

    private void Event() {
        listChefs = findViewById(R.id.listChefHistory);
        btnback = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearchH);
    }
}