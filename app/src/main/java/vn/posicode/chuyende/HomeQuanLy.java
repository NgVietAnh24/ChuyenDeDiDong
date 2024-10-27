package vn.posicode.chuyende;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.activities.Login;
import vn.posicode.chuyende.activities.QLNhanVien;
import vn.posicode.chuyende.adapters.BanAdapter;
import vn.posicode.chuyende.models.Ban;

public class HomeQuanLy extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    List<Ban> list;

    private BanAdapter banAdapter;

    private TextView btnLogout, btn_ql_taikhoan;
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
        banAdapter = new BanAdapter(list, new BanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ban ban) {

            }
        });
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

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(view -> {
            mAuth.signOut();
            // Chuyển người dùng về màn hình đăng nhập
            Intent intent = new Intent(HomeQuanLy.this, Login.class);
            startActivity(intent);
            finish();  // Đóng màn hình hiện tại
        });
    }

    public void docDulieu() {
        firestore.collection("tables")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ban tableData = document.toObject(Ban.class);
                                list.add(tableData);
                                Log.d("AAA", "Getting documents: " + tableData);
                            }
                            Log.d("AAA", "Getting documents: ");
                            banAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void Event() {
        drawerLayout = findViewById(R.id.drawer_layout);
        imgSkill = findViewById(R.id.btnSkill);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnlogout);
        btn_ql_taikhoan = findViewById(R.id.btn_ql_taikhoan);
        listTable = findViewById(R.id.listTable);
    }

}
