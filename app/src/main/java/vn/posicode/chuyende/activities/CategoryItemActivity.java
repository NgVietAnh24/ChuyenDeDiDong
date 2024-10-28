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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FirebaseFirestore db;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> categoryNames = new ArrayList<>();;
    private List<String> itemNames = new ArrayList<>(); // Dữ liệu cho ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item);

        // Khởi tạo các biến
        db = FirebaseFirestore.getInstance();
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonSave = findViewById(R.id.buttonSave);
        buttonEdit = findViewById(R.id.buttonEdit);
        backButton = findViewById(R.id.backButton);
        listViewCategories = findViewById(R.id.listViewCategories); // Khởi tạo ListView

        // Khởi tạo danh sách và adapter
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        listViewCategories.setAdapter(categoryAdapter); // Thiết lập adapter cho ListView

        spinnerCategoryType = findViewById(R.id.spinnerCategoryType); // Khởi tạo Spinner
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.category_types, android.R.layout.simple_spinner_item);
       // arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categoryList);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerCategoryType.setAdapter(arrayAdapter); // Gán adapter cho Spinner
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryType.setAdapter(arrayAdapter);

        // Gọi loadCategories để tải danh mục
        loadCategories();
        loadCategoriesItem();
      //  loadCategories(); // Đọc danh mục từ Firestore

        // Thiết lập sự kiện cho nút Lưu
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = editTextCategoryName.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    if (selectedPosition >= 0) {
                        // Nếu có danh mục đang được chọn, gọi hàm sửa
                        updateCategory(selectedPosition, categoryName);
                    } else {
                        // Nếu không có danh mục nào được chọn, gọi hàm thêm mới
                        addCategory(categoryName);
                    }
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
                        updateCategory(selectedPosition, categoryName);
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
                editTextCategoryName.setText(categoryList.get(position).getName()); // Hiển thị tên danh mục được chọn
            }
        });
    }


//Ham doc categoriesItem
private void loadCategoriesItem() {
    db.collection("categoriesItem")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String categoryName = document.getString("name");
                            CategoryModel category = new CategoryModel(documentId, categoryName);
                            categoryList.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CategoryItemActivity.this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}





    // Hàm đọc danh mục từ Firestore
    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoryNames.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                String categoryName = document.getString("name");
//                                CategoryModel category = new CategoryModel(documentId, categoryName);
//                                categoryList.add(category);
                                if(categoryName != null)
                                {
                                    categoryNames.add(categoryName);
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CategoryItemActivity.this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    // Hàm thêm danh mục mới vào Firestore
    private void addCategory(String categoryName) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", categoryName);

        db.collection("categoriesItem")
                .add(category)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            CategoryModel newCategory = new CategoryModel(documentReference.getId(), categoryName);
                            categoryList.add(newCategory);
                            categoryAdapter.notifyDataSetChanged();
                            Toast.makeText(CategoryItemActivity.this, "Đã lưu danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                            editTextCategoryName.setText(""); // Xóa ô nhập
                        } else {
                            Toast.makeText(CategoryItemActivity.this, "Lỗi khi lưu danh mục", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Hàm cập nhật danh mục đã chọn trong Firestore
    private void updateCategory(int position, String categoryName) {
        CategoryModel category = categoryList.get(position);
        String documentId = category.getId();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", categoryName);

        db.collection("categoriesItem")
                .document(documentId)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            category.setName(categoryName);
                            categoryAdapter.notifyDataSetChanged();
                            Toast.makeText(CategoryItemActivity.this, "Đã sửa danh mục: " + categoryName, Toast.LENGTH_SHORT).show();
                            editTextCategoryName.setText(""); // Xóa ô nhập
                            selectedPosition = -1; // Đặt lại vị trí đã chọn
                        } else {
                            Toast.makeText(CategoryItemActivity.this, "Lỗi khi sửa danh mục", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
