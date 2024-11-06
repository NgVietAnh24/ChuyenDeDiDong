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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.Food;
import vn.posicode.chuyende.adapter.FoodAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String FOODS_COLLECTION = "foods";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Food> foodList;
    private int selectedPosition = -1;
    private String image;
    private Uri imageUri;


    private EditText editTextTenMonAn, editTextGia;
    private Spinner spinnerDanhMuc;
    private Button buttonAdd, buttonEdit, buttonDelete;
    private ImageView upload, imageView;
    private StorageReference storageReference;


    //Hien thi du lieu len spinner
    private ArrayAdapter<CategoryModel> adapterSpinner;
    private List<CategoryModel> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(); // Khởi tạo Storage reference
        initializeViews();
        setupSpinner();
        setupRecyclerView();
        setupListeners();
        loadFoodData();



    }

    private void initializeViews() {
        // Khởi tạo các View từ giao diện XML
        editTextTenMonAn = findViewById(R.id.editTextTenMonAn);
        editTextGia = findViewById(R.id.editTextGia);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        recyclerView = findViewById(R.id.recyclerView);
        upload = findViewById(R.id.upload);
        imageView = findViewById(R.id.upload);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageView optionsButton = findViewById(R.id.btnoptions);

        // Xử lý nút quay lại
        backButton.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        // Xử lý menu tùy chọn
        optionsButton.setOnClickListener(v -> showOptionsMenu());
    }
//lay du lieu tu firebase them vao spinner
    private void setupSpinner() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            categoryList = new ArrayList<>();
            // Lấy dữ liệu từ Firestore
            db.collection("categories")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               //lay id
                                String category_id = document.getId();
                                String categoryName = document.getString("name"); // Giả sử trường tên là "name"

                                categoryList.add(new CategoryModel(category_id,categoryName));
                            }

                            // Thiết lập adapter sau khi lấy dữ liệu thành công
                            adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
                            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerDanhMuc.setAdapter(adapterSpinner);
                        } else {
                            Log.w("Firestore", "Lỗi khi lấy dữ liệu danh mục", task.getException());
                        }
                    });
    }

    private void setupRecyclerView() {
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));// Cài đặt layout cho RecyclerView
        recyclerView.setAdapter(adapter); // Gắn adapter vào RecyclerView
// Đặt sự kiện khi click vào món ăn trong danh sách
        adapter.setOnItemClickListener(position -> {
            selectedPosition = position;
            Food food = foodList.get(position);
            editTextTenMonAn.setText(food.getName());
            editTextGia.setText(food.getPrice());
            loadImage(food.getImage());
        });
    }

    private void setupListeners() {
        // Thêm món ăn
        buttonAdd.setOnClickListener(v -> addFood());// Sự kiện thêm món ăn

        // Sửa món ăn
        buttonEdit.setOnClickListener(v -> editFood());

        // Xóa món ăn
        buttonDelete.setOnClickListener(v -> deleteFood());

        // Chọn ảnh
        upload.setOnClickListener(v -> chooseImageFromGallery());
    }

    // Tải dữ liệu món ăn từ Firestore
    private void loadFoodData() {
        firestore.collection(FOODS_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                foodList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Food foodItems = document.toObject(Food.class);
                    foodItems.setId(document.getId());
                    foodList.add(foodItems);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Hiển thị ảnh lên ImageView bằng Glide
    private void loadImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Uri imageUri = Uri.parse(imageUrl);
            Glide.with(this).load(imageUri).into(imageView);
        } else {
            imageView.setImageDrawable(null);
        }
    }

