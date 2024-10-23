package vn.posicode.chuyende.ChiTietHoaDon;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class InvoiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        // Nhận dữ liệu từ Intent
        String invoiceTitle = getIntent().getStringExtra("invoiceTitle");
        String invoiceTime = getIntent().getStringExtra("invoiceTime");

        // Gán dữ liệu vào TextView
        TextView titleTextView = findViewById(R.id.invoiceTitleTextView);
        TextView timeTextView = findViewById(R.id.invoiceTimeTextView);

        titleTextView.setText(invoiceTitle);
        timeTextView.setText("Thời gian: " + invoiceTime);  // Thêm tiền tố "Thời gian"

        // Nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });
    }
}
