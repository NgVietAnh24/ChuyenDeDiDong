package vn.posicode.chuyende.ChiTietHoaDon;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import vn.posicode.chuyende.QuanLyHoaDon.Invoice;
import vn.posicode.chuyende.R;

public class InvoiceDetailActivity extends AppCompatActivity {
    private static final String TAG = "InvoiceDetailActivity";
    private FirebaseFirestore db;
    private String invoiceId;
    private Invoice selectedInvoice;

    private TextView titleTextView, timeTextView, dateTextView, totalTextView, amountReceivedTextView, changeTextView;
    private Button paymentButton;
    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        initializeViews();

        db = FirebaseFirestore.getInstance();

        invoiceId = getIntent().getStringExtra("invoiceId");
        Log.d(TAG, "Received invoice ID: " + invoiceId);

        if (invoiceId != null && !invoiceId.isEmpty()) {
            loadInvoiceDetails();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.invoiceTitleTextView);
        timeTextView = findViewById(R.id.invoiceTimeTextView);
        dateTextView = findViewById(R.id.invoiceDateTextView);
        totalTextView = findViewById(R.id.totalTextView);
        amountReceivedTextView = findViewById(R.id.amountReceivedTextView);
        changeTextView = findViewById(R.id.changeTextView);
        paymentButton = findViewById(R.id.paymentButton);
        itemsListView = findViewById(R.id.itemsListView);

        paymentButton.setOnClickListener(v -> showPaymentDialog());
    }

    private void loadInvoiceDetails() {
        Log.d(TAG, "Loading invoice details for ID: " + invoiceId);
        db.collection("invoices").document(invoiceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        selectedInvoice = Invoice.fromFirestore(documentSnapshot);
                        selectedInvoice.setId(documentSnapshot.getId());
                        selectedInvoice.setBanId(documentSnapshot.getString("ban_id"));
                        selectedInvoice.setDate(documentSnapshot.getString("ngay_tao"));
                        selectedInvoice.setTime(documentSnapshot.getString("gio_tao"));
                        selectedInvoice.setTotal(documentSnapshot.getDouble("tong_tien"));
                        selectedInvoice.setPaymentStatus(documentSnapshot.getString("tinh_trang"));

                        loadInvoiceItems();
                    } else {
                        Log.e(TAG, "Invoice document does not exist");
                        Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading invoice", e);
                    Toast.makeText(this, "Lỗi khi tải thông tin hóa đơn", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void loadInvoiceItems() {
        Log.d(TAG, "Loading invoice items for invoice ID: " + invoiceId);
        db.collection("invoice_items")
                .whereEqualTo("hoa_don_id", invoiceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e(TAG, "No items found for this invoice");
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String name = document.getString("ten_mon_an");
                            Long quantityLong = document.getLong("so_luong");
                            int quantity = quantityLong != null ? quantityLong.intValue() : 0;
                            Double priceDouble = document.getDouble("gia");
                            double price = priceDouble != null ? priceDouble : 0.0;

                            if (name != null) {
                                selectedInvoice.addItem(new Invoice.InvoiceItem(name, quantity, price));
                                Log.d(TAG, "Added item: " + name + ", quantity: " + quantity + ", price: " + price);
                            }
                        }
                        Log.d(TAG, "Items loaded successfully: " + selectedInvoice.getItems());
                        displayInvoiceDetails();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading invoice items", e);
                    Toast.makeText(this, "Lỗi khi tải danh sách món", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayInvoiceDetails() {
        if (selectedInvoice != null) {
            titleTextView.setText("Chi tiết " + selectedInvoice.getTitle());
            timeTextView.setText(selectedInvoice.getTime());
            dateTextView.setText(selectedInvoice.getDate());
            totalTextView.setText("Tổng tiền: " + String.format("%.2f$", selectedInvoice.getTotal()));

            if (selectedInvoice.getPaymentStatus().equals("Đã thanh toán")) {
                paymentButton.setVisibility(View.GONE);
                amountReceivedTextView.setText("Tiền thu: " + String.format("%.2f$", selectedInvoice.getAmountReceived()));
                changeTextView.setText("Tiền dư: " + String.format("%.2f$", selectedInvoice.getChange()));
                amountReceivedTextView.setVisibility(View.VISIBLE);
                changeTextView.setVisibility(View.VISIBLE);
            } else {
                paymentButton.setVisibility(View.VISIBLE);
                amountReceivedTextView.setVisibility(View.GONE);
                changeTextView.setVisibility(View.GONE);
            }

            InvoiceItemAdapter adapter = new InvoiceItemAdapter(this, R.layout.invoice_item, selectedInvoice.getItems());
            itemsListView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Invoice is null");
        }
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_payment);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(layoutParams);
        }

        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        EditText edtTienThu = dialog.findViewById(R.id.edtTienThu); Button btnThanhToan = dialog.findViewById(R.id.btnThanhToan);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        TextView totalAmountValue = dialog.findViewById(R.id.totalAmountValue);

        titleTextView.setText("Tính tiền");
        totalAmountValue.setText(String.format("%.2f$", selectedInvoice.getTotal()));

        btnThanhToan.setOnClickListener(v -> {
            String tienThuStr = edtTienThu.getText().toString();
            if (!tienThuStr.isEmpty()) {
                double tienThu = Double.parseDouble(tienThuStr);
                if (tienThu >= selectedInvoice.getTotal()) {
                    selectedInvoice.setAmountReceived(tienThu);
                    updateInvoicePayment(tienThu);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Số tiền không đủ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateInvoicePayment(double amountReceived) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("tinh_trang", "Đã thanh toán");
        updates.put("tien_thu", amountReceived);
        updates.put("tien_du", amountReceived - selectedInvoice.getTotal());

        db.collection("invoices").document(selectedInvoice.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    selectedInvoice.setPaymentStatus("Đã thanh toán");
                    selectedInvoice.setAmountReceived(amountReceived);
                    displayInvoiceDetails();
                    updateTableStatus("Trống");
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating payment", e);
                    Toast.makeText(this, "Lỗi khi cập nhật thanh toán", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateTableStatus(String status) {
        if (selectedInvoice != null) {
            db.collection("tables")
                    .document(selectedInvoice.getBanId())
                    .update("status", status)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Trạng thái bàn đã được cập nhật thành " + status);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi khi cập nhật trạng thái bàn", e);
                    });
        }
    }
}