//    private void addFood() {
//        String tenMonAn = editTextTenMonAn.getText().toString();
//        String gia = editTextGia.getText().toString();
//        CategoryModel category = (CategoryModel) spinnerDanhMuc.getSelectedItem();
//        String category_id = category.getId();
//
//        if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
//            if (imageUri != null) {
//                // Tải ảnh lên Firebase Storage trước khi lưu dữ liệu món ăn
//                uploadImageToFirebaseStorage();
//            }
//            Food newFood = new Food(tenMonAn, gia, image, category_id);
//            foodList.add(newFood);
//            saveFoodToFirestore(newFood);// Lưu món ăn lên Firestore
//            adapter.notifyDataSetChanged();
//            showToast("Thêm món ăn thành công");
//        } else {
//            showToast("Vui lòng nhập đủ thông tin");
//        }
//    }

    private void addFood() {
        String tenMonAn = editTextTenMonAn.getText().toString();
        String gia = editTextGia.getText().toString();

        // Kiểm tra xem Spinner có giá trị được chọn không
        if (spinnerDanhMuc.getSelectedItem() != null) {
            CategoryModel category = (CategoryModel) spinnerDanhMuc.getSelectedItem();
            String category_id = category.getId();
            String categoryName = category.getName(); // Lấy tên danh mục

            if (!tenMonAn.isEmpty() && !gia.isEmpty()) {
                String foodId = firestore.collection(FOODS_COLLECTION).document().getId(); // Tạo ID món ăn mới

                if (imageUri != null) {
                    uploadImageToFirebaseStorage(foodId, tenMonAn, gia, category_id, categoryName); // Tải ảnh lên trước khi lưu món ăn
                } else {
                    Food newFood = new Food(foodId, tenMonAn, gia, null, category_id, categoryName); // Không có ảnh
                    saveFoodToFirestore(newFood);
                    foodList.add(newFood);
                    adapter.notifyDataSetChanged();
                    showToast("Thêm món ăn thành công");
                }
            } else {
                showToast("Vui lòng nhập đủ thông tin");
            }
        } else {
            showToast("Vui lòng chọn danh mục");
        }
    }

    // Tải ảnh lên Firebase Storage và lấy URL ảnh sau khi tải
    private void uploadImageToFirebaseStorage(String foodId, String tenMonAn, String gia, String category_id, String categoryName) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("Food/" + foodId + ".jpg");

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString(); // Lưu URL của ảnh vào biến tạm thời
                            Glide.with(this).load(uri).into(imageView); // Hiển thị ảnh trên ImageView
                            showToast("Tải ảnh lên thành công!");

                            // Tạo đối tượng món ăn sau khi có URL ảnh
                            Food newFood = new Food(foodId, tenMonAn, gia, imageUrl, category_id, categoryName);
                            saveFoodToFirestore(newFood); // Lưu món ăn lên Firestore
                            foodList.add(newFood);
                            adapter.notifyDataSetChanged();
                            Log.d("UploadImage", "URL của ảnh: " + imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        showToast("Lỗi khi tải ảnh lên: " + e.getMessage());
                        Log.e("UploadImage", "Lỗi khi tải ảnh lên: ", e);
                    });
        } else {
            showToast("Vui lòng chọn ảnh trước khi tải lên.");
        }
    }







    // Lưu đối tượng Food lên Firestore
    private void saveFoodToFirestore(Food foodItem) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            firestore.collection(FOODS_COLLECTION).add(foodItem)
                    .addOnSuccessListener(documentReference -> {

                     //set id duoc tao tu dong tu firebase
                        String generatedId = documentReference.getId();
                        foodItem.setId(generatedId);

   //cap nhat lai id vua duoc set
                        documentReference.update("id", generatedId)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(MainActivity.this, "Lưu thành công với ID: " + generatedId, Toast.LENGTH_SHORT).show();
                                    Log.d("Firestore", "DocumentSnapshot successfully written with ID: " + generatedId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error updating document with ID", e);
                                });
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding document", e);
                        showToast("Lỗi khi thêm món ăn!");
                    });
        } else {
            showToast("Permission not granted");
        }
    }

    private void editFood() {
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

            updateFoodInFirestore(food);
            adapter.notifyItemChanged(selectedPosition);
            showToast("Sửa món ăn thành công");
        } else {
            showToast("Chọn món ăn để sửa");
        }
    }

    private void updateFoodInFirestore(Food food) {
        if (food.getId() != null) {
            firestore.collection(FOODS_COLLECTION).document(food.getId())
                    .set(food)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Món ăn đã được cập nhật thành công!"))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi cập nhật món ăn", e);
                        showToast("Lỗi khi cập nhật món ăn!");
                    });
        }
    }

    private void deleteFood() {
        if (selectedPosition >= 0) {
            Food food = foodList.get(selectedPosition);
            deleteFoodFromFirestore(food);
            foodList.remove(selectedPosition);
            adapter.notifyItemRemoved(selectedPosition);
            showToast("Xóa món ăn thành công");
            clearSelection();
        } else {
            showToast("Chọn món ăn để xóa");
        }
    }

    private void deleteFoodFromFirestore(Food food) {
        if (food.getId() != null) {
            firestore.collection(FOODS_COLLECTION).document(food.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Món ăn đã được xóa thành công!"))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi xóa món ăn", e);
                        showToast("Lỗi khi xóa món ăn!");
                    });
        }
    }

    private void clearSelection() {
        selectedPosition = -1;
        editTextTenMonAn.setText("");
        editTextGia.setText("");
        imageView.setImageDrawable(null);
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
            getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            image = imageUri.toString();
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showOptionsMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.btnoptions));
        popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.categoryID) {
                showToast("Quản lý danh mục");
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                return true;
            } else if (item.getItemId() == R.id.category_itemID) {
                showToast("Quản lý danh mục con");
                startActivity(new Intent(MainActivity.this, CategoryItemActivity.class));
                return true;
            }
            return false;
        });

        popupMenu.show();
    }
}
