package vn.posicode.chuyende.DanhSachMonAn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;

public class FoodMenuActivity extends AppCompatActivity {

    private EditText searchFood;
    private Button btnAll, btnHotpot, btnGrill, btnDrinks;
    private LinearLayout foodListLayout;
    private TextView tableNameTextView;
    private TextView tableDescriptionTextView;
    private Button btnSelectedFood;

    private List<Food> foodList;
    private List<Food> selectedFoodList;

    private String reservationName = "";
    private String reservationPhone = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        tableNameTextView = findViewById(R.id.tableNameTextView);
        tableDescriptionTextView = findViewById(R.id.tableDescriptionTextView);

        Intent intent = getIntent();
        if (intent != null) {
            String tableName = intent.getStringExtra("tableName");
            String tableDescription = intent.getStringExtra("tableDescription");

            if (tableName != null) {
                tableNameTextView.setText(tableName);
            }
            if (tableDescription != null) {
                tableDescriptionTextView.setText(tableDescription);
            }
        }

        // Lấy thông tin đặt bàn từ Firestore
        String tableName = tableNameTextView.getText().toString();
        FirebaseFirestore.getInstance()
                .collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        reservationName = document.getString("reservationName");
                        reservationPhone = document.getString("reservationPhone");
                    }
                });

        searchFood = findViewById(R.id.search_food);
        btnAll = findViewById(R.id.btn_all);
        btnHotpot = findViewById(R.id.btn_hotpot);
        btnGrill = findViewById(R.id.btn_grill);
        btnDrinks = findViewById(R.id.btn_drinks);
        foodListLayout = findViewById(R.id.food_list);

        foodList = new ArrayList<>();
        selectedFoodList = new ArrayList<>();
        foodList.add(new Food("Hamburger", "$10", R.drawable.hamburgur, "All"));
        foodList.add(new Food("Lẩu bò", "$20", R.drawable.lau_bo, "Lẩu"));
        foodList.add(new Food("Gà nướng", "$15", R.drawable.ga_nuong, "Nướng"));
        foodList.add(new Food("Coca-Cola", "$2", R.drawable.coca_cola, "Drinks"));
        foodList.add(new Food("Bia", "$5", R.drawable.bia, "Drinks"));

        addFoodToFirestore();

        btnSelectedFood = findViewById(R.id.btn_selected_items);

        btnSelectedFood.setOnClickListener(v -> {
            String tableName1 = tableNameTextView.getText().toString();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("tables")
                    .whereEqualTo("name", tableName1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("status", "Đang sử dụng");

                            firestore.collection("tables")
                                    .document(documentId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Trạng thái bàn đã được cập nhật thành đang sử dụng");

                                        // Gửi broadcast để cập nhật UI
                                        Intent broadcastIntent = new Intent("UPDATE_TABLE_STATUS");
                                        broadcastIntent.putExtra("tableName", tableName1);
                                        broadcastIntent.putExtra("status", "Đang sử dụng");
                                        sendBroadcast(broadcastIntent);

                                        // Chuyển sang màn hình SelectedFoodActivity
                                        Intent intentSelectedFood = new Intent(FoodMenuActivity.this, SelectedFoodActivity.class);
                                        intentSelectedFood.putParcelableArrayListExtra("selectedFoodList", (ArrayList<? extends Parcelable>) selectedFoodList);
                                        intentSelectedFood.putExtra("tableName", tableName1);
                                        startActivity(intentSelectedFood);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Lỗi khi cập nhật trạng thái bàn", e);
                                        Toast.makeText(FoodMenuActivity.this,
                                                "Có lỗi xảy ra khi cập nhật trạng thái bàn",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tìm thông tin bàn",
                                Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error finding table", e);
                    });
        });

        displayFoodList(foodList);

        btnAll.setOnClickListener(v -> displayFoodList(foodList));
        btnHotpot.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Lẩu")));
        btnGrill.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Nướng")));
        btnDrinks.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Drinks")));

        Button btnReserve = findViewById(R.id.btn_reserve);
        btnReserve.setOnClickListener(v -> showReserveDialog());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void displayFoodList(List<Food> foods) {
        foodListLayout.removeAllViews();
        for (Food food : foods) {
            View foodView = getLayoutInflater().inflate(R.layout.food_item, null);

            ImageView foodImage = foodView.findViewById(R.id.sample_food_image);
            TextView foodName = foodView.findViewById(R.id.food_name);
            TextView foodPrice = foodView.findViewById(R.id.food_price);

            foodImage.setImageResource(food.getImageResId());
            foodName.setText(food.getName());
            foodPrice.setText(food.getPrice());

            foodView.setOnClickListener(v -> {
                selectedFoodList.add(food);
                Toast.makeText(this, "Đã thêm món " + food.getName() + " vào danh sách đã chọn", Toast.LENGTH_SHORT).show();
            });

            foodListLayout.addView(foodView);
        }
    }

    private List<Food> filterFoodByCategory(String category) {
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getCategory().equals(category)) {
                filteredList.add(food);
            }
        }
        return filteredList;
    }

    private void showReserveDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reserve);

        EditText editTextReservationName = dialog.findViewById(R.id.edit_text_reservation_name);
        EditText editTextReservationPhone = dialog.findViewById(R.id.edit_text_reservation_phone);
        Button buttonSave = dialog.findViewById(R.id.button_save);
        Button buttonCancel = dialog.findViewById(R.id.button_cancel);

        // Hiển thị thông tin đặt bàn hiện tại
        editTextReservationName.setText(reservationName);
        editTextReservationPhone.setText(reservationPhone);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        buttonSave.setOnClickListener(v -> {
            String name = editTextReservationName.getText().toString();
            String phone = editTextReservationPhone.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                updateTableStatus("Đã đặt", name, phone);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng nhập tên và số điện thoại.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> {
            updateTableStatus("Trống", "", "");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateTableStatus(String status, String name, String phone) {
        String tableName = tableNameTextView.getText().toString();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("status", status);
                        updates.put("reservationName", name);
                        updates.put("reservationPhone", phone);

                        firestore.collection("tables")
                                .document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Trạng thái bàn đã được cập nhật");
                                    String message = status.equals("Trống") ?
                                            "Đã hủy đặt bàn!" :
                                            "Đặt bàn thành công!";
                                    Toast.makeText(FoodMenuActivity.this, message,
                                            Toast.LENGTH_SHORT).show();

                                    // Cập nhật biến thành viên
                                    reservationName = name;
                                    reservationPhone = phone;

                                    Intent intent = new Intent("UPDATE_TABLE_STATUS");
                                    intent.putExtra("tableName", tableName);
                                    intent.putExtra("status", status);
                                    sendBroadcast(intent);

                                    // Không kết thúc activity ở đây
                                    // finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Lỗi khi cập nhật trạng thái bàn", e);
                                    Toast.makeText(FoodMenuActivity.this,
                                            "Có lỗi xảy ra khi thực hiện thao tác",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tìm thông tin bàn",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error finding table", e);
                });
    }

    private void addFoodToFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<Food> foodList = new ArrayList<>();
        foodList.add(new Food("Hamburger", "$10", R.drawable.hamburgur, "All"));
        foodList.add(new Food("Lẩu bò", "$20", R.drawable.lau_bo, "Lẩu"));
        foodList.add(new Food("Gà nướng", "$15", R.drawable.ga_nuong, "Nướng"));
        foodList.add(new Food("Coca-Cola", "$2", R.drawable.coca_cola, "Drinks"));
        foodList.add(new Food("Bia", "$5", R.drawable.bia, "Drinks"));

        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            Map<String, Object> foodData = new HashMap<>();

            foodData.put("ten_mon", food.getName());
            foodData.put("gia_mon", Double.parseDouble(food.getPrice().replace("$", "").trim()));
            foodData.put("image_mon", "url_to_image_" + food.getName().toLowerCase().replace(" ", "_"));
            foodData.put("danh_muc_id", i + 1);

            firestore.collection("monan").add(foodData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(" Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error adding document", e);
                    });
        }
    }
}