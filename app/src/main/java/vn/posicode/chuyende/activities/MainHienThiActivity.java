package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.Normalizer;
import java.util.Collections;
import java.util.regex.Pattern;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.ListTable_Adapter;
import vn.posicode.chuyende.models.ListTable;

public class MainHienThiActivity extends AppCompatActivity {
    private RecyclerView recyclerView_table;
    private ListTable_Adapter table_adapter;
    private ArrayList<ListTable> listTable;
    private ArrayList<ListTable> listTableSearch;
    private DrawerLayout drawerLayout;
    private ImageButton btnShowTool, btnBack, btnSearch;
    private EditText edtSearch;
//    private DatabaseReference dbRef;
    private TextView tv_null;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//    Khai bao phan tu nav
    private TextView tv_ql_taikhoan, tv_ql_monan, tv_ql_ban, tv_ql_hoadon, tv_thongke, tv_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectXML();
        //ghiDulieu();

        listTable = new ArrayList<>();
        listTableSearch = new ArrayList<>(listTable);
        docDuLieu();


        table_adapter = new ListTable_Adapter(listTableSearch);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView_table.setAdapter(table_adapter);
        recyclerView_table.setLayoutManager(linearLayout);
//    Hiển thị nav
        btnShowTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
//   Thoat khoi nav
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
//      Xu li phan tu trong nav
tv_ql_taikhoan.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Toast.makeText(MainHienThiActivity.this,"Quan li tai khoan",Toast.LENGTH_SHORT).show();
    }
});
tv_thongke.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainHienThiActivity.this, ThongKeActivity.class);
        startActivity(intent);
    }
});


//        Tim kiem theo ten ban
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = edtSearch.getText().toString();
                Search(key);
            }
        });
//        Xu li thanh tim kiem
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    listTableSearch.clear();
                    listTableSearch.addAll(listTable);
                    table_adapter.notifyDataSetChanged();
                } else {
                    Search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Ghi du lieu vao firestore
/*
    private void ghiDulieu() {
        Map<String, Object> listTable = new HashMap<>();
        listTable.put("nameTable", "Ban 2");
        listTable.put("capacity", "2 - 5 nguoi");
        listTable.put("Status", "đang ngồi");

            firestore.collection("tables01")
                    .add(listTable)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("BBB", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FirestoreFailure", "Error adding document", e);
                            e.printStackTrace();
                        }
                    });
    }
*/
    //    Doc du lieu tu firestore
    private void docDuLieu() {
        firestore.collection("tables").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listTable.clear();  // Xóa dữ liệu cũ
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ListTable table = document.toObject(ListTable.class);
                        listTable.add(table);
                    }
//                    Sap xep theo ten ban
                    sapXepTheoTenBan();
                } else {
                    Log.d(TAG, "Lỗi khi lấy tài liệu: ", task.getException());
                }
            }
        });
    }

    //    Kết nối với xml
    private void connectXML() {
        recyclerView_table = findViewById(R.id.listTable);
        btnShowTool = findViewById(R.id.btnShowTool);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
        tv_null = findViewById(R.id.tv_null);
        tv_ql_monan = findViewById(R.id.tv_ql_monan);
        tv_ql_ban = findViewById(R.id.tv_ql_ban);
        tv_ql_hoadon = findViewById(R.id.tv_ql_hoadon);
        tv_thongke = findViewById(R.id.tv_thongke);

    }

    //    Hàm tìm kiếm
    private void Search(String key) {
        String chuanHoaTuKhoa = chuanHoaChuoi(key.toLowerCase());
        listTableSearch.clear();
        if (key.isEmpty()) {
            listTableSearch.addAll(listTable);
        } else {
            for (ListTable table : listTable
            ) {
                String chuanHoaTenBan = chuanHoaChuoi(table.getName().toLowerCase());
                if (chuanHoaTenBan.contains(chuanHoaTuKhoa)) {
                    listTableSearch.add(table);
                }
            }
        }
//        Kiem tra ket qua tim kiem
        if (listTableSearch.isEmpty()) {
            tv_null.setVisibility(View.VISIBLE);
            recyclerView_table.setVisibility(View.GONE);
        } else {
            tv_null.setVisibility(View.GONE);
            recyclerView_table.setVisibility(View.VISIBLE);
        }
        table_adapter.notifyDataSetChanged();
    }

    //    hàm chuẩn hóa chuôĩ
    private String chuanHoaChuoi(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
//    Hàm sắp xếp tên ban tang dan

    private void sapXepTheoTenBan() {
        Collections.sort(listTable, (table1, table2) -> {
            String name1 = chuanHoaChuoi(table1.getName().toLowerCase());
            String name2 = chuanHoaChuoi(table2.getName().toLowerCase());
            return name1.compareTo(name2);
        });
        listTableSearch.clear();
        listTableSearch.addAll(listTable);
        table_adapter.notifyDataSetChanged();
    }
}