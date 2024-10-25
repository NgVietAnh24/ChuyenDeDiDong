package vn.posicode.chuyende.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.Food;
import vn.posicode.chuyende.adapter.FoodAdapter;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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

        // Khởi tạo danh mục
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        categoryList = new ArrayList<>();
        categoryList.add("Món khai vị");
        categoryList.add("Món chính");
        categoryList.add("Món tráng miệng");

        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDanhMuc.setAdapter(adapterSpinner);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodList = new ArrayList<>();
        docDuLieu(); // Đọc dữ liệu từ Firestore
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo các view
        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
        buttonAdd = findViewById(R.id.buttonAdd);
        editTextGia = findViewById(R.id.editTextGia);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        upload = findViewById(R.id.upload);
        imageView = findViewById(R.id.upload); // Đảm bảo bạn đã khởi tạo đúng ImageView

        // Thêm món ăn
        buttonAdd.setOnClickListener(v -> {
            String tenMonAn = editTextTenMonAn.getText().toString();
            String gia = editTextGia.getText().toString();
            String selectedCategory = spinnerDanhMuc.getSelectedItem().toString();

            if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
                Food newFood = new Food(tenMonAn, gia, image);
                foodList.add(newFood);
                ghiDuLieu(newFood); // Ghi dữ liệu vào Firestore
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        // Sửa món ăn
        buttonEdit.setOnClickListener(v -> {
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
                if (image != null && !image.isEmpty()) {
                    food.setImage(image);
                }

                // Cập nhật Firestore
                updateFoodInFirestore(food);
                adapter.notifyItemChanged(selectedPosition);
                Toast.makeText(MainActivity.this, "Sửa món ăn thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Chọn món ăn để sửa", Toast.LENGTH_SHORT).show();
            }
        });

        // Xóa món ăn
        buttonDelete.setOnClickListener(v -> {
            if (selectedPosition >= 0) {
                Food food = foodList.get(selectedPosition);
                deleteFoodFromFirestore(food); // Xóa từ Firestore
                foodList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
                Toast.makeText(MainActivity.this, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                selectedPosition = -1;
                editTextTenMonAn.setText("");
                editTextGia.setText("");
                imageView.setImageDrawable(null); // Xóa ảnh
            } else {
                Toast.makeText(MainActivity.this, "Chọn món ăn để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        // Chọn ảnh
        upload.setOnClickListener(v -> chooseImageFromGallery());

        // Xử lý sự kiện chọn món ăn
        adapter.setOnItemClickListener(position -> {
            selectedPosition = position;
            Food food = foodList.get(position);
            editTextTenMonAn.setText(food.getName());
            editTextGia.setText(food.getPrice());

            if (food.getImage() != null && !food.getImage().isEmpty()) {
                Uri imageUri = Uri.parse(food.getImage());
                Glide.with(MainActivity.this).load(imageUri).into(imageView); // Hiển thị ảnh
            } else {
                imageView.setImageDrawable(null); // Nếu không có ảnh, xóa ảnh
            }
        });

        // Xử lý nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        // Xử lý menu tùy chọn
        optionsButton = findViewById(R.id.btnoptions);
        optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, optionsButton);
            popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.categoryID) {
                    Toast.makeText(MainActivity.this, "Quản lý danh mục", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.category_itemID) {
                    Toast.makeText(MainActivity.this, "Quản lý danh mục con", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CategoryItemActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Quản lý hình ảnh lâu dài
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            image = imageUri.toString();
            Glide.with(this).load(imageUri).into(imageView); // Hiển thị ảnh
        }
    }

    // Ghi dữ liệu
    private void ghiDuLieu(Food foodItem) {
        // Kiểm tra quyền Internet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            // Ghi dữ liệu vào Firestore
            Log.d("Firebase", "Ghi dữ liệu: " + foodItem.getName() + ", " + foodItem.getPrice() + ", " + foodItem.getImage());

            firestore.collection("foods").add(foodItem)
                    .addOnSuccessListener(documentReference -> Log.d("Firebase", "DocumentSnapshot added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.e("Firebase Error", "Error adding document", e));
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    // Cập nhật món ăn trong Firestore
    private void updateFoodInFirestore(Food food) {
        // Tìm ID của món ăn đã chọn (bạn cần thêm logic để lấy ID này khi đọc dữ liệu)
        // Đây là ví dụ, bạn cần thay đổi theo thực tế
        // Nếu bạn lưu ID trong Food, bạn có thể truy cập như food.getId()
        // Hoặc bạn có thể thêm một trường id trong Food và lưu nó cùng với dữ liệu
    }

    // Xóa món ăn từ Firestore
    private void deleteFoodFromFirestore(Food food) {
        // Tìm ID của món ăn đã chọn (bạn cần thêm logic để lấy ID này khi đọc dữ liệu)
        // Đây là ví dụ, bạn cần thay đổi theo thực tế
        // Nếu bạn lưu ID trong Food, bạn có thể truy cập như food.getId()
        // Hoặc bạn có thể thêm một trường id trong Food và lưu nó cùng với dữ liệu
    }

    // Đọc dữ liệu
    private void docDuLieu() {
        firestore.collection("foods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    foodList.clear();  // Xóa dữ liệu cũ
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Food foodItems = document.toObject(Food.class);
                        foodList.add(foodItems);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("ErrorRead", "Lỗi khi lấy dữ liệu: ", task.getException());
                }
            }
        });
    }
}
