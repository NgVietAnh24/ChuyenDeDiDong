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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DauBepFoodAdapter;
import vn.posicode.chuyende.adapter.DauBep_Food;
import vn.posicode.chuyende.adapter.Food;

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

        luuMonAnDauBep();
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
                Intent intent = new Intent(DauBepFoodActivity.this, HistoryActivity.class);
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
        db.collection("foodChefs")
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
        db.collection("foodChefs").document(foodId)
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
//            // Cập nhật lại số lượng đã làm từ danh sách hiện tại
//            int totalQuantityInList = monDaChon.getOrDefault(foodId, 0);
//            int remainingQuantity = totalQuantityInList - quantity[0];
//
//            if (remainingQuantity < 0) {
//                remainingQuantity = 0; // Giới hạn không cho số âm
//            }
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
//            } else if (remainingQuantity == 0) {
//                // Nếu đã hoàn thành tất cả
//                Toast.makeText(this, "Tất cả món đã được làm xong!", Toast.LENGTH_SHORT).show();
//                // Chuyển đến màn hình lịch sử đầu bếp
//                goToHistoryScreen();
//            }
//
//            dialog.dismiss(); // Đóng dialog sau khi nhấn nút "Đã xong"
//        });




        btnDone.setOnClickListener(v -> {
            int totalQuantityInList = monDaChon.getOrDefault(foodId, 0); // Lấy số lượng đã chọn ban đầu
            int remainingQuantity = totalQuantityInList - quantity[0]; // Số lượng còn lại

            if (quantity[0] < totalQuantityInList) {
                // Nếu số lượng nhập chưa đủ
                Toast.makeText(this, "Còn " + remainingQuantity + " món chưa làm.", Toast.LENGTH_SHORT).show();
                btnDone.setEnabled(false); // Làm mờ nút nếu số lượng không đủ
            } else {
                // Nếu đủ số lượng, thực hiện cập nhật
                btnDone.setEnabled(true); // Bật lại nút "Đã xong" khi đủ số lượng

                // Cập nhật trạng thái món ăn trong Firestore
                updateFoodStatus(foodId, "Đã làm");

                // Lưu món ăn vào lịch sử
                saveDishToHistory(foodId, quantity[0]);

                // Kiểm tra nếu đã hoàn thành tất cả
                if (remainingQuantity == 0) {
                    Toast.makeText(this, "Tất cả món đã được làm xong!", Toast.LENGTH_SHORT).show();
                    goToHistoryScreen(); // Chuyển đến màn hình lịch sử đầu bếp
                }

                dialog.dismiss(); // Đóng dialog khi hoàn thành
            }
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

    private void luuMonAnDauBep() {
//        Map<String, Object> foodData = new HashMap<>();
//        foodData.put("id", food.getId());
//        foodData.put("name", food.getName());
//        foodData.put("price", food.getPrice());
//        foodData.put("soLuong", 1);
//        foodData.put("image", food.getImage());
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(now);

        String fId= String.valueOf(System.currentTimeMillis());

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("id", fId);
        foodData.put("name", "Lau cua");
        foodData.put("price", "123000VND");
        foodData.put("soLuong", 1);
        foodData.put("time", time);

        foodData.put("image", "https://firebasestorage.googleapis.com/v0/b/vietanh-38a14.appspot.com/o/Food%2FZZMRinyJerpGcRW6zXfE.jpg?alt=media&token=69882f5b-ebec-464f-9eed-a95ccf222334"
                );


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("foodChefs").add(foodData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d("ThanhCong","Fall");
                    }
                });
    }

//    private void luuMonAnDauBep() {
//        // Danh sách các món ăn để thêm vào Firestore
//        List<DauBep_Food> foodList = new ArrayList<>();
//        foodList.add(new DauBep_Food("1", "Lau cua", 123000, "https://firebasestorage.googleapis.com/v0/b/vietanh-38a14.appspot.com/o/Food%2FZZMRinyJerpGcRW6zXfE.jpg?alt=media&token=69882f5b-ebec-464f-9eed-a95ccf222334"));
//        foodList.add(new Food("2", "Pho Bo", 50000, "https://example.com/image_pho_bo.jpg"));
//        foodList.add(new Food("3", "Com tam", 30000, "https://example.com/image_com_tam.jpg"));
//        // Thêm nhiều món ăn khác vào danh sách...
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        for (DauBep_Food food : foodList) {
//            Map<String, Object> foodData = new HashMap<>();
//            foodData.put("id", food.getId());
//            foodData.put("name", food.getName());
//            foodData.put("price", food.getPrice() + " VND"); // Đảm bảo giá là chuỗi
//            foodData.put("soLuong", 1);
//            foodData.put("time", new SimpleDateFormat("HH:mm").format(new Date()));
//            foodData.put("image", food.getImage());
//
//            // Thêm món ăn vào Firestore
//            db.collection("foodChefs").add(foodData)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Log.d("ThanhCong", "Món ăn đã được thêm thành công: " + food.getName());
//                        } else {
//                            Log.d("ThatBai", "Lỗi khi thêm món ăn: ", task.getException());
//                        }
//                    });
//        }
//    }

    private void goToHistoryScreen() {
        // Implement logic để chuyển đến màn hình lịch sử đầu bếp
         Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
