package vn.posicode.chuyende.Menu;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.QuanLyBan.MainActivity;
import vn.posicode.chuyende.R;

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
    }
}
