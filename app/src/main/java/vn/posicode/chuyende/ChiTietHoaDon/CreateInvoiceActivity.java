package vn.posicode.chuyende.ChiTietHoaDon;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.posicode.chuyende.QuanLyHoaDon.Invoice;
import vn.posicode.chuyende.QuanLyHoaDon.InvoiceListActivity;
import vn.posicode.chuyende.R;

public class CreateInvoiceActivity extends AppCompatActivity {
    private static final String TAG = "CreateInvoiceActivity";
    private FirebaseFirestore db;
    private String invoiceId;
    private Invoice selectedInvoice;

    private TextView titleTextView, timeTextView, dateTextView, totalTextView, amountReceivedTextView, changeTextView;
    private TextView customerNameTextView, customerPhoneTextView;
    private TextView invoiceIdTextView; // Variable for invoice ID TextView
    //private Button paymentButton;
    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        initializeViews();

        db = FirebaseFirestore.getInstance();

        invoiceId = getIntent().getStringExtra("invoiceId");
        Log.d(TAG, "Received invoice ID: " + invoiceId);

        if (invoiceId == null || invoiceId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadInvoiceDetails();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.invoiceTitleTextView);
        timeTextView = findViewById(R.id.invoiceTimeTextView);
        dateTextView = findViewById(R.id.invoiceDateTextView);
        totalTextView = findViewById(R.id.totalTextView);
        amountReceivedTextView = findViewById(R.id.amountReceivedTextView);
        changeTextView = findViewById(R.id.changeTextView);
        customerNameTextView = findViewById(R.id.customerNameTextView);
        customerPhoneTextView = findViewById(R.id.customerPhoneTextView);
        invoiceIdTextView = findViewById(R.id.invoiceIdTextView); // Initialize TextView for invoice ID
        //paymentButton = findViewById(R.id.paymentButton);
        itemsListView = findViewById(R.id.itemsListView);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        //paymentButton.setOnClickListener(v -> showPaymentDialog());
    }

    private void loadInvoiceDetails() {
        Log.d(TAG, "Loading invoice details for ID: " + invoiceId);
        db.collection("invoices").document(invoiceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            selectedInvoice = Invoice.fromFirestore(documentSnapshot);
                            if (selectedInvoice != null) {
                                // Kiểm tra nếu items đã được load từ Firestore
                                if (selectedInvoice.getItems() == null || selectedInvoice.getItems().isEmpty()) {
                                    loadInvoiceItems();
                                } else {
                                    displayInvoiceDetails();
                                }
                            } else {
                                throw new Exception("Failed to parse invoice");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing invoice: " + e.getMessage());
                            Toast.makeText(this, "Lỗi khi đọc thông tin hóa đơn", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading invoice: " + e.getMessage());
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
                    List<Invoice.InvoiceItem> items = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("ten_mon_an");
                        Long quantityLong = document.getLong("so_luong");
                        int quantity = quantityLong != null ? quantityLong.intValue() : 0;
                        Double priceDouble = document.getDouble("gia");
                        double price = priceDouble != null ? priceDouble : 0.0;

                        if (name != null) {
                            items.add(new Invoice.InvoiceItem(name, quantity, price));
                            Log.d(TAG, "Added item: " + name + ", quantity: " + quantity + ", price: " + price);
                        }
                    }

                    // Set items directly to the invoice
                    selectedInvoice.setItems(items);

                    displayInvoiceDetails();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading invoice items", e);
                    Toast.makeText(this, "Lỗi khi tải danh sách món", Toast.LENGTH_SHORT).show();
                    displayInvoiceDetails(); // Vẫn hiển thị chi tiết hóa đơn ngay cả khi không load được items
                });
    }

    private void displayInvoiceDetails() {
        if (selectedInvoice == null) {
            Log.e(TAG, "Selected invoice is null");
            Toast.makeText(this, "Lỗi hiển thị thông tin hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            titleTextView.setText(selectedInvoice.getTitle());
            invoiceIdTextView.setText("Mã hóa đơn: " + selectedInvoice.getId());
            timeTextView.setText(selectedInvoice.getTime() != null ? selectedInvoice.getTime() : "N/A");
            dateTextView.setText(selectedInvoice.getDate() != null ? selectedInvoice.getDate() : "N/A");
            totalTextView.setText(String.format("Tổng tiền: %,.0f VND", selectedInvoice.getTotal()));

            customerNameTextView.setText("Tên khách hàng: " +
                    (selectedInvoice.getCustomerName() != null ? selectedInvoice.getCustomerName() : "N/A"));
            customerPhoneTextView.setText("Số điện thoại: " +
                    (selectedInvoice.getCustomerPhone() != null ? selectedInvoice.getCustomerPhone() : "N/A"));

            // Handle note display
            TextView noteTextView = findViewById(R.id.noteTextView);
            String note = selectedInvoice.getNote();
            if (note != null && !note.isEmpty()) {
                noteTextView.setText("Ghi chú: " + note);
                noteTextView.setVisibility(View.VISIBLE);
            } else {
                noteTextView.setText("Ghi chú: Không có");
                noteTextView.setVisibility(View.VISIBLE);
            }

            String paymentStatus = selectedInvoice.getPaymentStatus();
            if (paymentStatus != null && paymentStatus.equals("Đã thanh toán")) {
                //paymentButton.setVisibility(View.GONE);
                amountReceivedTextView.setVisibility(View.VISIBLE);
                changeTextView.setVisibility(View.VISIBLE);
                amountReceivedTextView.setText(String.format("Tiền thu: %,.0f VND", selectedInvoice.getAmountReceived()));
                changeTextView.setText(String.format("Tiền dư: %,.0f VND", selectedInvoice.getChange()));
            } else {
                //paymentButton.setVisibility(View.VISIBLE);
                amountReceivedTextView.setVisibility(View.GONE);
                changeTextView.setVisibility(View.GONE);
            }

            if (selectedInvoice.getItems() != null && !selectedInvoice.getItems().isEmpty()) {
                InvoiceItemAdapter adapter = new InvoiceItemAdapter(this, R.layout.invoice_item, selectedInvoice.getItems());
                itemsListView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Không có món ăn trong hóa đơn", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying invoice details: " + e.getMessage());
            Toast.makeText(this, "Lỗi hiển thị thông tin hóa đơn", Toast.LENGTH_SHORT).show();
        }
    }

//    private void showPaymentDialog() {
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_payment);
//
//        Window window = dialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(window.getAttributes());
//            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
//            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
//            window.setAttributes(layoutParams);
//        }
//
//        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
//        EditText edtTienThu = dialog.findViewById(R.id.edtTienThu);
//        //Button btnThanhToan = dialog.findViewById(R.id.btnThanhToan);
//        TextView btnClose = dialog.findViewById(R.id.btnClose);
//        TextView totalAmountValue = dialog.findViewById(R.id.totalAmountValue);
//
//        // Format total amount according to Vietnamese locale
//        Locale vietnameseLocale = new Locale("vi", "VN");
//        NumberFormat currencyFormatter = NumberFormat.getNumberInstance(vietnameseLocale);
//        currencyFormatter.setGroupingUsed(true);
//        currencyFormatter.setMaximumFractionDigits(0);
//
//        titleTextView.setText("Tính tiền");
//        totalAmountValue.setText(currencyFormatter.format(selectedInvoice.getTotal()) + " VND");
//
//        // Apply CurrencyTextWatcher
//        CurrencyTextWatcher currencyTextWatcher = new CurrencyTextWatcher(edtTienThu);
//        edtTienThu.addTextChangedListener(currencyTextWatcher);
//
////        btnThanhToan.setOnClickListener(v -> {
////            double tienThu = currencyTextWatcher.getNumericValue();
////
////            if (tienThu > 0) {
////                if (tienThu >= selectedInvoice.getTotal()) {
////                    double tienDu = tienThu - selectedInvoice.getTotal();
////                    showConfirmPaymentDialog(tienThu, tienDu, dialog, currencyTextWatcher);
////                } else {
////                    Toast.makeText(this, "Số tiền không đủ", Toast.LENGTH_SHORT).show();
////                }
////            } else {
////                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        // Use TextView as close button
//        btnClose.setOnClickListener(v -> dialog.dismiss());
//
//        dialog.show();
//    }

    private void showConfirmPaymentDialog(double tienThu, double tienDu,
                                          Dialog originalDialog,
                                          CurrencyTextWatcher currencyTextWatcher) {
        Locale vietnameseLocale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getNumberInstance(vietnameseLocale);
        currencyFormatter.setGroupingUsed(true);
        currencyFormatter.setMaximumFractionDigits(0);

        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage(String.format(
                        "Tiền thu: %s VND\nTổng tiền: %s VND\nTiền dư: %s VND",
                        currencyFormatter.format(tienThu),
                        currencyFormatter.format(selectedInvoice.getTotal()),
                        currencyFormatter.format(tienDu)
                ))
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    selectedInvoice.setAmountReceived(tienThu);
                    updateInvoicePayment(tienThu);
                    originalDialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateInvoicePayment(double amountReceived) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("tinh_trang", "Đã thanh toán");
        updates.put("tien_thu", amountReceived);
        updates.put("tien_du", amountReceived - selectedInvoice.getTotal());

        // Keep the old note if it exists
        if (selectedInvoice.getNote() != null && !selectedInvoice.getNote().isEmpty()) {
            updates.put("ghi_chu", selectedInvoice.getNote());
        }

        db.collection("invoices").document(selectedInvoice.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    selectedInvoice.setPaymentStatus("Đã thanh toán");
                    selectedInvoice.setAmountReceived(amountReceived);
                    displayInvoiceDetails();
                    updateTableStatus("Trống");

                    Intent intent = new Intent(CreateInvoiceActivity.this, InvoiceListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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

    @Override
    public void onBackPressed() {
        // Set result if needed
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}