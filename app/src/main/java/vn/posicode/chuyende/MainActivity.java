package vn.posicode.chuyende;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;

import vn.posicode.chuyende.activities.Login;
import vn.posicode.chuyende.activities.QLNhanVien;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView btnLogout, btn_ql_taikhoan;
    private ImageView imgSkill, btnBack;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Event();

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                Intent intent = new Intent(MainActivity.this, QLNhanVien.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(view -> {
            mAuth.signOut();
            // Chuyển người dùng về màn hình đăng nhập
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();  // Đóng màn hình hiện tại
        });
    }

    public void Event() {
        drawerLayout = findViewById(R.id.drawer_layout);
        imgSkill = findViewById(R.id.btnSkill);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnlogout);
        btn_ql_taikhoan = findViewById(R.id.btn_ql_taikhoan);
    }

}
