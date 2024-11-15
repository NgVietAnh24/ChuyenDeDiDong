package vn.posicode.chuyende.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private Map<String, Integer> monDaChon = new HashMap<>();  // Khai báo để lưu món đã chọn với số lượng
    private int tongMonDaChon = 0;  // Tổng số món đã chọn
    private String selectedFoodId;
    private Context context; // Thêm biến Context

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
        loadDataFromFirebase();  // Phương thức để tải dữ liệu từ Firestore

        adapter = new DauBepFoodAdapter(daubepfoodlist, this);
        recyclerView.setAdapter(adapter);


        // Khởi tạo ImageButton
        ImageButton btnShowToolDaubep = findViewById(R.id.btnShowTool_daubep);

        // Thiết lập sự kiện click
        btnShowToolDaubep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển đến MonAnDaChon
                Intent intent = new Intent(DauBepFoodActivity.this, MonAnDaChonActivity.class);
                startActivity(intent);
            }
        });

    }

    public void setSelectedFoodId(String foodId) {
        this.selectedFoodId = foodId;
    }

    private void loadDataFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn collection "selected_foods"
        db.collection("selected_foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            DauBep_Food food = document.toObject(DauBep_Food.class);
                            daubepfoodlist.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("DauBepFoodActivity", "Error getting documents: ", task.getException());
                    }
                });
    }


    public void updateFoodStatus(String foodId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("selected_foods").document(foodId)
                .update("trang_thai", status)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật thành công
                    Toast.makeText(DauBepFoodActivity.this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    Toast.makeText(DauBepFoodActivity.this, "Cập nhật trạng thái thất bại", Toast.LENGTH_SHORT).show();
                });
    }

