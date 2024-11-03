package vn.posicode.chuyende.QuanLyHoaDon;

import android.content.Intent; import android.os.Bundle; import android.util.Log; import android.widget.ImageButton; import android.widget.ListView; import android.widget.TextView; import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task; import com.google.firebase.firestore.FirebaseFirestore; import com.google.firebase.firestore.Query; import com.google.firebase.firestore.QueryDocumentSnapshot; import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import vn.posicode.chuyende.ChiTietHoaDon.InvoiceDetailActivity; import vn.posicode.chuyende.R;

public class InvoiceListActivity extends AppCompatActivity { private static final String TAG = "InvoiceListActivity"; private static final int REQUEST_INVOICE_DETAIL = 1;

    private ListView invoiceListView;
    private ArrayList<Invoice> invoiceList;
    private InvoiceAdapter adapter;
    private FirebaseFirestore db;
    private String tableId;
    private String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_list);

        db = FirebaseFirestore.getInstance();

        // Lấy thông tin bàn từ Intent
        tableId = getIntent().getStringExtra("tableId");
        tableName = getIntent().getStringExtra("tableName");

        setupViews();
        setupListeners();

        // Load danh sách hóa đơn
        if (tableId != null && !tableId.isEmpty()) {
            loadInvoicesForTable();
        } else {
            loadAllInvoices();
        }
    }

    private void setupViews() {
        // Thiết lập tiêu đề
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(tableName != null ? "Hóa đơn của " + tableName : "Tất cả hóa đơn");

        // Thiết lập nút quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Thiết lập ListView và Adapter
        invoiceListView = findViewById(R.id.invoiceListView);
        invoiceList = new ArrayList<>();
        adapter = new InvoiceAdapter(this, R.layout.invoice_list_item, invoiceList);
        invoiceListView.setAdapter(adapter);
    }

    private void setupListeners() {
        invoiceListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "Item clicked at position: " + position);
            if (position >= 0 && position < invoiceList.size()) {
                Invoice selectedInvoice = invoiceList.get(position);
                if (selectedInvoice != null && selectedInvoice.getId() != null) {
                    Log.d(TAG, "Attempting to open invoice with ID: " + selectedInvoice.getId());
                    openInvoiceDetail(selectedInvoice.getId());
                } else {
                    Log.e(TAG, "Invalid invoice or invoice ID");
                    Toast.makeText(this, "Không thể mở chi tiết hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInvoicesForTable() {
        Log.d(TAG, "Loading invoices for table: " + tableId);
        db.collection("invoices")
                .whereEqualTo("ban_id", tableId)
                .orderBy("ngay_tao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> handleInvoiceQueryResult(task));
    }

    private void loadAllInvoices() {
        Log.d(TAG, "Loading all invoices");
        db.collection("invoices")
                .orderBy("ngay_tao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> handleInvoiceQueryResult(task));
    }

    private void handleInvoiceQueryResult(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            invoiceList.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Invoice invoice = Invoice.fromFirestore(document);
                invoice.setId(document.getId());
                loadTableInfo(invoice);
                invoiceList.add(invoice);
                Log.d(TAG, "Added invoice with ID: " + invoice.getId());
            }
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error getting documents: ", task.getException());
            Toast.makeText(this, "Lỗi khi tải danh sách hóa đơn", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTableInfo(Invoice invoice) {
        if (invoice.getBanId() != null) {
            db.collection("tables").document(invoice.getBanId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String tableName = documentSnapshot.getString("name");
                            invoice.setTableName(tableName);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error loading table info", e));
        }
    }

    private void openInvoiceDetail(String invoiceId) {
        Log.d(TAG, "Opening invoice detail with ID: " + invoiceId);
        if (invoiceId != null && !invoiceId.isEmpty()) {
            Intent intent = new Intent(this, InvoiceDetailActivity.class);
            intent.putExtra("invoiceId", invoiceId);
            Log.d(TAG, "Starting InvoiceDetailActivity...");
            startActivityForResult(intent, REQUEST_INVOICE_DETAIL);
        } else {
            Log.e(TAG, "Invalid invoice ID");
            Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVOICE_DETAIL && resultCode == RESULT_OK) {
            // Refresh danh sách sau khi quay lại từ chi tiết hóa đơn
            if (tableId != null && !tableId.isEmpty()) {
                loadInvoicesForTable();
            } else {
                loadAllInvoices();
            }
        }
    }
}