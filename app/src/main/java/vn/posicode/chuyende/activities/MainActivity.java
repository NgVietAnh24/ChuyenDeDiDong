package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.Food;
import vn.posicode.chuyende.adapter.FoodAdapter;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private int selectedPosition = -1;
    private List<String> categoryList;
  private String image;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;

    private EditText editTextTenMonAn, editTextGia;
    private Spinner spinnerDanhMuc;
    private Button buttonAdd, buttonEdit, buttonDelete;
    private ImageView upload, optionsButton;
    private ArrayAdapter<String> adapterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Spinner
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        categoryList = new ArrayList<>();
        categoryList.add("Món khai vị");
        categoryList.add("Món chính");
        categoryList.add("Món tráng miệng");

        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDanhMuc.setAdapter(adapterSpinner);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this,foodList);
        recyclerView.setAdapter(adapter);

        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
        editTextGia = findViewById(R.id.editTextGia);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        upload = findViewById(R.id.upload);

        // Thêm món ăn
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMonAn = editTextTenMonAn.getText().toString();
                String gia = editTextGia.getText().toString();
                String selectedCategory = spinnerDanhMuc.getSelectedItem().toString();
//                String image = "";

                if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
                    foodList.add(new Food(tenMonAn, gia, image));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sửa món ăn
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) {
                    Food food = foodList.get(selectedPosition);
                    String tenMonAnMoi = editTextTenMonAn.getText().toString();
                    String giaMoi = editTextGia.getText().toString();

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

        // Xóa món ăn
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) {
                    foodList.remove(selectedPosition);
                    adapter.notifyItemRemoved(selectedPosition);
                    Toast.makeText(MainActivity.this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                    selectedPosition = -1;
                    editTextTenMonAn.setText("");
                    editTextGia.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Chọn món ăn để xóa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Chức năng tải ảnh lên
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedPosition = position;
                Food food = foodList.get(position);
                editTextTenMonAn.setText(food.getName());
                editTextGia.setText(food.getPrice());
            }
        });

        // Xử lý nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });




        // Xử lý nút Options
        optionsButton = findViewById(R.id.btnoptions);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, optionsButton);
                popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

                // Thiết lập sự kiện khi chọn menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Sử dụng if-else thay cho switch-case
                        if (item.getItemId() == R.id.categoryID) {
                            Toast.makeText(MainActivity.this, "Quản lý danh mục", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                            startActivity(intent);
                            // Xử lý chức năng quản lý danh mục ở đây
                            return true;
                        } else if (item.getItemId() == R.id.category_itemID) {
                            Toast.makeText(MainActivity.this, "Quản lý danh mục con", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CategoryItemActivity.class);
                            startActivity(intent);
                            // Xử lý chức năng quản lý danh mục con ở đây
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });


        // Khởi tạo ImageView
        imageView = findViewById(R.id.upload);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            imageView.setImageURI(imageUri);
//            Glide.with(this).load(imageUri).into(imageView);
//        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Lấy URI của bức ảnh đã chọn
            String imageUrl = imageUri.toString(); // Chuyển đổi URI thành chuỗi URL
            Log.d("Selected Image URI", imageUrl); // Ghi log URL để kiểm tra

            // Lưu URL vào biến image (để sử dụng sau này)
            image = imageUrl;

            // Hiển thị ảnh lên ImageView
            imageView.setImageURI(imageUri);
            Glide.with(this).load(imageUri).into(imageView);
        }
    }
}
