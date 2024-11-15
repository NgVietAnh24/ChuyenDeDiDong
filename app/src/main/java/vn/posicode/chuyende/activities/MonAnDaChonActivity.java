package vn.posicode.chuyende.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.SelectedFood;
import vn.posicode.chuyende.adapter.SelectedFoodAdapter;

public class MonAnDaChonActivity extends AppCompatActivity implements SelectedFoodAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SelectedFoodAdapter adapter;
    private List<SelectedFood> foodList;
    private FirebaseFirestore db;
    private Button btnLuuTatCa;  // Thêm nút "Làm tất cả"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsmdachon);

        recyclerView = findViewById(R.id.recyclerViewMon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new SelectedFoodAdapter(this, foodList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        btnLuuTatCa = findViewById(R.id.btnMakeAll); // Tham chiếu đến nút "Làm tất cả"
        // Get data from Firestore
        db.collection("selected_foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            SelectedFood food = document.toObject(SelectedFood.class);
                            foodList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
        // Thiết lập sự kiện cho nút "Làm tất cả"
        btnLuuTatCa.setOnClickListener(v -> handleLamTatCa());
    }

    private void handleLamTatCa() {
        if (foodList.isEmpty()) {
            Toast.makeText(this, "Không có món ăn nào để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalItems = foodList.size();
        int[] completedCount = {0}; // Biến đếm số lần lưu thành công
        btnLuuTatCa.setEnabled(false); // Vô hiệu hóa nút

        // Hiển thị ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        for (int i = 0; i < totalItems; i++) {
            SelectedFood food = foodList.get(i);

            // Lưu trạng thái của món ăn vào Firestore
            db.collection("selected_foods")
                    .document(food.getMon_an_id())
                    .set(food)
                    .addOnSuccessListener(aVoid -> {
                                completedCount[0]++;

                                // Nếu tất cả món ăn đều đã được xử lý thành công
                                if (completedCount[0] == totalItems) {
                                    progressDialog.dismiss(); // Đóng ProgressDialog
                                    progressDialog.dismiss(); // Đóng ProgressDialog
                                    Toast.makeText(MonAnDaChonActivity.this, "Tất cả trạng thái món ăn đã được lưu lại", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MonAnDaChonActivity.this, DauBepFoodActivity.class);
                                    startActivity(intent);
                                    finish();  // Kết thúc MonAnDaChonActivity
                                }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss(); // Đóng ProgressDialog
                        Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi lưu trạng thái món ăn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnLuuTatCa.setEnabled(true); // Kích hoạt lại nút
                    });
        }

    }



    // Implement các phương thức xử lý sự kiện từ Adapter
    @Override
    public void onDeleteClick(int position) {
        SelectedFood food = foodList.get(position);
        db.collection("selected_foods")
                .document(food.getMon_an_id())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    foodList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(MonAnDaChonActivity.this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi xóa: ", e);
                    Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onChooseClick(int position) {
        SelectedFood food = foodList.get(position);
        food.setTrang_thai("Đang làm");
        db.collection("selected_foods")
                .document(food.getMon_an_id())
                .set(food)
                .addOnSuccessListener(aVoid -> {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(MonAnDaChonActivity.this, "Món ăn đang được làm", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi chọn làm", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onTakeClick(int position) {
        SelectedFood food = foodList.get(position);
        food.setTrang_thai("Đã lấy");
        db.collection("selected_foods")
                .document(food.getMon_an_id())
                .set(food)
                .addOnSuccessListener(aVoid -> {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(MonAnDaChonActivity.this, "Món ăn đã được lấy", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi lấy món ăn", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onIncreaseClick(int position) {
        SelectedFood food = foodList.get(position);
        food.setSo_luong(food.getSo_luong() + 1);
        db.collection("selected_foods")
                .document(food.getMon_an_id())
                .set(food)
                .addOnSuccessListener(aVoid -> {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(MonAnDaChonActivity.this, "Số lượng đã tăng", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi tăng số lượng", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDecreaseClick(int position) {
        SelectedFood food = foodList.get(position);
        if (food.getSo_luong() > 1) {
            food.setSo_luong(food.getSo_luong() - 1);
            db.collection("selected_foods")
                    .document(food.getMon_an_id())
                    .set(food)
                    .addOnSuccessListener(aVoid -> {
                        adapter.notifyItemChanged(position);
                        Toast.makeText(MonAnDaChonActivity.this, "Số lượng đã giảm", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(MonAnDaChonActivity.this, "Lỗi khi giảm số lượng", Toast.LENGTH_SHORT).show());
        }
    }
}
