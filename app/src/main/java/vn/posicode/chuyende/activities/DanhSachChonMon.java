package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;

import vn.posicode.chuyende.adapters.DanhMucChonAdapter;
import vn.posicode.chuyende.adapters.MonAnChonAdapter;
import vn.posicode.chuyende.models.DanhMuc;
import vn.posicode.chuyende.models.MonAn;


public class DanhSachChonMon extends AppCompatActivity {

    FirebaseFirestore firestore;

    List<MonAn> listMonAn;
    List<DanhMuc> listDanhMuc;
    ArrayList<MonAn> listMonAnDaChon;  // Danh sách món đã chọn
    MonAnChonAdapter monAnAdapter;
    DanhMucChonAdapter danhMucAdapter;
    EditText editTextReservationName;
    EditText editTextReservationPhone;

    private EditText edtSearch;
    private RecyclerView listCategory, listFood;
    public static AppCompatButton btnMonDaChon, btnDatTruoc;
    public static ImageView btnBack;

    private String reservationName = "";
    private String reservationPhone = "";
    String tableId;
    String tableName;
    private String reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_chon_mon);
        Event();

        // Khởi tạo từ firebase
        firestore = FirebaseFirestore.getInstance();

        // Dữ liệu được truyền từ home
        Intent intent = getIntent();
        tableId = intent.getStringExtra("id");
        tableName = intent.getStringExtra("name");

        Log.d("ban", "getDATA: " + tableId);
        Log.d("ban", "getDATA: " + tableName);


        // Khởi tạo RecyclerView cho danh mục món ăn
        LinearLayoutManager layoutDanhMuc = new LinearLayoutManager(this);
        layoutDanhMuc.setOrientation(RecyclerView.HORIZONTAL);
        listCategory.setLayoutManager(layoutDanhMuc);
        listDanhMuc = new ArrayList<>();
        danhMucAdapter = new DanhMucChonAdapter(listDanhMuc, new DanhMucChonAdapter.OnItemClickListener() {
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
        monAnAdapter = new MonAnChonAdapter(listMonAn, new MonAnChonAdapter.OnItemClickListener() {
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

//                                    btnBack.setVisibility(View.GONE);
                                } else {
                                    Log.d(TAG, "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                                }
                            }
                        });
            }
        });

        btnDatTruoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReserveDialog();
            }
        });

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

    private void showReserveDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reserve);

        EditText editTextReservationName = dialog.findViewById(R.id.edit_text_reservation_name);
        EditText editTextReservationPhone = dialog.findViewById(R.id.edit_text_reservation_phone);
        Button buttonSave = dialog.findViewById(R.id.button_save);
        Button buttonCancel = dialog.findViewById(R.id.button_cancel);

        // Hiển thị dữ liệu nếu có
        editTextReservationName.setText(reservationName);
        editTextReservationPhone.setText(reservationPhone);

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Lưu đặt bàn
        buttonSave.setOnClickListener(v -> {
            String name = editTextReservationName.getText().toString();
            String phone = editTextReservationPhone.getText().toString();

            if (!name.isEmpty() && !phone.isEmpty()) {
                // Lưu thông tin đặt bàn vào Firestore
                saveReservation(name, phone, tableId, tableName);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng nhập tên và số điện thoại.", Toast.LENGTH_SHORT).show();
            }
        });

        // Hủy đặt bàn
        buttonCancel.setOnClickListener(v -> {
            // Gọi phương thức hủy đặt bàn
            cancelReservation(tableId);
            editTextReservationName.setText("");
            editTextReservationPhone.setText("");
            dialog.dismiss();
        });

        dialog.show();

        // Gọi phương thức để tải thông tin đặt bàn sau khi dialog được hiển thị
        loadReservationDetails(tableId, editTextReservationName, editTextReservationPhone);
    }

    private void saveReservation(String name, String phone, String tableId, String tableName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("name", name);
        reservationData.put("phone", phone);
        reservationData.put("tableId", tableId);
        reservationData.put("tableName", tableName);

        // Kiểm tra xem bàn đã có thông tin đặt trước hay chưa
        firestore.collection("reservations")
                .whereEqualTo("tableId", tableId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Nếu có thông tin đặt trước, cập nhật thông tin
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            reservationId = document.getId(); // Lưu ID của đặt bàn
                            firestore.collection("reservations").document(reservationId)
                                    .update(reservationData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Cập nhật trạng thái bàn thành "Đã đặt"
                                        updateTableStatus(tableId, "Đã đặt", name, phone);
                                        Toast.makeText(this, "Cập nhật thông tin đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Có lỗi xảy ra khi cập nhật thông tin đặt bàn", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Nếu không có thông tin đặt trước, thêm mới
                        firestore.collection("reservations")
                                .add(reservationData)
                                .addOnSuccessListener(documentReference -> {
                                    reservationId = documentReference.getId(); // Lưu ID
                                    updateTableStatus(tableId, "Đã đặt", name, phone); // Cập nhật trạng thái bàn
                                    Toast.makeText(this, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Có lỗi xảy ra khi lưu đặt bàn", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Có lỗi xảy ra khi kiểm tra thông tin đặt bàn", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateTableStatus(String tableId, String status, String name, String phone) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("tables").document(tableId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật UI với tên và số điện thoại
                    reservationName = name; // Lưu tên để sử dụng sau
                    reservationPhone = phone; // Lưu số điện thoại để sử dụng sau
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Có lỗi xảy ra khi cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
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

    private void cancelReservation(String tableId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Cập nhật trạng thái bàn thành "Trống"
        firestore.collection("tables").document(tableId)
                .update("status", "Trống")
                .addOnSuccessListener(aVoid -> {
                    // Xóa thông tin đặt bàn
                    firestore.collection("reservations")
                            .whereEqualTo("tableId", tableId) // Tìm tất cả đặt bàn cho bàn này
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete(); // Xóa đặt bàn
                                    }
                                    Toast.makeText(this, "Đã hủy đặt bàn!", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Có lỗi xảy ra khi cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
                });
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

    private void loadReservationDetails(String tableId, EditText editTextReservationName, EditText editTextReservationPhone) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("reservations")
                .whereEqualTo("tableId", tableId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            reservationId = document.getId(); // Lưu ID của đặt bàn
                            // Set the retrieved name and phone to the input fields
                            editTextReservationName.setText(name);
                            editTextReservationPhone.setText(phone);
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin đặt bàn.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Có lỗi xảy ra khi tải thông tin đặt bàn.", Toast.LENGTH_SHORT).show();
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
        editTextReservationName = findViewById(R.id.edit_text_reservation_name);
        editTextReservationPhone = findViewById(R.id.edit_text_reservation_phone);
        edtSearch = findViewById(R.id.edtSearch);
        listCategory = findViewById(R.id.listCategory);
        listFood = findViewById(R.id.listFood);
        btnMonDaChon = findViewById(R.id.btnMonDaChon);
        btnDatTruoc = findViewById(R.id.btnDatTruoc);
        btnBack = findViewById(R.id.btnBack);
    }
}
