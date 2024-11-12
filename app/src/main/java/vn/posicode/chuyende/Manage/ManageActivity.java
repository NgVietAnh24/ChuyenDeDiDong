package vn.posicode.chuyende.Manage;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.QuanLyBan.MainActivity;
import vn.posicode.chuyende.QuanLyHoaDon.InvoiceListActivity;
import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.ThongKeActivity;
import vn.posicode.chuyende.activities.login_forgot.Login;
import vn.posicode.chuyende.activities.manages.QLNhanVien;

public class ManageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        // Nút quay lại
        ImageButton backArrowButton = findViewById(R.id.backButton);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình TableListActivity
                finish(); // Kết thúc activity hiện tại, quay về activity trước đó
            }
        });

        // Quản lý bàn
        TextView manageTable = findViewById(R.id.manageTableTextView);
        manageTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình quản lý bàn
                Intent intent = new Intent(ManageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Lấy các TextView từ layout
        TextView manageAccountTextView = findViewById(R.id.manageAccountTextView);
        TextView manageDishTextView = findViewById(R.id.manageDishTextView);
        TextView manageTableTextView = findViewById(R.id.manageTableTextView);
        TextView manageInvoiceTextView = findViewById(R.id.manageInvoiceTextView);
        TextView statisticsTextView = findViewById(R.id.statisticsTextView);
        TextView logoutTextView = findViewById(R.id.logoutTextView);

        // Thêm gạch dưới cho từng TextView
        manageAccountTextView.setPaintFlags(manageAccountTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        manageDishTextView.setPaintFlags(manageDishTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        manageTableTextView.setPaintFlags(manageTableTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        manageInvoiceTextView.setPaintFlags(manageInvoiceTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        statisticsTextView.setPaintFlags(statisticsTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        logoutTextView.setPaintFlags(logoutTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        // Quản lý hóa đơn
        manageInvoiceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình danh sách hóa đơn (InvoiceListActivity)
                Intent intent = new Intent(ManageActivity.this, InvoiceListActivity.class);
                startActivity(intent);
            }
        });

        // Quản lý nhân viên
        manageAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình danh sách hóa đơn (QLNhanVien)
                Intent intent = new Intent(ManageActivity.this, QLNhanVien.class);
                startActivity(intent);
            }
        });

        // Đăng xuất
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình login (Login)
                Intent intent = new Intent(ManageActivity.this, Login.class);
                startActivity(intent);
            }
        });

        // Thống kê
        statisticsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình login (Thống kê)
                Intent intent = new Intent(ManageActivity.this, ThongKeActivity.class);
                startActivity(intent);
            }
        });
    }
}
