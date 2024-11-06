package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.CategoryButtonAdapter;
import vn.posicode.chuyende.adapter.MonAnAdapter;  // Import MonAnAdapter mới
import vn.posicode.chuyende.adapter.Food;

public class DanhSachMon extends AppCompatActivity {
    private static final String TAG = "DanhSachMon";
    private static final String FOODS_COLLECTION = "foods";// Tên collection chứa món ăn
    private static final String CATEGORY_COLLECTION = "categories";// Tên collection chứa món ăn
    private List<Food> foodList; // Danh sách chứa món ăn
    private List<CategoryModel> cateList;
    private RecyclerView listMonAn; // RecyclerView cho món ăn
    private MonAnAdapter monAnAdapter; // Adapter cho món ăn
    private CategoryButtonAdapter cateAdapter; // Adapter cho món ăn
    private RecyclerView listCate;
    private EditText searchBar;
    private ImageView searchIcon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhsachmon);

        // Khởi tạo RecyclerView cho món ăn
        listMonAn = findViewById(R.id.foodListRecyclerView);
        listCate = findViewById(R.id.categoryRecyclerView);
        listMonAn.setLayoutManager(new LinearLayoutManager(this));
        // khởi tạo đối tượng tìm kiếm
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listCate.setLayoutManager(horizontalLayoutManager);

        foodList = new ArrayList<>();
        cateList = new ArrayList<>();
        monAnAdapter = new MonAnAdapter(DanhSachMon.this, foodList);

        // Sửa đổi ở đây để truyền monAnAdapter vào constructor
        cateAdapter = new CategoryButtonAdapter(DanhSachMon.this, cateList, monAnAdapter);

        listMonAn.setAdapter(monAnAdapter);
        listCate.setAdapter(cateAdapter);

        // Tải dữ liệu từ Firestore
        layDuLieuTuFirestore(new DataLoadCallback() {
            @Override
            public void onDataLoaded(List<Food> foods) {
                foodList.clear();
                foodList.addAll(foods);
                monAnAdapter.notifyDataSetChanged(); // Cập nhật adapter cho món ăn
            }
        });

        layDuLieuCategoryTuFirestore(new CategoryDataLoadCallback() {
            @Override
            public void onDataLoaded(List<CategoryModel> categories) {
                cateList.clear();
                cateList.addAll(categories);
                cateAdapter.notifyDataSetChanged(); // Cập nhật adapter cho danh mục
            }
        });

        // Sự kiện cho danh mục
        cateAdapter.setOnCategoryClickListener(new CategoryButtonAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String categoryId,String name) {
                // Gọi phương thức lọc món ăn theo danh mục đã chọn
                monAnAdapter.LocDanhMuc(categoryId,name);
            }
        });





    }



    // Định nghĩa interface callback để tải dữ liệu
    public interface DataLoadCallback {
        void onDataLoaded(List<Food> foodList);
    }
    public interface CategoryDataLoadCallback {
        void onDataLoaded(List<CategoryModel> categoryList);
    }

    // Phương thức lấy món ăn từ Firestore
    public void layDuLieuTuFirestore(final DataLoadCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FOODS_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Food> foods = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Food food = document.toObject(Food.class);
                                food.setId(document.getId()); // Set ID từ document
                                foods.add(food);
                            }
                            // Gọi callback với danh sách món ăn đã tải
                            callback.onDataLoaded(foods);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(DanhSachMon.this, "Lỗi khi tải dữ liệu món ăn!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //    Lay danh muc tu firestore
    public void layDuLieuCategoryTuFirestore(final CategoryDataLoadCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(CATEGORY_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CategoryModel> categories = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModel.setId(document.getId()); // Set ID từ document
                                categories.add(categoryModel); // Sửa: Thêm vào danh sách categories thay vì cateList
                            }
                            // Gọi callback với danh sách danh mục đã tải
                            callback.onDataLoaded(categories); // Trả về danh sách categories
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(DanhSachMon.this, "Lỗi khi tải dữ liệu danh mục!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public List<Food> getFoodList() {
        return foodList;
    }
}
