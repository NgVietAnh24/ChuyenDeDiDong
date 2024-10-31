package vn.posicode.chuyende.QuanLyHoaDon;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import vn.posicode.chuyende.ChiTietHoaDon.InvoiceDetailActivity;
import vn.posicode.chuyende.Menu.ManageActivity;
import vn.posicode.chuyende.R;

public class InvoiceListActivity extends AppCompatActivity {
    private ListView invoiceListView;
    private ArrayList<Invoice> invoiceList;
    private InvoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        ImageButton backArrowButton = findViewById(R.id.backButton);
        backArrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceListActivity.this, ManageActivity.class);
            startActivity(intent);
            finish();
        });

        invoiceListView = findViewById(R.id.invoiceListView);
        invoiceList = new ArrayList<>();

        invoiceList.add(createSampleInvoice("Hóa đơn 1", "10:30", "01/01/2023"));
        invoiceList.add(createSampleInvoice("Hóa đơn 2", "11:00", "02/01/2023"));

        adapter = new InvoiceAdapter(this, R.layout.invoice_list_item, invoiceList);
        invoiceListView.setAdapter(adapter);

        invoiceListView.setOnItemClickListener((parent, view, position, id) -> {
            Invoice selectedInvoice = invoiceList.get(position);
            Intent intent = new Intent(InvoiceListActivity.this, InvoiceDetailActivity.class);
            intent.putExtra("selectedInvoice", selectedInvoice);
            startActivity(intent);
        });
    }

    private Invoice createSampleInvoice(String title, String time, String date) {
        List<Invoice.InvoiceItem> items = new ArrayList<>();
        items.add(new Invoice.InvoiceItem("Bánh mì bò", 2, 4.0));
        items.add(new Invoice.InvoiceItem("Bánh mì heo quay", 1, 3.0));
        return new Invoice(title, time, date, items);
    }
}