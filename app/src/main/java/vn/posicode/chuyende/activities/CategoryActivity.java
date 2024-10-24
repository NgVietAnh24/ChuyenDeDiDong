package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.CategoryAdapter;

public class CategoryActivity extends AppCompatActivity {

    private EditText editTextCategoryName;
    private Button buttonSave, buttonEdit;
    private ImageButton backButton;
    private ListView listViewCategories; // Thêm ListView

    private List<String> categoryList; // Danh sách các danh mục
    private CategoryAdapter categoryAdapter;
    private int selectedPosition = -1; // Biến để lưu vị trí danh mục được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category); // Đảm bảo layout này chỉ chứa các thành phần cần thiết

        // Khởi tạo các biến
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonEdit = findViewById(R.id.buttonEdit);
        backButton = findViewById(R.id.backButton);
        listViewCategories = findViewById(R.id.listViewCategories); // Khởi tạo ListView

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        listViewCategories.setAdapter(categoryAdapter); // Thiết lập adapter cho ListView

        // Thiết lập sự kiện cho nút Lưu
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editTextCategoryName.getText().toString().trim();

                if (!categoryName.isEmpty()) {
                    if (selectedPosition >= 0) {
                        // Nếu có danh mục đang được chọn, thay thế tên
                        categoryList.set(selectedPosition, categoryName);
                        Toast.makeText(CategoryActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                        selectedPosition = -1; // Đặt lại vị trí đã chọn
                    } else {
                        // Nếu không có danh mục nào được chọn, thêm mới
                        categoryList.add(categoryName); // Thêm tên danh mục vào danh sách
                        Toast.makeText(CategoryActivity.this, "Đã lưu danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                    }
                    categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
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
                if (selectedPosition >= 0) {
                    String categoryName = editTextCategoryName.getText().toString().trim();
                    if (!categoryName.isEmpty()) {
                        // Cập nhật danh mục đã chọn
                        categoryList.set(selectedPosition, categoryName);
                        categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                        Toast.makeText(CategoryActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                        editTextCategoryName.setText(""); // Xóa ô nhập
                        selectedPosition = -1; // Đặt lại vị trí đã chọn
                    } else {
                        Toast.makeText(CategoryActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Vui lòng chọn danh mục để sửa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện cho nút quay lại
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Quay lại Activity trước
            }
        });

        // Thiết lập sự kiện khi người dùng chọn danh mục từ danh sách
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lưu vị trí danh mục được chọn
                selectedPosition = position;
                editTextCategoryName.setText(categoryList.get(position)); // Hiển thị tên danh mục được chọn
            }
        });
    }
}
