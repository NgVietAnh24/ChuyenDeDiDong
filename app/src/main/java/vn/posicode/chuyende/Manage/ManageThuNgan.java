package vn.posicode.chuyende.Manage;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.login_forgot.Login;

public class ManageThuNgan extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tn);

        // Nút quay lại
        ImageButton backArrowButton = findViewById(R.id.backtnButton);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình TableListActivity
                finish(); // Kết thúc activity hiện tại, quay về activity trước đó
            }
        });

        // Lấy các TextView từ layout
        TextView logoutTextView = findViewById(R.id.logoutThuNgan);

        // Thêm gạch dưới cho từng TextView

        logoutTextView.setPaintFlags(logoutTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        // Đăng xuất
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình login (Login)
                Intent intent = new Intent(ManageThuNgan.this, Login.class);
                startActivity(intent);
            }
        });
    }
}