//    public void showQuantityDialog(String foodId) {
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//        builder.setCancelable(true);
//
//        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
//        TextView btnDecrease = dialogView.findViewById(R.id.btn_decrease);
//        TextView btnIncrease = dialogView.findViewById(R.id.btn_increase);
//
//        // Lấy số lượng món ăn đã chọn từ `monDaChon`, mặc định là 1 nếu chưa có trong danh sách
//        final int[] quantity = {monDaChon.getOrDefault(foodId, 1)};
//        tvQuantity.setText(String.valueOf(quantity[0]));
//
//        btnDecrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (quantity[0] > 1) {  // Giới hạn không cho giảm dưới 1
//                    quantity[0]--;
//                    tvQuantity.setText(String.valueOf(quantity[0]));
//                }
//            }
//        });
//
//        btnIncrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                quantity[0]++;
//                tvQuantity.setText(String.valueOf(quantity[0]));
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if (quantity[0] == 0) {
//                    monDaChon.remove(foodId);
//                } else {
//                    monDaChon.put(foodId, quantity[0]);
//                }
//
//                // Cập nhật tổng số lượng món đã chọn
//                tongMonDaChon = 0;
//                for (Integer quantity : monDaChon.values()) {
//                    tongMonDaChon += quantity;
//                }
//
//
//            }
//        });
//    }




//    public void showQuantityDialog(String foodId) {
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//        builder.setCancelable(true);
//
//        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
//        TextView btnDecrease = dialogView.findViewById(R.id.btn_decrease);
//        TextView btnIncrease = dialogView.findViewById(R.id.btn_increase);
//        Button btnDone = dialogView.findViewById(R.id.btn_done); // Nút "Đã xong"
//
//        // Lấy số lượng món ăn đã chọn từ `monDaChon`, mặc định là 1 nếu chưa có trong danh sách
//        final int[] quantity = {monDaChon.getOrDefault(foodId, 1)};
//        tvQuantity.setText(String.valueOf(quantity[0]));
//
//        btnDecrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (quantity[0] > 1) {  // Giới hạn không cho giảm dưới 1
//                    quantity[0]--;
//                    tvQuantity.setText(String.valueOf(quantity[0]));
//                }
//            }
//        });
//
//        btnIncrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                quantity[0]++;
//                tvQuantity.setText(String.valueOf(quantity[0]));
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if (quantity[0] == 0) {
//                    monDaChon.remove(foodId);
//                } else {
//                    monDaChon.put(foodId, quantity[0]);
//                }
//
//                // Cập nhật tổng số lượng món đã chọn
//                tongMonDaChon = 0;
//                for (Integer qty : monDaChon.values()) {
//                    tongMonDaChon += qty;
//                }
//            }
//        });
//
//        // Xử lý sự kiện khi nút "Đã xong" được nhấn
//        btnDone.setOnClickListener(v -> {
//            // Lấy số lượng món ăn từ danh sách
//            int totalQuantityInList = monDaChon.getOrDefault(foodId, 0);
//            int remainingQuantity = totalQuantityInList - quantity[0];
//
//            // Cập nhật trạng thái món ăn trong Firestore
//            updateFoodStatus(foodId, "Đã làm");
//
//            if (remainingQuantity > 0) {
//                // Nếu còn món chưa làm
//                Toast.makeText(this, "Còn " + remainingQuantity + " món chưa làm.", Toast.LENGTH_SHORT).show();
//            } else {
//                // Nếu đã hoàn thành tất cả
//                Toast.makeText(this, "Tất cả món đã được làm xong!", Toast.LENGTH_SHORT).show();
//                // Chuyển đến màn hình lịch sử đầu bếp (cần implement phương thức chuyển màn hình)
//                goToHistoryScreen();
//            }
//
//            dialog.dismiss(); // Đóng dialog sau khi nhấn nút "Đã xong"
//        });
//    }



    public void showQuantityDialog(String foodId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);

        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        TextView btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        TextView btnIncrease = dialogView.findViewById(R.id.btn_increase);
        Button btnDone = dialogView.findViewById(R.id.btn_done); // Nút "Đã xong"

        // Lấy số lượng món ăn đã chọn từ `monDaChon`, mặc định là 1 nếu chưa có trong danh sách
        final int[] quantity = {monDaChon.getOrDefault(foodId, 1)};
        tvQuantity.setText(String.valueOf(quantity[0]));

        btnDecrease.setOnClickListener(view -> {
            if (quantity[0] > 1) {  // Giới hạn không cho giảm dưới 1
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

        dialog.setOnDismissListener(dialogInterface -> {
            if (quantity[0] == 0) {
                monDaChon.remove(foodId);
            } else {
                monDaChon.put(foodId, quantity[0]);
            }

            // Cập nhật tổng số lượng món đã chọn
            tongMonDaChon = 0;
            for (Integer qty : monDaChon.values()) {
                tongMonDaChon += qty;
            }
        });

        // Xử lý sự kiện khi nút "Đã xong" được nhấn
//        btnDone.setOnClickListener(v -> {
//            // Lấy số lượng món ăn từ danh sách
//            int totalQuantityInList = monDaChon.getOrDefault(foodId, 0);
//            int remainingQuantity = totalQuantityInList - quantity[0];
//
//            // Cập nhật trạng thái món ăn trong Firestore
//            updateFoodStatus(foodId, "Đã làm");
//
//            // Lưu món ăn vào lịch sử
//            saveDishToHistory(foodId, quantity[0]);
//
//            if (remainingQuantity > 0) {
//                // Nếu còn món chưa làm
//                Toast.makeText(this, "Còn " + remainingQuantity + " món chưa làm.", Toast.LENGTH_SHORT).show();
//            } else {
//                // Nếu đã hoàn thành tất cả
//                Toast.makeText(this, "Tất cả món đã được làm xong!", Toast.LENGTH_SHORT).show();
//                // Chuyển đến màn hình lịch sử đầu bếp
//                goToHistoryScreen();
//            }
//
//            dialog.dismiss(); // Đóng dialog sau khi nhấn nút "Đã xong"
//        });
        btnDone.setOnClickListener(v -> {
            // Lấy số lượng món ăn từ danh sách
            int totalQuantityInList = monDaChon.getOrDefault(foodId, 0);
            int remainingQuantity = totalQuantityInList - quantity[0];

            Log.d("DauBepFoodActivity", "Total Quantity in List: " + totalQuantityInList);
            Log.d("DauBepFoodActivity", "Quantity Selected: " + quantity[0]);
            Log.d("DauBepFoodActivity", "Remaining Quantity: " + remainingQuantity);

            // Cập nhật trạng thái món ăn trong Firestore
            updateFoodStatus(foodId, "Đã làm");

            // Lưu món ăn vào lịch sử
            saveDishToHistory(foodId, quantity[0]);

            if (remainingQuantity > 0) {
                // Nếu còn món chưa làm
                Toast.makeText(this, "Còn " + remainingQuantity + " món chưa làm.", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu đã hoàn thành tất cả
                Toast.makeText(this, "Tất cả món đã được làm xong!", Toast.LENGTH_SHORT).show();
                // Chuyển đến màn hình lịch sử đầu bếp
                goToHistoryScreen();
            }

            dialog.dismiss(); // Đóng dialog sau khi nhấn nút "Đã xong"
        });

    }

    private void saveDishToHistory(String foodId, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo một đối tượng để lưu vào Firestore
        Map<String, Object> dishData = new HashMap<>();
        dishData.put("mon_an_id", foodId);
        dishData.put("so_luong", quantity);
        dishData.put("trang_thai", "Đã làm");
        dishData.put("time", System.currentTimeMillis()); // Lưu thời gian hiện tại

        // Lưu vào collection "dish_history"
        db.collection("dish_history")
                .add(dishData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Món ăn đã được lưu vào lịch sử", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi lưu món ăn vào lịch sử: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void goToHistoryScreen() {
        // Implement logic để chuyển đến màn hình lịch sử đầu bếp
         Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
