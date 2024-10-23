package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.ListTable_Adapter;
import vn.posicode.chuyende.models.ListTable;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView_table;
    private ListTable_Adapter table_adapter;
    private ArrayList<ListTable> listTable;
    private ArrayList<ListTable> listTableSearch;
    private DrawerLayout drawerLayout;
    private ImageButton btnShowTool, btnBack, btnSearch;
    private EditText edtSearch;
    private DatabaseReference dbRef;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectXML();
        //ghiDulieu();

        listTable = new ArrayList<>();
        listTableSearch = new ArrayList<>(listTable);
        docDuLieu();
        //Khoi tao FireBase Database
//        dbRef = FirebaseDatabase.getInstance().getReference("");
//        //Doc du lieu tu realtime
//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listTable.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ListTable table = snapshot.getValue(ListTable.class);
//                    if (table != null) {
//                        listTable.add(table);
//                    }
//                }
//                listTableSearch.clear();
//                listTableSearch.addAll(listTable);
//                table_adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


//        Them du lieu vao ban
//        listTable = new ArrayList<>();
//        listTable.add(new ListTable(1,"Bàn 1", "4 - 5 nguoi", true));
//        listTable.add(new ListTable(2,"Bàn 2", "4 - 8 nguoi", true));
//        listTable.add(new ListTable(3,"Bàn 9", "4 - 8 nguoi", true));

//       Sử dụng danh sách tìm kiêếm
        //   listTableSearch = new ArrayList<>(listTable);
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

    //    Doc du lieu tu firestore
    private void docDuLieu() {
        firestore.collection("tables01").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listTable.clear();  // Xóa dữ liệu cũ
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ListTable table = document.toObject(ListTable.class);
                        listTable.add(table);
                    }
                    listTableSearch.clear();
                    listTableSearch.addAll(listTable);
                    table_adapter.notifyDataSetChanged();
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
                String chuanHoaTenBan = chuanHoaChuoi(table.getNameTable().toLowerCase());
                if (chuanHoaTenBan.contains(chuanHoaTuKhoa)) {
                    listTableSearch.add(table);
                }
            }
        }
        table_adapter.notifyDataSetChanged();
    }

    //    hàm chuẩn hóa chuôĩ
    private String chuanHoaChuoi(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
}