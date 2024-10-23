package vn.posicode.chuyende.DanhSachMonAn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class SelectedFoodActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgFood;
    private TextView tvFoodName, tvFoodPrice, tvStatus;
    private Button btnChooseToMake, btnTaken, btnCancelOrder, btnPay;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);

        // Initialize Views
        btnBack = findViewById(R.id.backButton);
        imgFood = findViewById(R.id.imgFood);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvFoodPrice = findViewById(R.id.tvFoodPrice);
        tvStatus = findViewById(R.id.tvStatus);
        btnChooseToMake = findViewById(R.id.btnChooseToMake);
        btnTaken = findViewById(R.id.btnTaken);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnPay = findViewById(R.id.btnPay);
        etNote = findViewById(R.id.etNote);

        // Set food details (example data)
        tvFoodName.setText("Nước dừa");
        tvFoodPrice.setText("$10");
        tvStatus.setText("Đang chuẩn bị");

        // Handle Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close activity
            }
        });

        // Choose to make button action
        btnChooseToMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("Đang làm");
                Toast.makeText(SelectedFoodActivity.this, "Chọn làm món!", Toast.LENGTH_SHORT).show();
            }
        });

        // Mark as taken button action
        btnTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("Đã lấy");
                Toast.makeText(SelectedFoodActivity.this, "Món đã lấy!", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel order button action
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectedFoodActivity.this, "Hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                // Implement order cancellation logic here
                finish();
            }
        });

        // Pay button action
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = etNote.getText().toString();
                Toast.makeText(SelectedFoodActivity.this, "Thanh toán đơn hàng!", Toast.LENGTH_SHORT).show();
                // Implement payment logic here
            }
        });
    }
}
