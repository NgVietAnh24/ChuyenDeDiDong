package vn.posicode.chuyende.DanhSachMonAn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import vn.posicode.chuyende.R;

public class SelectedFoodActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout selectedFoodLayout; // Thay đổi từ ImageView và TextView sang LinearLayout
    private Button btnCancelOrder, btnPay;
    private EditText etNote;

    private ArrayList<Food> selectedFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);

        // Khởi tạo các view
        btnBack = findViewById(R.id.btnQuayLai);
        selectedFoodLayout = findViewById(R.id.selected_food_list); // Thay đổi từ imgFood, tvFoodName, tvFoodPrice sang selectedFoodLayout
        btnCancelOrder = findViewById(R.id.btnHuyDon);
        btnPay = findViewById(R.id.btnThanhToan);
        etNote = findViewById(R.id.etGhiChu);

        // Gán sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nhận danh sách món đã chọn từ Intent
        selectedFoodList = getIntent().getParcelableArrayListExtra("selectedFoodList");

        // Hiển thị danh sách món đã chọn
        if (selectedFoodList != null) {
            for (Food food : selectedFoodList) {
                addFoodView(food); // Thêm món ăn vào layout
            }
        }

        // Gán sự kiện cho nút hủy đơn
        btnCancelOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        });

        // Gán sự kiện cho nút thanh toán
        btnPay.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thanh toán", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình trước
        });
    }

    // Hàm thêm món ăn vào layout
    private void addFoodView(Food food) {
        View foodView = LayoutInflater.from(this).inflate(R.layout.selected_food_item, selectedFoodLayout, false); // Inflate layout cho món ăn

        ImageView imgFood = foodView.findViewById(R.id.imgMonAn);
        TextView tvFoodName = foodView.findViewById(R.id.tvTenMonAn);
        TextView tvFoodPrice = foodView.findViewById(R.id.tvGiaMonAn);
        TextView tvStatus = foodView.findViewById(R.id.tvTrangThai);
        TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);

        imgFood.setImageResource(food.getImageResId());
        tvFoodName.setText(food.getName());
        tvFoodPrice.setText(food.getPrice());
        tvStatus.setText("Chưa được đặt."); // Trạng thái mặc định
        tvQuantity.setText("1"); // Số lượng mặc định

        // Gán sự kiện cho nút chọn làm món
        foodView.findViewById(R.id.btnChonLam).setOnClickListener(v -> {
            tvStatus.setText("Đã chọn làm."); // Cập nhật trạng thái
            Toast.makeText(this, "Đã chọn làm món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        // Gán sự kiện cho nút đã lấy món
        foodView.findViewById(R.id.btnDaLay).setOnClickListener(v -> {
            tvStatus.setText("Đã lấy."); // Cập nhật trạng thái
            Toast.makeText(this, "Đã lấy món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        // Gán sự kiện cho nút xóa món
        foodView.findViewById(R.id.btnXoa).setOnClickListener(v -> {
            Toast.makeText (this, "Đã xóa món " + food.getName(), Toast.LENGTH_SHORT).show();
            selectedFoodLayout.removeView(foodView); // Xóa món ăn khỏi layout
        });

        // Gán sự kiện cho nút tăng số lượng
        foodView.findViewById(R.id.btnTang).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString()) + 1;
            tvQuantity.setText(String.valueOf(quantity)); // Cập nhật số lượng
        });

        // Gán sự kiện cho nút giảm số lượng
        foodView.findViewById(R.id.btnGiam).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 1) { // Không giảm xuống dưới 1
                quantity--;
                tvQuantity.setText(String.valueOf(quantity)); // Cập nhật số lượng
            }
        });

        selectedFoodLayout.addView(foodView); // Thêm món ăn vào layout
    }
}