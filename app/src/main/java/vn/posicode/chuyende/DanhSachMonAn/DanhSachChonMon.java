package vn.posicode.chuyende.DanhSachMonAn;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapters.DanhMucAdapter;
import vn.posicode.chuyende.models.DanhMuc;

public class DanhSachChonMon extends AppCompatActivity {
    FirebaseFirestore firestore;

    List<Food> listMonAn;
    List<DanhMuc> listDanhMuc;
    ArrayList<Food> listMonAnDaChon;  // Danh sách món đã chọn
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
        String tableId = intent.getStringExtra("documentId");
        String tableName = intent.getStringExtra("tableName");

        // Kiểm tra xem tableId có phải là null không
        if (tableId == null) {
            Log.e(TAG, "Table ID is null. Please check the Intent data.");
            Toast.makeText(this, "Lỗi: ID bàn không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu không có ID bàn
            return; // Trả về để không tiếp tục thực hiện các bước tiếp theo
        }

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
        monAnAdapter = new MonAnAdapter(listMonAn, new MonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Food monAn) {
                // Khi người dùng chọn món, thêm món vào danh sách món đã chọn
                if (!listMonAnDaChon.contains(monAn)) {
                    listMonAnDaChon.add(monAn);  // Thêm món vào danh sách đã chọn
                    Toast.makeText(DanhSachChonMon.this, "Đã thêm món: " + monAn.getName(), Toast.LENGTH_SHORT).show();
                    updateSelectedCount(); // Cập nhật số lượng món đã chọn
                } else {
                    Toast.makeText(DanhSachChonMon.this, "Món đã có trong danh sách", Toast.LENGTH_SHORT).show();
                }
            }
        }, firestore, tableId, DanhSachChonMon.this);
        listFood.setAdapter(monAnAdapter);

        //TODO Tải dữ liệu món ăn
        docDulieuMonAn();

        btnMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cập nhật trạng thái bàn thành "Đang sử dụng"
                firestore.collection("tables").document(tableId)
                        .update("status", "Đang sử dụng")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Bàn đã được cập nhật trạng thái thành 'Đang sử dụng'");
                                } else {
                                    Log.d(TAG, "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                                }
                            }
                        });

                btnBack.setVisibility(View.GONE);
                // Chuyển sang màn hình danh sách món đã chọn
                Intent intent = new Intent(DanhSachChonMon.this, DanhSachDaChon.class);
                intent.putParcelableArrayListExtra("mon", listMonAnDaChon);  // Truyền danh sách món đã chọn
                intent.putExtra("id", tableId);
                intent.putExtra("name", tableName);
                startActivity(intent);
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

    private void showReservationDialog() {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đặt trước");

        // Inflate layout cho dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reservation, null);
        builder.setView(dialogView);

        // Ánh xạ các view trong dialog
        EditText edtName = dialogView.findViewById(R.id.edtReservationName);
        EditText edtPhone = dialogView.findViewById(R.id.edtReservationPhone);

        // Lấy thông tin bàn từ Intent
        Intent intent = getIntent();
        String tableId = intent.getStringExtra("documentId");
        String tableName = intent.getStringExtra("tableName");

        // Kiểm tra và hiển thị thông tin đặt trước nếu đã có
        firestore.collection("tables")
                .document(tableId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String savedName = documentSnapshot.getString("reservationName");
                        String savedPhone = documentSnapshot.getString("reservationPhone");

                        // Nếu đã có thông tin đặt trước, điền vào EditText
                        if (!TextUtils.isEmpty(savedName)) {
                            edtName.setText(savedName);
                        }
                        if (!TextUtils.isEmpty(savedPhone)) {
                            edtPhone.setText(savedPhone);
                        }
                    }
                });

        // Tạo dialog
        AlertDialog dialog = builder.create();

        // Thiết lập nút Lưu
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String name = edtName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                // Kiểm tra validation
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(DanhSachChonMon.this,
                            "Vui lòng nhập đầy đủ thông tin",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo map dữ liệu để lưu
                Map<String, Object> reservationData = new HashMap<>();
                reservationData.put("reservationName", name);
                reservationData.put("reservationPhone", phone);
                reservationData.put("tableId", tableId);
                reservationData.put("tableName", tableName);
                reservationData.put("status", "Đã đặt");

                // Cập nhật thông tin đặt bàn và trạng thái bàn
                firestore.collection("tables")
                        .document(tableId)
                        .update(reservationData)
                        .addOnSuccessListener(aVoid -> {
                            // Thêm vào collection reservations để tracking
                            firestore.collection("reservations")
                                    .add(reservationData)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(DanhSachChonMon.this,
                                                "Đặt bàn thành công!",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(DanhSachChonMon.this,
                                                "Lỗi: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DanhSachChonMon.this,
                                    "Lỗi khi cập nhật thông tin bàn: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Thiết lập nút Hủy - Xóa thông tin đặt bàn
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // Chuẩn bị dữ liệu để reset
                Map<String, Object> resetData = new HashMap<>();
                resetData.put("reservationName", "");
                resetData.put("reservationPhone", "");
                resetData.put("status", "Trống");

                // Xóa thông tin đặt bàn trên Firestore
                firestore.collection("tables")
                        .document(tableId)
                        .update(resetData)
                        .addOnSuccessListener(aVoid -> {
                            // Reset các trường thông tin trong dialog
                            edtName.setText("");
                            edtPhone.setText("");

                            // Xóa đơn đặt bàn khỏi collection reservations
                            firestore.collection("reservations")
                                    .whereEqualTo("tableId", tableId)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                            doc.getReference().delete();
                                        }

                                        Toast.makeText(DanhSachChonMon.this,
                                                "Đã hủy đặt bàn",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DanhSachChonMon.this,
                                    "Lỗi khi hủy đặt bàn: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Hiển thị dialog
        dialog.show();
    }

    private void updateSelectedCount() {
        String buttonText = "Món đã chọn (" + listMonAnDaChon.size() + ")";
        btnMonDaChon.setText(buttonText);
    }

    private void filterByCategory(String categoryName) {
        List<Food> filteredList = new ArrayList<>();
        if (categoryName.equals("Tất cả")) {
            filteredList.addAll(listMonAn);
        } else {
            for (Food item : listMonAn) {
                if (item.getCategoryName().equals(categoryName)) {
                    filteredList.add(item);
                }
            }
        }
        monAnAdapter.filterList(filteredList);
    }

    private void filter(String text) {
        List<Food> filteredList = new ArrayList<>();
        for (Food item : listMonAn) {
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
                                Food foodData = document.toObject(Food.class);
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

        // Thêm dòng này để ánh xạ nút Đặt trước
        AppCompatButton btnDatTruoc = findViewById(R.id.btnDatTruoc);

        // Và thêm sự kiện click cho nút
        btnDatTruoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReservationDialog();
            }
        });
    }
}