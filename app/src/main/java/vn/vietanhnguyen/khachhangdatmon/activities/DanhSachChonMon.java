package vn.vietanhnguyen.khachhangdatmon.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.adapters.DanhMucAdapter;
import vn.vietanhnguyen.khachhangdatmon.adapters.MonAnAdapter;
import vn.vietanhnguyen.khachhangdatmon.models.DanhMuc;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;


public class DanhSachChonMon extends AppCompatActivity {

    FirebaseFirestore firestore;

    List<MonAn> listMonAn;
    List<DanhMuc> listDanhMuc;
    ArrayList<MonAn> listMonAnDaChon;  // Danh sách món đã chọn
    MonAnAdapter monAnAdapter;
    DanhMucAdapter danhMucAdapter;

    private EditText edtSearch;
    private RecyclerView listCategory, listFood;
    public static AppCompatButton btnMonDaChon;
    public static ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_chon_mon);
        Event();

        // Khởi tạo từ firebase
        firestore = FirebaseFirestore.getInstance();

        // Dữ liệu được truyền từ home
        Intent intent = getIntent();
        String tableId = intent.getStringExtra("id");
        String tableName = intent.getStringExtra("name");

        Log.d("ban", "getDATA: " + tableId);
        Log.d("ban", "getDATA: " + tableName);



        // Khởi tạo RecyclerView cho danh mục món ăn
        LinearLayoutManager layoutDanhMuc = new LinearLayoutManager(this);
        layoutDanhMuc.setOrientation(RecyclerView.HORIZONTAL);
        listCategory.setLayoutManager(layoutDanhMuc);
        listDanhMuc = new ArrayList<>();
        danhMucAdapter = new DanhMucAdapter(listDanhMuc, new DanhMucAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DanhMuc danhMuc) {
                filterByCategory(danhMuc.getName());
            }
        });
        listCategory.setAdapter(danhMucAdapter);
        docDulieuDanhMuc();

        // Khởi tạo RecyclerView cho món ăn
        LinearLayoutManager layoutMonAn = new LinearLayoutManager(this);
        layoutMonAn.setOrientation(RecyclerView.VERTICAL);
        listFood.setLayoutManager(layoutMonAn);

        // Danh sách món ăn và adapter
        listMonAn = new ArrayList<>();
        listMonAnDaChon = new ArrayList<>();  // Khởi tạo danh sách món đã chọn
        // Cập nhật phương thức onItemClick trong MonAnAdapter
        monAnAdapter = new MonAnAdapter(listMonAn, new MonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MonAn monAn) {
                // Khi người dùng chọn món, thêm món vào danh sách món đã chọn
                if (!listMonAnDaChon.contains(monAn)) {
                    listMonAnDaChon.add(monAn);  // Thêm món vào danh sách đã chọn
                    Toast.makeText(DanhSachChonMon.this, "Đã thêm món: " + monAn.getName(), Toast.LENGTH_SHORT).show();
                    // Gọi hàm để cập nhật số lượng món đã chọn từ Firestore
                } else {
                    Toast.makeText(DanhSachChonMon.this, "Món đã có trong danh sách", Toast.LENGTH_SHORT).show();
                }
            }
        }, firestore, tableId, DanhSachChonMon.this);
        listFood.setAdapter(monAnAdapter);

        // Cập nhật số lượng món đã chọn
        updateSelectedCount(tableId);

        //TODO Tải dữ liệu món ăn
        docDulieuMonAn();

        btnMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cập nhật trạng thái bàn thành "Đang sử dụng"
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Đang sử dụng"); // Cập nhật trạng thái bàn

                firestore.collection("tables") // Giả sử bạn có bộ sưu tập tables
                        .document(tableId) // ID của bàn
                        .update(updates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Cập nhật thành công trạng thái bàn
                                    Log.d(TAG, "Trạng thái bàn đã được cập nhật thành công.");

                                    // Lấy danh sách món đã chọn từ bảng selected_foods theo tableId
                                    firestore.collection("selectedFoods")
                                            .whereEqualTo("ban_id", tableId) // Lọc theo tableId
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<MonAn> selectedFoods = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            MonAn monAn = document.toObject(MonAn.class); // Chuyển đổi tài liệu thành đối tượng MonAn
                                                            selectedFoods.add(monAn);
                                                        }

                                                        // Chuyển sang màn hình danh sách món đã chọn
                                                        Intent intent = new Intent(DanhSachChonMon.this, DanhSachDaChon.class);
                                                        intent.putParcelableArrayListExtra("mon", (ArrayList<? extends Parcelable>) selectedFoods);  // Truyền danh sách món đã chọn
                                                        intent.putExtra("id", tableId);
                                                        intent.putExtra("name", tableName);
                                                        startActivity(intent);
                                                    } else {
                                                        Log.d(TAG, "Lỗi khi lấy danh sách món đã chọn: ", task.getException());
                                                    }
                                                }
                                            });

                                    btnBack.setVisibility(View.GONE);
                                } else {
                                    Log.d(TAG, "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                                }
                            }
                        });
            }
        });

        // Sự kiện nhấn nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Sự kiện tìm kiếm món ăn
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private void updateSelectedCount(String tableId) {
        firestore.collection("selectedFoods")
                .whereEqualTo("ban_id", tableId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Lỗi khi lắng nghe thay đổi: ", e);
                            return;
                        }

                        int count = 0;
                        if (value != null) {
                            count = value.size(); // Đếm số lượng món đã chọn
                        }
                        String buttonText = "Món đã chọn (" + count + ")";
                        btnMonDaChon.setText(buttonText);
                    }
                });
    }

    private void filterByCategory(String categoryName) {
        List<MonAn> filteredList = new ArrayList<>();
        if (categoryName.equals("Tất cả")) {
            filteredList.addAll(listMonAn);
        } else {
            for (MonAn item : listMonAn) {
                if (item.getCategory_name().equals(categoryName)) {
                    filteredList.add(item);
                }
            }
        }
        monAnAdapter.filterList(filteredList);
    }

    private void filter(String text) {
        List<MonAn> filteredList = new ArrayList<>();
        for (MonAn item : listMonAn) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        monAnAdapter.filterList(filteredList);
    }

    public void docDulieuDanhMuc() {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listDanhMuc.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DanhMuc cateData = document.toObject(DanhMuc.class);
                                listDanhMuc.add(cateData);
                            }
                            danhMucAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void docDulieuMonAn() {
        firestore.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listMonAn.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MonAn foodData = document.toObject(MonAn.class);
                                listMonAn.add(foodData);
                            }
                            monAnAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void Event() {
        edtSearch = findViewById(R.id.edtSearch);
        listCategory = findViewById(R.id.listCategory);
        listFood = findViewById(R.id.listFood);
        btnMonDaChon = findViewById(R.id.btnMonDaChon);
        btnBack = findViewById(R.id.btnBack);
    }
}
