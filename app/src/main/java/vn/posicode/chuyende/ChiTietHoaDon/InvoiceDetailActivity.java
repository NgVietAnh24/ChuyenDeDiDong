package vn.posicode.chuyende.ChiTietHoaDon;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import vn.posicode.chuyende.QuanLyHoaDon.Invoice;
import vn.posicode.chuyende.R;

public class InvoiceDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Invoice selectedInvoice = (Invoice) getIntent().getSerializableExtra("selectedInvoice");

        if (selectedInvoice != null) {
            TextView titleTextView = findViewById(R.id.invoiceTitleTextView);
            TextView timeTextView = findViewById(R.id.invoiceTimeTextView);
            TextView dateTextView = findViewById(R.id.invoiceDateTextView);

            titleTextView.setText(selectedInvoice.getTitle());
            timeTextView.setText(selectedInvoice.getTime());
            dateTextView.setText(selectedInvoice.getDate());

            ListView itemsListView = findViewById(R.id.itemsListView);
            InvoiceItemAdapter adapter = new InvoiceItemAdapter(this, R.layout.invoice_item, selectedInvoice.getItems());
            itemsListView.setAdapter(adapter);
        }
    }
}