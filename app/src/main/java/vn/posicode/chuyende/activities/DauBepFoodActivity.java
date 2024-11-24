package vn.posicode.chuyende.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DauBepFoodAdapter;
import vn.posicode.chuyende.adapter.DauBep_Food;

public class DauBepFoodActivity extends AppCompatActivity {




    private RecyclerView recyclerView;
    private DauBepFoodAdapter adapter;
    private List<DauBep_Food> daubepfoodlist;
    private Map<String, Integer> monDaChon = new HashMap<>();
    private int tongMonDaChon = 0;
    private String selectedFoodId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homedaubep);

        recyclerView = findViewById(R.id.listDauBep);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (daubepfoodlist == null) {
            daubepfoodlist = new ArrayList<>();
        }
        loadDataFromFirebase();

        adapter = new DauBepFoodAdapter(daubepfoodlist, this);
        recyclerView.setAdapter(adapter);

        ImageButton btnShowToolDaubep = findViewById(R.id.btnShowTool_daubep);
        btnShowToolDaubep.setOnClickListener(v -> {
            Intent intent = new Intent(DauBepFoodActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        luuMonAnDauBep();

    }


    private void loadDataFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("foodChefs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        daubepfoodlist.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            DauBep_Food food = document.toObject(DauBep_Food.class);
                            if (food != null) daubepfoodlist.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("DauBepFoodActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void showQuantityDialog(String foodId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);

        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);
        Button btnDone = dialogView.findViewById(R.id.btn_done);

        int initialQuantity = monDaChon.getOrDefault(foodId, 1);
        final int[] quantity = {initialQuantity};
        tvQuantity.setText(String.valueOf(quantity[0]));

        btnDecrease.setOnClickListener(view -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnIncrease.setOnClickListener(view -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnDone.setOnClickListener(v -> {
            int remainingQuantity = monDaChon.getOrDefault(foodId, 0) - quantity[0];

            if (quantity[0] > monDaChon.getOrDefault(foodId, 0)) {
                Toast.makeText(this, "Số lượng vượt quá món đã chọn!", Toast.LENGTH_SHORT).show();
                btnDone.setEnabled(false);
            } else {
                btnDone.setEnabled(true);

                updateFoodStatus(foodId, "Đã làm");
                saveDishToHistory(foodId, quantity[0]);

                if (remainingQuantity > 0) {
                    Toast.makeText(this, "Còn " + remainingQuantity + " món chưa làm.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Tất cả món đã làm xong!", Toast.LENGTH_SHORT).show();
                    goToHistoryScreen();
                }
                dialog.dismiss();
            }
        });
    }

    private void updateFoodStatus(String foodId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("foodChefs").document(foodId)
                .update("trang_thai", status)
                .addOnSuccessListener(aVoid -> Toast.makeText(DauBepFoodActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DauBepFoodActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show());
    }

    private void saveDishToHistory(String foodId, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> dishData = new HashMap<>();
        dishData.put("mon_an_id", foodId);
        dishData.put("so_luong", quantity);
        dishData.put("trang_thai", "Đã làm");
        dishData.put("time", System.currentTimeMillis());

        db.collection("dish_history")
                .add(dishData)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Đã lưu vào lịch sử", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lưu lịch sử", Toast.LENGTH_SHORT).show());
    }

    private void luuMonAnDauBep() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", String.valueOf(System.currentTimeMillis()));
        foodData.put("name", "Lẩu cua");
        foodData.put("price", "123000 VND");
        foodData.put("soLuong", 1);
        foodData.put("time", new SimpleDateFormat("HH:mm").format(new Date()));
        foodData.put("image", "https://firebasestorage.googleapis.com/v0/...");

        db.collection("foodChefs")
                .add(foodData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("DauBepFoodActivity", "Thêm món ăn thành công!");
                    else Log.e("DauBepFoodActivity", "Lỗi khi thêm món ăn!");
                });
    }

    private void goToHistoryScreen() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
