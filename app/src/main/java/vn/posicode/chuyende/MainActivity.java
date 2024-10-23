package vn.posicode.chuyende;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import adapter.Food;
import adapter.FoodAdapter;

//public class MainActivity extends AppCompatActivity {
//    FirebaseFirestore firestore;
//
//    private RecyclerView recyclerView;
//    private FoodAdapter adapter;
//    private List<Food> foodList;
//   //làm sự kiện lick chuột
//    private int selectedPosition = -1;
//    // sự kiên spriner
//    private List<String> categoryList;
//
//    private EditText editTextTenMonAn, editTextGia;
//    private Spinner spinnerDanhMuc;
//    private Button buttonAdd, buttonEdit, buttonDelete;
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Khởi tạo Spinner
//        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
//
//        // Tạo danh sách danh mục
//        categoryList = new ArrayList<>();
//        categoryList.add("Món khai vị");
//        categoryList.add("Món chính");
//        categoryList.add("Món tráng miệng");
//
//        // Thiết lập adapter cho Spinner
//        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerDanhMuc.setAdapter(adapterSpinner);
//
//
//        // Khởi tạo RecyclerView
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Dummy data
//        foodList = new ArrayList<>();
//        foodList.add(new Food("Bánh mì thịt bò", "80", R.drawable.banhmi));
//        adapter = new FoodAdapter(foodList);
//        recyclerView.setAdapter(adapter);
//
//        // Khởi tạo các thành phần giao diện
//        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
//        editTextGia = findViewById(R.id.editTextGia);
//        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
//
//        buttonAdd = findViewById(R.id.buttonAdd);
//        buttonEdit = findViewById(R.id.buttonEdit);
//        buttonDelete = findViewById(R.id.buttonDelete);
//
//        // Xử lý sự kiện cho nút Thêm
////        buttonAdd.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String tenMonAn = editTextTenMonAn.getText().toString();
////                String gia = editTextGia.getText().toString();
////
////                if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
////                    // Thêm món ăn vào danh sách
////                    foodList.add(new Food(tenMonAn, gia, R.drawable.banhmi)); // Thay thế bằng ảnh món ăn
////                    adapter.notifyDataSetChanged();
////                    Toast.makeText(MainActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
////                } else {
////                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
//
//        buttonAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tenMonAn = editTextTenMonAn.getText().toString();
//                String gia = editTextGia.getText().toString();
//                String selectedCategory = spinnerDanhMuc.getSelectedItem().toString(); // Lấy danh mục đã chọn
//
//                if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
//                    // Thêm món ăn vào danh sách
//                    foodList.add(new Food(tenMonAn, gia, R.drawable.banhmi)); // Thay thế bằng ảnh món ăn
//                    adapter.notifyDataSetChanged();
//                    Toast.makeText(MainActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Xử lý sự kiện cho nút Sửa
//        buttonEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedPosition >= 0) {
//                    Food food = foodList.get(selectedPosition);
//                    String tenMonAnMoi = editTextTenMonAn.getText().toString();
//                    String giaMoi = editTextGia.getText().toString();
//
//                    // Kiểm tra và cập nhật thông tin
//                    if (!tenMonAnMoi.isEmpty()) {
//                        food.setName(tenMonAnMoi);
//                    }
//                    if (!giaMoi.isEmpty()) {
//                        food.setPrice(giaMoi);
//                    }
//
//                    adapter.notifyItemChanged(selectedPosition);
//                    Toast.makeText(MainActivity.this, "Sửa món ăn thành công", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Chọn món ăn để sửa", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Xử lý sự kiện cho nút Xóa
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedPosition >= 0) {
//                    foodList.remove(selectedPosition);
//                    adapter.notifyItemRemoved(selectedPosition);
//                    Toast.makeText(MainActivity.this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
//                    selectedPosition = -1; // Reset vị trí được chọn
//                    editTextTenMonAn.setText(""); // Xóa nội dung EditText
//                    editTextGia.setText(""); // Xóa nội dung EditText
//                } else {
//                    Toast.makeText(MainActivity.this, "Chọn món ăn để xóa", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        adapter = new FoodAdapter(foodList);
//        recyclerView.setAdapter(adapter);
//
//// Thiết lập listener cho adapter
//        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                selectedPosition = position; // Lưu vị trí được chọn
//                Food food = foodList.get(position);
//                editTextTenMonAn.setText(food.getName());
//                editTextGia.setText(food.getPrice());
//            }
//        });
//
//
//
//        // Xử lý sự kiện cho nút Back
//        ImageButton backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        // Xử lý sự kiện cho nút Options
//        ImageView optionsButton = findViewById(R.id.options);
//        optionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopupMenu(v);
//            }
//        });
//    }
//
//    public void addCategory(String category) {
//        categoryList.add(category);
//        adapterSpinner.notifyDataSetChanged(); // Cập nhật giao diện
//    }
//
//    public void removeCategory(String category) {
//        categoryList.remove(category);
//        adapterSpinner.notifyDataSetChanged(); // Cập nhật giao diện
//    }
//
//
//    private void showPopupMenu(View view) {
//        PopupMenu popup = new PopupMenu(this, view);
//        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.categoryID) {
//                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
//                    startActivity(intent);
//                    return true;
//                } else if (item.getItemId() == R.id.category_itemID) {
//                    Intent intent = new Intent(MainActivity.this, CategoryItemActivity.class);
//                    startActivity(intent);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
//
//        popup.show();
//    }
//}



public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private int selectedPosition = -1;
    private List<String> categoryList;

    private EditText editTextTenMonAn, editTextGia;
    private Spinner spinnerDanhMuc;
    private Button buttonAdd, buttonEdit, buttonDelete;
    private ArrayAdapter<String> adapterSpinner; // Khai báo adapterSpinner ở đây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Spinner
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);

        // Tạo danh sách danh mục
        categoryList = new ArrayList<>();
        categoryList.add("Món khai vị");
        categoryList.add("Món chính");
        categoryList.add("Món tráng miệng");

        // Thiết lập adapter cho Spinner
        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDanhMuc.setAdapter(adapterSpinner);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data
        foodList = new ArrayList<>();
        foodList.add(new Food("Bánh mì thịt bò", "80", R.drawable.banhmi));
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo các thành phần giao diện
        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
        editTextGia = findViewById(R.id.editTextGia);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Thiết lập sự kiện cho nút Thêm
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMonAn = editTextTenMonAn.getText().toString();
                String gia = editTextGia.getText().toString();
                String selectedCategory = spinnerDanhMuc.getSelectedItem().toString(); // Lấy danh mục đã chọn

                if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
                    // Thêm món ăn vào danh sách
                    foodList.add(new Food(tenMonAn, gia, R.drawable.banhmi)); // Thay thế bằng ảnh món ăn
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện cho nút Sửa
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) {
                    Food food = foodList.get(selectedPosition);
                    String tenMonAnMoi = editTextTenMonAn.getText().toString();
                    String giaMoi = editTextGia.getText().toString();

                    // Kiểm tra và cập nhật thông tin
                    if (!tenMonAnMoi.isEmpty()) {
                        food.setName(tenMonAnMoi);
                    }
                    if (!giaMoi.isEmpty()) {
                        food.setPrice(giaMoi);
                    }

                    adapter.notifyItemChanged(selectedPosition);
                    Toast.makeText(MainActivity.this, "Sửa món ăn thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Chọn món ăn để sửa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện cho nút Xóa
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) {
                    foodList.remove(selectedPosition);
                    adapter.notifyItemRemoved(selectedPosition);
                    Toast.makeText(MainActivity.this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                    selectedPosition = -1; // Reset vị trí được chọn
                    editTextTenMonAn.setText(""); // Xóa nội dung EditText
                    editTextGia.setText(""); // Xóa nội dung EditText
                } else {
                    Toast.makeText(MainActivity.this, "Chọn món ăn để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập listener cho adapter
        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedPosition = position; // Lưu vị trí được chọn
                Food food = foodList.get(position);
                editTextTenMonAn.setText(food.getName());
                editTextGia.setText(food.getPrice());
            }
        });

        // Xử lý sự kiện cho nút Back
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Xử lý sự kiện cho nút Options
        ImageView optionsButton = findViewById(R.id.options);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    public void addCategory(String category) {
        categoryList.add(category);
        adapterSpinner.notifyDataSetChanged(); // Cập nhật giao diện
    }

    public void removeCategory(String category) {
        categoryList.remove(category);
        adapterSpinner.notifyDataSetChanged(); // Cập nhật giao diện
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.categoryID) {
                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.category_itemID) {
                    Intent intent = new Intent(MainActivity.this, CategoryItemActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popup.show();
    }
}
