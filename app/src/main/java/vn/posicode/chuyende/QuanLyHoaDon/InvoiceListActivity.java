package vn.posicode.chuyende.QuanLyHoaDon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import vn.posicode.chuyende.ChiTietHoaDon.InvoiceDetailActivity;
import vn.posicode.chuyende.Menu.ManageActivity; // Import ManageActivity
import vn.posicode.chuyende.R;

public class InvoiceListActivity extends AppCompatActivity {

    private ListView invoiceListView;
    private ArrayList<Invoice> invoiceList;
    private InvoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        // Nút quay lại
        ImageButton backArrowButton = findViewById(R.id.backButton);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo intent để quay lại màn hình ManageActivity
                Intent intent = new Intent(InvoiceListActivity.this, ManageActivity.class);
                startActivity(intent); // Bắt đầu ManageActivity
                finish(); // Hoặc bạn có thể để lại finish() để đóng InvoiceListActivity
            }
        });

        // Lấy ListView từ layout
        invoiceListView = findViewById(R.id.invoiceListView);

        // Tạo danh sách hóa đơn
        invoiceList = new ArrayList<>();
        invoiceList.add(new Invoice("Hóa đơn 1", "12:00"));
        invoiceList.add(new Invoice("Hóa đơn 2", "12:30"));
        invoiceList.add(new Invoice("Hóa đơn 3", "13:00"));
        invoiceList.add(new Invoice("Hóa đơn 4", "13:30"));
        invoiceList.add(new Invoice("Hóa đơn 5", "14:00"));
        invoiceList.add(new Invoice("Hóa đơn 6", "14:30"));

        // Tạo adapter và thiết lập cho ListView
        adapter = new InvoiceAdapter(this, R.layout.invoice_list_item, invoiceList);
        invoiceListView.setAdapter(adapter);

        // Đặt sự kiện khi nhấn vào item trong ListView
        invoiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy hóa đơn tại vị trí được nhấn
                Invoice selectedInvoice = invoiceList.get(position);

                // Tạo intent để chuyển sang InvoiceDetailActivity
                Intent intent = new Intent(InvoiceListActivity.this, InvoiceDetailActivity.class);

                // Truyền dữ liệu (tiêu đề và thời gian hóa đơn) qua intent
                intent.putExtra("invoiceTitle", selectedInvoice.getTitle());
                intent.putExtra("invoiceTime", selectedInvoice.getTime());

                // Bắt đầu InvoiceDetailActivity
                startActivity(intent);
            }
        });
    }
}
