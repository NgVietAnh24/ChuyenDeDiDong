package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.Category_Adapter;
import vn.posicode.chuyende.adapter.MonAn_Adapter;
import vn.posicode.chuyende.models.CategoryModel;
import vn.posicode.chuyende.models.MonAnModel;
import vn.posicode.chuyende.models.MonDaChonModel;

public class DSMonAnActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 999;
    private RecyclerView recDanhMuc;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ArrayList<CategoryModel> list_cate;
    private Category_Adapter category_adapter;
    private RecyclerView recMonAn;
    private ArrayList<MonAnModel> list_monan;
    private MonAn_Adapter monAnAdapter;
    private int tongMonDaChon = 0;
    private Button btnMonDaChon;
    private ArrayList<MonDaChonModel> list_monDaChon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsmon_an);
        connectXML();
        list_cate = new ArrayList<>();
        list_monan = new ArrayList<>();
        list_monDaChon = new ArrayList<>();
//    Đọc và hiển thị dữ liệu món ăn
        docDuLieuFood();
        monAnAdapter = new MonAn_Adapter(DSMonAnActivity.this, list_monan);
        LinearLayoutManager linearLayoutFood = new LinearLayoutManager(this);
        linearLayoutFood.setOrientation(RecyclerView.VERTICAL);
        recMonAn.setAdapter(monAnAdapter);
        recMonAn.setLayoutManager(linearLayoutFood);
//        Đọc và hiển thị dữ liệu danh mục
        docDuLieuCategory();
        category_adapter = new Category_Adapter(list_cate);
        LinearLayoutManager linearLayoutCate = new LinearLayoutManager(this);
        linearLayoutCate.setOrientation(RecyclerView.HORIZONTAL);
        recDanhMuc.setAdapter(category_adapter);
        recDanhMuc.setLayoutManager(linearLayoutCate);

//        Xử lí chọn món
        monAnAdapter.setOnItemClickListener(new MonAn_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String foodId) {
                showQuantityDialog(foodId);
            }
        });
//        Btn mon da chon
        btnMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MonDaChonModel> listMonDaChon = getMonDaChonList();
                Intent intent = new Intent(DSMonAnActivity.this, MonDaChonActivity.class);
                intent.putParcelableArrayListExtra("monAnDaChon", listMonDaChon);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    //    Tao danh sach cac mon da chon
    private ArrayList<MonDaChonModel> getMonDaChonList() {
        return new ArrayList<>(list_monDaChon);
    }

    //Đọc dữ liệu danh mục
    private void docDuLieuCategory() {
        firestore.collection("categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    list_cate.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryModel categoryModel = document.toObject(CategoryModel.class);
                        list_cate.add(categoryModel);
                    }
                    category_adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
                }
            }
        });
    }

    //    Đọc dữ liệu món ăn
    private void docDuLieuFood() {
        firestore.collection("foods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    list_monan.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        MonAnModel monAnModel = document.toObject(MonAnModel.class);
                        list_monan.add(monAnModel);
                    }
                    monAnAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
                }
            }
        });
    }

    //Show dialog chọn số lượng món
    public void showQuantityDialog(String foodId) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quality, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);

        if (list_monDaChon == null) {
            list_monDaChon = new ArrayList<>();
        }

        MonDaChonModel selectedMon = null;
        for (MonDaChonModel item : list_monDaChon) {
            if (item != null && item.getMonAn() != null && item.getMonAn().getId() != null) {
                Log.d(TAG, "Kiểm tra ID món: " + item.getMonAn().getId() + " với foodId: " + foodId);
                if (item.getMonAn().getId().equals(foodId)) {
                    selectedMon = item;
                    break;
                }
            }
        }
        final int[] quantity = {selectedMon != null ? selectedMon.getSoLuong() : 1};
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
                    list_monDaChon.removeIf(item -> item.getMonAn() != null && item.getMonAn().getId() != null && item.getMonAn().getId().equals(foodId));
                } else {
                    boolean foodExists = false;
                    for (MonDaChonModel item : list_monDaChon) {
                        if (item.getMonAn() != null && item.getMonAn().getId() != null) {
                            if (item.getMonAn().getId().equals(foodId)) {
                                item.setSoLuong(quantity[0]);
                                foodExists = true;
                                break;
                            }
                        }
                    }
                    if (!foodExists) {
                        // Thêm món mới nếu chưa có
                        for (MonAnModel monAn : list_monan) {
                            if (monAn != null && monAn.getId() != null && monAn.getId().equals(foodId)) {
                                list_monDaChon.add(new MonDaChonModel(monAn, quantity[0]));
                                break;
                            }
                        }
                    }
                }
                updateTotalQuantity();
            }
        });
    }
    //    Ánh xạ
    private void connectXML() {
        recDanhMuc = findViewById(R.id.listDanhMuc);
        recMonAn = findViewById(R.id.listMonAn);
        btnMonDaChon = findViewById(R.id.btnMonDaChon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<MonDaChonModel> updatedList = data.getParcelableArrayListExtra("updatedMonDaChon");
            tongMonDaChon = data.getIntExtra("totalQuantity", 0);
            if (updatedList != null) {
                tongMonDaChon = 0;
                list_monDaChon.clear();

                for (MonDaChonModel item : updatedList) {
                    if (item != null) {
                        list_monDaChon.add(item);
                        tongMonDaChon += item.getSoLuong();
                    }
                }
                btnMonDaChon.setText("Món đã chọn: " + tongMonDaChon);
                if (tongMonDaChon == 0) {
                    btnMonDaChon.setText("Món đã chọn");
                }
            }

            // Cập nhật adapter sau khi thay đổi dữ liệu
            monAnAdapter.notifyDataSetChanged();
        }
    }


    private void updateTotalQuantity() {
        tongMonDaChon = 0;
        for (MonDaChonModel item : list_monDaChon) {
            if (item != null) {
                tongMonDaChon += item.getSoLuong();
            }
        }
        btnMonDaChon.setText("Món đã chọn: " + tongMonDaChon);
        if (tongMonDaChon == 0) {
            btnMonDaChon.setText("Món đã chọn");
        }
    }
}