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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DishHistory;
import vn.posicode.chuyende.adapter.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<DishHistory> dishHistoryList;
    private FirebaseFirestore db;

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
        loadHistoryData();
    }

    private void loadHistoryData() {
        db.collection("dish_history")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            DishHistory history = document.toObject(DishHistory.class);
                            if (history != null) {
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
        // Có thể hiển thị thông tin chi tiết hoặc thực hiện hành động khác
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
        // Xóa tài liệu từ collection "dish_history"
        db.collection("dish_history").document(dishHistory.getMon_an_id()) // Sử dụng ID của món ăn để xóa
                .delete()
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

    @Override
    protected void onStop() {
        super.onStop();
        // Hủy đăng ký lắng nghe khi activity không còn hiển thị
    }
}