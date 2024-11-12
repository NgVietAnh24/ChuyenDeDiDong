package vn.posicode.chuyende.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.CategoryButtonAdapter;
import vn.posicode.chuyende.adapter.MonAnAdapter;  // Import MonAnAdapter mới
import vn.posicode.chuyende.adapter.Food;

public class DanhSachMon extends AppCompatActivity {
    private static final String TAG = "DanhSachMon";
    private static final String FOODS_COLLECTION = "foods";// Tên collection chứa món ăn
    private static final String CATEGORY_COLLECTION = "categories";// Tên collection chứa món ăn
    private List<Food> foodList; // Danh sách chứa món ăn
    private List<Food> foodListSearch; // Danh sách chứa món ăn
    private List<CategoryModel> cateList;
    private RecyclerView listMonAn; // RecyclerView cho món ăn
    private MonAnAdapter monAnAdapter; // Adapter cho món ăn
    private CategoryButtonAdapter cateAdapter; // Adapter cho món ăn
    private RecyclerView listCate;
    private EditText searchBar;
    private ImageView searchIcon;
    private TextView tv_null;
    private Button btnMonDaChon;
    private List<Food> selectedFoods = new ArrayList<>(); // Danh sách món ăn đã chọn
    private int tongMonDaChon = 0;
    private HashMap<String, Integer> monDaChon = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danhsachmon);
        Event();


        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listCate.setLayoutManager(horizontalLayoutManager);

        foodList = new ArrayList<>();
        foodListSearch = new ArrayList<>(foodList);
        cateList = new ArrayList<>();
        listMonAn.setLayoutManager(new LinearLayoutManager(this));
        listCate.setAdapter(cateAdapter);


//        monAnAdapter = new MonAnAdapter(DanhSachMon.this, foodListSearch);
//        listMonAn.setAdapter(monAnAdapter);
//        btnmondachon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DanhSachMon.this, MonAnDaChon.class);
//                startActivity(intent); // Bắt đầu Activity mới
//            }
//        });
        Log.d("ABC","-----------------------");

        btnMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chuyển danh sách món đã chọn (monDaChon) qua Intent
                Intent intent = new Intent(DanhSachMon.this, MonAnDaChonActivity.class);
                ArrayList<String> selectedFoodIds = new ArrayList<>(monDaChon.keySet()); // Lấy danh sách các món ăn đã chọn
                intent.putStringArrayListExtra("selectedFoods", selectedFoodIds); // Truyền qua Intent
                startActivity(intent); // Bắt đầu Activity mới

            }
        });



        // Tải dữ liệu từ Firestore
        layDuLieuTuFirestore(new DataLoadCallback() {
            @Override
            public void onDataLoaded(List<Food> foods) {
                foodList.clear();
                foodList.addAll(foods);
                foodListSearch.clear();
                foodListSearch.addAll(foods); // Khởi tạo dữ liệu cho danh sách tìm kiếm
                monAnAdapter.notifyDataSetChanged();

            }
        });
        monAnAdapter = new MonAnAdapter(DanhSachMon.this, foodListSearch);
        listMonAn.setAdapter(monAnAdapter);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        cateAdapter = new CategoryButtonAdapter(DanhSachMon.this, cateList, monAnAdapter);
        listCate.setAdapter(cateAdapter);
        // Sự kiện cho danh mục
        cateAdapter.setOnCategoryClickListener(new CategoryButtonAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String categoryId, String name) {
                // Gọi phương thức lọc món ăn theo danh mục đã chọn

                monAnAdapter.LocDanhMuc(categoryId, name);

            }
        });
        monAnAdapter.setOnItemClickListener(new MonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//             count++;
//             btnmondachon.setText("Món đã chọn: " + "("+count+")");
                Food food = foodList.get(position);
                showQuantityDialog(food.getId());
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SearchFood(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void Event() {
        // Khởi tạo RecyclerView cho món ăn
        listMonAn = findViewById(R.id.foodListRecyclerView);
        listCate = findViewById(R.id.categoryRecyclerView);

        // khởi tạo đối tượng tìm kiếm
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);
        tv_null = findViewById(R.id.tv_null);
        btnMonDaChon = findViewById(R.id.btnmondachon);
    }

    // Hàm tìm kiếm món ăn
    private void SearchFood(String key) {
        String chuanHoaTuKhoa = chuanHoaChuoi(key.toLowerCase());
        foodListSearch.clear(); // Xóa danh sách tìm kiếm cũ

        if (key.isEmpty()) {
            foodListSearch.addAll(foodList);
        } else {
            // Nếu từ khóa có nội dung, thực hiện tìm kiếm
            for (Food food : foodList) {
                String chuanHoaTenMon = chuanHoaChuoi(food.getName().toLowerCase());
                if (chuanHoaTenMon.contains(chuanHoaTuKhoa)) {
                    foodListSearch.add(food);
                }
            }
        }

        // Kiểm tra kết quả tìm kiếm
        if (foodListSearch.isEmpty()) {
            tv_null.setVisibility(View.VISIBLE);
            listMonAn.setVisibility(View.GONE);
        } else {
            tv_null.setVisibility(View.GONE);
            listMonAn.setVisibility(View.VISIBLE);
        }

        // Cập nhật lại dữ liệu cho adapter của RecyclerView
        monAnAdapter.notifyDataSetChanged();
    }


    // Hàm chuẩn hóa chuỗi (loại bỏ dấu tiếng Việt)
    private String chuanHoaChuoi(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
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

    public void showQuantityDialog(String foodId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        TextView btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        TextView btnIncrease = dialogView.findViewById(R.id.btn_increase);

        final int[] quantity = {monDaChon.getOrDefault(foodId, 1)};
        tvQuantity.setText(String.valueOf(quantity[0]));

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity[0] > 0) {
                    quantity[0]--;
                    tvQuantity.setText(String.valueOf(quantity[0]));
                }
            }
        });
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity[0]++;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (quantity[0] == 0) {
                    monDaChon.remove(foodId);
                } else {
                    monDaChon.put(foodId, quantity[0]);
                }
//                Reset tổng món để tính lại
                tongMonDaChon = 0;
                for (Integer quanlity : monDaChon.values()) {
                    tongMonDaChon += quanlity;
                }
                btnMonDaChon.setText("Món đã chọn: " + tongMonDaChon);
                if (tongMonDaChon == 0) {
                    btnMonDaChon.setText("Món đã chọn");
                }
            }
        });
    }
}
