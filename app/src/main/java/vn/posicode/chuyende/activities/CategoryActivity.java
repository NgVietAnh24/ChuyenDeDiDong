package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class CategoryActivity extends AppCompatActivity {

    private EditText editTextCategoryName;
    private Button buttonSave, buttonEdit;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category); // Đảm bảo rằng bạn đã tạo tệp layout activity_category.xml

        // Khởi tạo các biến
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonEdit = findViewById(R.id.buttonEdit);
        backButton = findViewById(R.id.backButton);

        // Thiết lập sự kiện cho nút Lưu
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editTextCategoryName.getText().toString().trim();

                if (!categoryName.isEmpty()) {
                    // Xử lý lưu danh mục (lưu vào cơ sở dữ liệu hoặc danh sách)
                    // Ví dụ: Lưu vào Firestore hoặc ArrayList
                    Toast.makeText(CategoryActivity.this, "Đã lưu danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                    editTextCategoryName.setText(""); // Xóa ô nhập
                } else {
                    Toast.makeText(CategoryActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện cho nút Sửa
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editTextCategoryName.getText().toString().trim();

                if (!categoryName.isEmpty()) {
                    // Xử lý sửa danh mục (cập nhật vào cơ sở dữ liệu hoặc danh sách)
                    // Ví dụ: Cập nhật vào Firestore hoặc danh sách đã có
                    Toast.makeText(CategoryActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoryActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện cho nút quay lại
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
