package vn.posicode.chuyende.activities.homes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.DanhSachChonMon;
import vn.posicode.chuyende.activities.login_forgot.Login;

import vn.posicode.chuyende.activities.manages.InvoiceListActivity;
import vn.posicode.chuyende.activities.manages.QLBan;
import vn.posicode.chuyende.activities.manages.QLMonAn;
import vn.posicode.chuyende.activities.manages.QLNhanVien;
import vn.posicode.chuyende.activities.manages.QLThongKe;
import vn.posicode.chuyende.adapters.BanChonAdapter;
import vn.posicode.chuyende.models.Ban;

public class HomeQuanLy extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    private List<Ban> list;
    private List<Ban> listBan;

    private BanChonAdapter banAdapter;
    private EditText edtSearch;
    private TextView btnLogout, btn_ql_taikhoan, btn_ql_hoadon, btn_ql_monan, btn_ql_ban, btn_thongke;
    private ImageView imgSkill, btnBack;
    private DrawerLayout drawerLayout;
    private RecyclerView listTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_quan_ly_layout);
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
                Intent intent = new Intent(HomeQuanLy.this, DanhSachChonMon.class);
                intent.putExtra("id", ban.getId());
                intent.putExtra("name", ban.getName());
                startActivity(intent);
            }
        }, this);
        listTable.setAdapter(banAdapter);
        Log.d("JJ", "getData: " + list);
        docDulieu();
        // Xử lý sự kiện mở/đóng DrawerLayout
        imgSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        btn_ql_taikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeQuanLy.this, QLNhanVien.class);
                startActivity(intent);
            }
        });
        btn_ql_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeQuanLy.this, QLBan.class);
                startActivity(intent);
            }
        });
        btn_ql_hoadon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeQuanLy.this, InvoiceListActivity.class);
                startActivity(intent);
            }
        });
        btn_ql_monan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeQuanLy.this, QLMonAn.class);
                startActivity(intent);
            }
        });
        btn_thongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeQuanLy.this, QLThongKe.class);
                startActivity(intent);
            }
        });


        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(view -> {
            mAuth.signOut();
            // Chuyển người dùng về màn hình đăng nhập
            Intent intent = new Intent(HomeQuanLy.this, Login.class);
            startActivity(intent);
            finish();  // Đóng màn hình hiện tại
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

                        // Sắp xếp danh sách bàn theo tên
                        list.sort((ban1, ban2) -> {
                            int number1 = Integer.parseInt(ban1.getName().replaceAll("\\D+", ""));
                            int number2 = Integer.parseInt(ban2.getName().replaceAll("\\D+", ""));
                            return Integer.compare(number1, number2);
                        });

                        listBan.clear(); // Xóa dữ liệu cũ trong listBan
                        listBan.addAll(list); // Cập nhật lại listBan với dữ liệu mới nhất
                        Log.d(TAG, "Updated documents: " + list.size());
                        banAdapter.notifyDataSetChanged();
                    }
                });
    }


    public void Event() {
        drawerLayout = findViewById(R.id.drawer_layout);
        imgSkill = findViewById(R.id.btnSkill);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnlogout);
        btn_ql_taikhoan = findViewById(R.id.btn_ql_taikhoan);
        btn_ql_ban = findViewById(R.id.btn_ql_ban);
        btn_ql_hoadon = findViewById(R.id.btn_ql_hoadon);
        btn_ql_monan = findViewById(R.id.btn_ql_monan);
        btn_thongke = findViewById(R.id.btn_thongke);
        listTable = findViewById(R.id.listTable);
        edtSearch = findViewById(R.id.edtSearch);
    }

}