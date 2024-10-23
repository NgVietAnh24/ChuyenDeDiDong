package vn.posicode.chuyende.DanhSachMonAn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import ImageButton
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import vn.posicode.chuyende.R;

public class FoodMenuActivity extends AppCompatActivity {

    private EditText searchFood;
    private Button btnAll, btnHotpot, btnGrill, btnDrinks;
    private LinearLayout foodListLayout;
    private TextView tableNameTextView;
    private TextView tableDescriptionTextView;
    private Button btnSelectedFood;

    // Danh sách giả lập các món ăn
    private List<Food> foodList;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ánh xạ các view
        tableNameTextView = findViewById(R.id.tableNameTextView);
        tableDescriptionTextView = findViewById(R.id.tableDescriptionTextView);


        // Nhận dữ liệu từ Intent
        String tableName = getIntent().getStringExtra("tableName");
        String tableDescription = getIntent().getStringExtra("tableDescription");

        if (tableName != null) {
            tableNameTextView.setText(tableName);
        }
        if (tableDescription != null) {
            tableDescriptionTextView.setText(tableDescription);
        }

        // Ánh xạ các view
        searchFood = findViewById(R.id.search_food);
        btnAll = findViewById(R.id.btn_all);
        btnHotpot = findViewById(R.id.btn_hotpot);
        btnGrill = findViewById(R.id.btn_grill);
        btnDrinks = findViewById(R.id.btn_drinks);
        foodListLayout = findViewById(R.id.food_list);

        // Khởi tạo danh sách món ăn
        foodList = new ArrayList<>();
        foodList.add(new Food("Hamburger", "$10", R.drawable.hamburgur, "All"));
        foodList.add(new Food("Lẩu bò", "$20", R.drawable.lau_bo, "Lẩu"));
        foodList.add(new Food("Gà nướng", "$15", R.drawable.ga_nuong, "Nướng"));
        foodList.add(new Food("Coca Cola", "$2", R.drawable.coca_cola, "Đồ uống"));
        foodList.add(new Food("Bia", "$5", R.drawable.bia, "Đồ uống"));

        // Khởi tạo nút "Món đã chọn"
        btnSelectedFood = findViewById(R.id.btn_selected_items);

        // Gán sự kiện click cho nút "Món đã chọn"
        btnSelectedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang SelectedFoodActivity
                Intent intent = new Intent(FoodMenuActivity.this, SelectedFoodActivity.class);
                startActivity(intent);
            }
        });

        // Hiển thị tất cả các món ăn
        displayFoodList(foodList);

        // Xử lý sự kiện khi nhấn vào nút "Tất cả"
        btnAll.setOnClickListener(v -> displayFoodList(foodList));

        // Xử lý sự kiện khi nhấn vào nút "Món Lẩu"
        btnHotpot.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Lẩu")));

        // Xử lý sự kiện khi nhấn vào nút "Món Nướng"
        btnGrill.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Nướng")));

        // Xử lý sự kiện khi nhấn vào nút "Đồ uống"
        btnDrinks.setOnClickListener(v -> displayFoodList(filterFoodByCategory("Đồ uống")));

        // Xử lý sự kiện khi nhấn vào nút "Đặt trước"
        Button btnReserve = findViewById(R.id.btn_reserve);
        btnReserve.setOnClickListener(v -> showReserveDialog());

        // Xử lý sự kiện cho nút quay lại
        ImageButton backButton = findViewById(R.id.backButton); // Ánh xạ nút quay lại
        backButton.setOnClickListener(v -> finish()); // Quay lại màn hình trước
    }

    // Hàm hiển thị danh sách món ăn
    private void displayFoodList(List<Food> foods) {
        foodListLayout.removeAllViews(); // Xóa danh sách món ăn cũ
        for (Food food : foods) {
            View foodView = getLayoutInflater().inflate(R.layout.food_item, null); // Tạo view cho từng món ăn

            ImageView foodImage = foodView.findViewById(R.id.food_image);
            TextView foodName = foodView.findViewById(R.id.food_name);
            TextView foodPrice = foodView.findViewById(R.id.food_price);

            foodImage.setImageResource(food.getImageResId());
            foodName.setText(food.getName());
            foodPrice.setText(food.getPrice());

            // Xử lý sự kiện khi nhấn vào món ăn
            foodView.setOnClickListener(v -> {
                // Xử lý đặt món ở đây
                Toast.makeText(this, "Đặt món: " + food.getName(), Toast.LENGTH_SHORT).show();
            });

            foodListLayout.addView(foodView); // Thêm món ăn vào layout
        }
    }

    // Hàm lọc món ăn theo thể loại
    private List<Food> filterFoodByCategory(String category) {
        List<Food> filteredList = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getCategory().equals(category)) {
                filteredList.add(food);
            }
        }
        return filteredList;
    }

    // Hàm hiển thị dialog đặt trước
    private void showReserveDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reserve); // Đặt layout cho dialog đặt trước

        // Ánh xạ các view trong dialog
        EditText editTextReservationName = dialog.findViewById(R.id.edit_text_reservation_name);
        EditText editTextReservationPhone = dialog.findViewById(R.id.edit_text_reservation_phone);
        Button buttonConfirm = dialog.findViewById(R.id.button_confirm);

        // Thiết lập kích thước dialog
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); // Hoặc bạn có thể đặt chiều cao cố định

        // Xử lý sự kiện khi nhấn nút xác nhận
        buttonConfirm.setOnClickListener(v -> {
            String name = editTextReservationName.getText().toString();
            String phone = editTextReservationPhone.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                // Lưu thông tin vào Firestore
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                Map<String, String> reservation = new HashMap<>();
                reservation.put("name", name);
                reservation.put("phone", phone);

                firestore.collection("reservations") // Tên collection trong Firestore
                        .add(reservation)
                        .addOnSuccessListener(documentReference -> {
                            // Đặt trước thành công
                            Toast.makeText(this, "Đặt trước thành công!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Đóng dialog
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý lỗi
                            Toast.makeText(this, "Lỗi khi đặt trước: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Thông báo cho người dùng rằng họ cần nhập thông tin
                Toast.makeText(this, "Vui lòng nhập tên và số điện thoại.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show(); // Hiển thị dialog
    }

}
