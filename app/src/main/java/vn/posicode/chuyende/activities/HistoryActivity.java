// HistoryActivity.java
package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.EventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DishHistory;
import vn.posicode.chuyende.adapter.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<DishHistory> dishHistoryList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lichsudaubep);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dishHistoryList = new ArrayList<>();

        // Thiết lập adapter với listener cho nhấn và nhấn giữ
        historyAdapter = new HistoryAdapter(dishHistoryList, this::onDishHistoryClick, this::onDishHistoryLongClick);
        recyclerView.setAdapter(historyAdapter);

        // Lấy dữ liệu từ Firestore
       // loadDataFromFirestore();
        loadHistoryData();
    }

//    private void loadDataFromFirestore() {
//        registration = db.collection("selected_foods")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
//                        if (e != null) {
//                            return; // Xử lý lỗi nếu có
//                        }
//
//                        // Xóa danh sách cũ
//                        dishHistoryList.clear();
//                        if (snapshots != null) {
//                            for (QueryDocumentSnapshot document : snapshots) {
//                                // Lấy dữ liệu từ Firestore
//                                String banId = document.getString("ban_id");
//                                int gia = document.getLong("gia").intValue();
//                                String hinhAnh = document.getString("hinh_anh");
//                                String monAnId = document.getString("mon_an_id");
//                                int soLuong = document.getLong("so_luong").intValue();
//                                String tenMonAn = document.getString("ten_mon_an");
//                                String trangThai = document.getString("trang_thai");
//                                String time = document.getString("time");
//
//                                // Thêm vào danh sách
//                                dishHistoryList.add(new DishHistory(banId, gia, hinhAnh, monAnId, soLuong, tenMonAn, trangThai, time));
//                            }
//                        }
//                        // Thông báo adapter cập nhật dữ liệu
//                        historyAdapter.notifyDataSetChanged();
//                    }
//                });
//    }



    private void loadHistoryData() {
        db.collection("dish_history")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            DishHistory history = document.toObject(DishHistory.class);
                            if (history != null) {
                                Object timeValue = document.get("time");
                                if (timeValue instanceof Long) {
                                    // Chuyển đổi từ Long thành định dạng HH:mm cho hiển thị
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String formattedTime = dateFormat.format(new Date((Long) timeValue));
                                    history.setTime(formattedTime); // Lưu lại thời gian dưới dạng chuỗi
                                }
                                dishHistoryList.add(history);
                            }
                        }
                        historyAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("HistoryActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void onDishHistoryClick(DishHistory dishHistory) {
        // Xử lý sự kiện nhấn vào hóa đơn (nếu cần)
    }

    private void onDishHistoryLongClick(DishHistory dishHistory) {
        // Hiện thông báo xác nhận xóa
        new AlertDialog.Builder(this)
                .setTitle("Xóa hóa đơn")
                .setMessage("Bạn có chắc chắn muốn xóa hóa đơn này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    deleteDishHistory(dishHistory);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void deleteDishHistory(DishHistory dishHistory) {
        db.collection("selected_foods")
                .whereEqualTo("mon_an_id", dishHistory.getMon_an_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Hóa đơn đã được xóa", Toast.LENGTH_SHORT).show();
                                        // Cập nhật danh sách sau khi xóa
                                        dishHistoryList.remove(dishHistory);
                                        historyAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi khi xóa hóa đơn", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tìm hóa đơn", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy đăng ký lắng nghe khi activity không còn hiển thị
        if (registration != null) {
            registration.remove();
        }
    }
}
