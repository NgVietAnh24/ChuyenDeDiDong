package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.CategoryAdapter;

public class CategoryItemActivity extends AppCompatActivity {

    private EditText editTextCategoryName;
    private Button buttonSave, buttonEdit;
    private ImageButton backButton;
    private ListView listViewCategories; // Thêm ListView
    private Spinner spinnerCategoryType; // Khai báo Spinner
    private List<CategoryModel> categoryList; // Danh sách các danh mục
    private CategoryAdapter categoryAdapter;
    private int selectedPosition = -1; // Biến để lưu vị trí danh mục được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item);

        // Khởi tạo các biến
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonEdit = findViewById(R.id.buttonEdit);
        backButton = findViewById(R.id.backButton);
        listViewCategories = findViewById(R.id.listViewCategories); // Khởi tạo ListView

        // Khởi tạo danh sách và adapter
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        listViewCategories.setAdapter(categoryAdapter); // Thiết lập adapter cho ListView




        listViewCategories = findViewById(R.id.listViewCategories);
        spinnerCategoryType = findViewById(R.id.spinnerCategoryType); // Khởi tạo Spinner



        // Thiết lập adapter cho Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryType.setAdapter(spinnerAdapter); // Gán adapter cho Spinner





        // Thiết lập sự kiện cho nút Lưu
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editTextCategoryName.getText().toString().trim();

                if (!categoryName.isEmpty()) {
                    if (selectedPosition >= 0) {
                        // Nếu có danh mục đang được chọn, thay thế tên
                        categoryList.get(selectedPosition).setName(categoryName); // Cập nhật tên trong đối tượng
                        Toast.makeText(CategoryItemActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                        selectedPosition = -1; // Đặt lại vị trí đã chọn
                    } else {
                        // Nếu không có danh mục nào được chọn, thêm mới
                        CategoryModel newCategory = new CategoryModel(); // Tạo một đối tượng mới
                        newCategory.setName(categoryName);
                        categoryList.add(newCategory); // Thêm đối tượng vào danh sách
                        Toast.makeText(CategoryItemActivity.this, "Đã lưu danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                    }
                    categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                    editTextCategoryName.setText(""); // Xóa ô nhập
                } else {
                    Toast.makeText(CategoryItemActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
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
                        categoryList.get(selectedPosition).setName(categoryName); // Cập nhật tên trong đối tượng
                        Toast.makeText(CategoryItemActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                        categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                        editTextCategoryName.setText(""); // Xóa ô nhập
                        selectedPosition = -1; // Đặt lại vị trí đã chọn
                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryItemActivity.this, "Vui lòng chọn danh mục để sửa", Toast.LENGTH_SHORT).show();
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
                        categoryList.get(selectedPosition).setName(categoryName); // Cập nhật tên trong đối tượng
                        Toast.makeText(CategoryItemActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                        categoryAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                        editTextCategoryName.setText(""); // Xóa ô nhập
                        selectedPosition = -1; // Đặt lại vị trí đã chọn
                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CategoryItemActivity.this, "Vui lòng chọn danh mục để sửa", Toast.LENGTH_SHORT).show();
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
                editTextCategoryName.setText((CharSequence) categoryList.get(position)); // Hiển thị tên danh mục được chọn
            }
        });
    }
}
