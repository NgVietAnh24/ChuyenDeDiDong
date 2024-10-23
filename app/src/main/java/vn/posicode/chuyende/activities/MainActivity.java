package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;  // Để lưu trữ URI của ảnh đã chọn
    private ImageView imageView;  // ImageView để hiển thị ảnh


    private EditText editTextTenMonAn, editTextGia;
    private Spinner spinnerDanhMuc;
    private Button buttonAdd, buttonEdit, buttonDelete;
    private ImageView upload,optionsButton;
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
//        String image = "C:\\Users\\tranv\\OneDrive\\Hình ảnh\\banhmi1.png";


//        foodList.add(new Food("Bánh mì thịt bò", "80", image));
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo các thành phần giao diện
        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
        editTextGia = findViewById(R.id.editTextGia);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        upload = findViewById(R.id.upload);

        // Thiết lập sự kiện cho nút Thêm
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMonAn = editTextTenMonAn.getText().toString();
                String gia = editTextGia.getText().toString();
                String selectedCategory = spinnerDanhMuc.getSelectedItem().toString(); // Lấy danh mục đã chọn
                String image = "";

                if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
                    // Thêm món ăn vào danh sách
                    foodList.add(new Food(tenMonAn, gia, image)); // Thay thế bằng ảnh món ăn
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

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
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
        optionsButton = findViewById(R.id.btnoptions);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo PopupMenu
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, optionsButton);
                popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

            }
        });


        // Khởi tạo ImageView
        imageView = findViewById(R.id.upload);

        // Khởi tạo sự kiện click cho nút chọn ảnh (upload)
        ImageView upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();  // Gọi hàm chọn ảnh
            }
        });
    }

    // Phương thức chọn ảnh từ thư viện
    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    // Nhận kết quả trả về khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();  // Lấy URI của ảnh đã chọn
            Food food = new Food();
            imageView.setImageURI(imageUri);
            // Sử dụng Glide để tải ảnh vào ImageView
            if(food.getImage() != null){
                Glide.with(this).load(food.getImage()).into(imageView);

            }else {
                Log.d("anh", "Hien thi anh" );
            }
        }else {
            Log.d("anh","Khong hien thi anh");
        }
    }


}
