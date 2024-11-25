package vn.posicode.chuyende.activities.homes;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.DanhSachChonMon;
import vn.posicode.chuyende.activities.login_forgot.Login;
import vn.posicode.chuyende.adapters.BanChonAdapter;
import vn.posicode.chuyende.models.Ban;

public class HomeNhanVien extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private List<Ban> list;
    private List<Ban> listBan;

    private BanChonAdapter banAdapter;
    private EditText edtSearch;
    private ImageView btnLogout;
    private RecyclerView listTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_nhan_vien_layout);
        Event();

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listTable.setLayoutManager(layoutManager);

        list = new ArrayList<>();

        listBan = new ArrayList<>(list);// lấy danh sách bàn gốc

        banAdapter = new BanChonAdapter(list, new BanChonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ban ban) {
                Intent intent = new Intent(HomeNhanVien.this, DanhSachChonMon.class);
                intent.putExtra("id", ban.getId());
                intent.putExtra("name", ban.getName());
                startActivity(intent);
            }
        }, this);
        listTable.setAdapter(banAdapter);
        Log.d("JJ", "getData: " + list);
        docDulieu();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // Chuyển người dùng về màn hình đăng nhập
                Intent intent = new Intent(HomeNhanVien.this, Login.class);
                startActivity(intent);
                finish();  // Đóng màn hình hiện tại
            }
        });

        // Xử lý tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });


    }

    private void filter(String text) {
        list.clear();
        if (text.isEmpty()) {
            list.addAll(listBan);
        } else {
            for (Ban ban : listBan) {
                if (ban.getName().toLowerCase().contains(text.toLowerCase())) {
                    list.add(ban);
                }
            }
        }
        banAdapter.notifyDataSetChanged();
    }

    public void docDulieu() {
        firestore.collection("tables")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d(TAG, "Error getting documents: ", error);
                        return;
                    }
                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Ban tableData = document.toObject(Ban.class);
                            list.add(tableData);
                        }
                        // Sắp xếp list tăng dần theo số thứ tự của bàn
                        Collections.sort(list, new Comparator<Ban>() {
                            @Override
                            public int compare(Ban ban1, Ban ban2) {
                                try {
                                    // Lấy số bàn từ tên và chuyển thành số nguyên
                                    int num1 = Integer.parseInt(ban1.getName().replaceAll("[^0-9]", ""));
                                    int num2 = Integer.parseInt(ban2.getName().replaceAll("[^0-9]", ""));
                                    return Integer.compare(num1, num2);
                                } catch (NumberFormatException e) {
                                    return ban1.getName().compareTo(ban2.getName());
                                }
                            }
                        });
                        listBan.clear();
                        listBan.addAll(list);

                        // Sắp xếp listBan tăng dần theo số thứ tự của bàn
                        Collections.sort(listBan, new Comparator<Ban>() {
                            @Override
                            public int compare(Ban ban1, Ban ban2) {
                                try {
                                    // Lấy số bàn từ tên và chuyển thành số nguyên
                                    int num1 = Integer.parseInt(ban1.getName().replaceAll("[^0-9]", ""));
                                    int num2 = Integer.parseInt(ban2.getName().replaceAll("[^0-9]", ""));
                                    return Integer.compare(num1, num2);
                                } catch (NumberFormatException e) {
                                    return ban1.getName().compareTo(ban2.getName());
                                }
                            }
                        });

                        Log.d(TAG, "Updated and sorted documents: " + list.size());
                        banAdapter.notifyDataSetChanged();
                    }
                });
    }


    public void Event() {
        btnLogout = findViewById(R.id.btnSkill);
        listTable = findViewById(R.id.listTableNV);
        edtSearch = findViewById(R.id.edtSearch);
    }

}