package vn.posicode.chuyende.DanhSachMonAn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.posicode.chuyende.ChiTietHoaDon.InvoiceDetailActivity;
import vn.posicode.chuyende.QuanLyHoaDon.Invoice;
import vn.posicode.chuyende.QuanLyHoaDon.InvoiceListActivity;
import vn.posicode.chuyende.R;

public class SelectedFoodActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private LinearLayout selectedFoodLayout;
    private Button btnCancelOrder, btnPay;
    private EditText etNote;
    private TextView tvTableName;
    private ArrayList<Food> selectedFoodList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupListeners();

        selectedFoodList = getIntent().getParcelableArrayListExtra("selectedFoodList");
        String tableName = getIntent().getStringExtra("tableName");
        tvTableName.setText("Tên bàn: " + tableName);

        if (selectedFoodList != null) {
            for (Food food : selectedFoodList) {
                addFoodView(food);
            }
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnQuayLai);
        selectedFoodLayout = findViewById(R.id.selected_food_list);
        btnCancelOrder = findViewById(R.id.btnHuyDon);
        btnPay = findViewById(R.id.btnThanhToan);
        etNote = findViewById(R.id.etGhiChu);
        tvTableName = findViewById(R.id.tvTieuDe);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelOrder.setOnClickListener(v -> {
            updateTableStatus("Trống");
            Toast.makeText(this, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
            finish();
        });
        btnPay.setOnClickListener(v -> createAndShowInvoice());
    }

    private void addFoodView(Food food) {
        View foodView = LayoutInflater.from(this).inflate(R.layout.selected_food_item, selectedFoodLayout, false);
        foodView.setTag(food.getName());

        ImageView imgFood = foodView.findViewById(R.id.imgMonAn);
        TextView tvFoodName = foodView.findViewById(R.id.tvTenMonAn);
        TextView tvFoodPrice = foodView.findViewById(R.id.tvGiaMonAn);
        TextView tvStatus = foodView.findViewById(R.id.tvTrangThai);
        TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);

        imgFood.setImageResource(food.getImageResId());
        tvFoodName.setText(food.getName());
        tvFoodPrice.setText(food.getPrice());
        tvStatus.setText("Chưa được đặt.");
        tvQuantity.setText("1");

        setupFoodViewListeners(foodView, food, tvStatus, tvQuantity);
        selectedFoodLayout.addView(foodView);
    }

    private void setupFoodViewListeners(View foodView, Food food, TextView tvStatus, TextView tvQuantity) {
        foodView.findViewById(R.id.btnChonLam).setOnClickListener(v -> {
            tvStatus.setText("Đã chọn làm.");
            Toast.makeText(this, "Đã chọn làm món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnDaLay).setOnClickListener(v -> {
            tvStatus.setText("Đã lấy.");
            Toast.makeText(this, "Đã lấy món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnXoa).setOnClickListener(v -> {
            selectedFoodLayout.removeView(foodView);
            selectedFoodList.remove(food);
            Toast.makeText(this, "Đã xóa món " + food.getName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnTang).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString()) + 1;
            tvQuantity.setText(String.valueOf(quantity));
        });

        foodView.findViewById(R.id.btnGiam).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 1) {
                tvQuantity.setText(String.valueOf(quantity - 1));
            }
        });
    }

    private void createAndShowInvoice() {
        String tableName = tvTableName.getText().toString().replace("Tên bàn: ", "");

        db.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot tableDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String tableId = tableDoc.getId();

                        // Tạo hóa đơn mới và lấy ID
                        createInvoiceInFirestore(tableDoc, invoice -> {
                            // Sau khi tạo hóa đơn thành công, chuyển sang màn hình chi tiết hóa đơn
                            Intent intent = new Intent(this, InvoiceDetailActivity.class);
                            intent.putExtra("invoiceId", invoice.getId());
                            startActivity(intent);
                            finish();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi tìm thông tin bàn", e);
                    Toast.makeText(this, "Có lỗi xảy ra khi tìm thông tin bàn", Toast.LENGTH_SHORT).show();
                });
    }

    private void createInvoiceInFirestore(DocumentSnapshot tableDoc, OnInvoiceCreatedListener listener) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();

        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("ban_id", tableDoc.getId());
        invoiceData.put("nv_id", "1");
        invoiceData.put("ten_khach_hang", tableDoc.getString("reservationName"));
        invoiceData.put("so_dt", tableDoc.getString("reservationPhone"));
        invoiceData.put("ngay_tao", dateFormat.format(now));
        invoiceData.put("gio_tao", timeFormat.format(now));
        invoiceData.put("tinh_trang", "Chưa thanh toán");
        invoiceData.put("ghi_chu", etNote.getText().toString());

        double totalAmount = calculateTotalAmount();
        invoiceData.put("tong_tien", totalAmount);

        db.collection("invoices")
                .add(invoiceData)
                .addOnSuccessListener(documentReference -> {
                    String invoiceId = documentReference.getId();
                    addInvoiceItems(invoiceId, totalAmount);

                    // Tạo đối tượng Invoice và gọi callback
                    Invoice invoice = new Invoice();
                    invoice.setId(invoiceId);
                    invoice.setBanId(tableDoc.getId());
                    invoice.setDate(dateFormat.format(now));
                    invoice.setTime(timeFormat.format(now));
                    invoice.setTotal(totalAmount);
                    invoice.setPaymentStatus("Chưa thanh toán");

                    if (listener != null) {
                        listener.onInvoiceCreated(invoice);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateInvoice", "Error creating invoice", e);
                    Toast.makeText(this, "Có lỗi xảy ra khi tạo hóa đơn", Toast.LENGTH_SHORT).show();
                });
    }

    // Interface để callback khi tạo hóa đơn xong
    interface OnInvoiceCreatedListener {
        void onInvoiceCreated(Invoice invoice);
    }
    private double calculateTotalAmount() {
        double totalAmount = 0;
        for (Food food : selectedFoodList) {
            View foodView = selectedFoodLayout.findViewWithTag(food.getName());
            if (foodView != null) {
                TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);
                int quantity = Integer.parseInt(tvQuantity.getText().toString());
                double price = Double.parseDouble(food.getPrice().replace("$", ""));
                totalAmount += price * quantity;
            }
        }
        return totalAmount;
    }

    private void addInvoiceItems(String invoiceId, double totalAmount) {
        for (Food food : selectedFoodList) {
            View foodView = selectedFoodLayout.findViewWithTag(food.getName());
            if (foodView != null) {
                TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);
                int quantity = Integer.parseInt(tvQuantity.getText().toString());
                double price = Double.parseDouble(food.getPrice().replace("$", ""));
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("hoa_don_id", invoiceId);
                itemData.put("ten_mon_an", food.getName());
                itemData.put("so_luong", quantity);
                itemData.put("gia", price);

                db.collection("invoice_items")
                        .add(itemData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "Chi tiết hóa đơn đã được tạo thành công");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Lỗi khi tạo chi tiết hóa đơn", e);
                        });
            }
        }
    }

    private void updateTableStatus(String status) {
        String tableName = tvTableName.getText().toString().replace("Tên bàn: ", "");
        db.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot tableDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String documentId = tableDoc.getId();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("status ", status);

                        db.collection("tables")
                                .document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Trạng thái bàn đã được cập nhật thành " + status);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Lỗi khi cập nhật trạng thái bàn", e);
                                    Toast.makeText(this, "Có lỗi xảy ra khi cập nhật trạng thái bàn", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi tìm thông tin bàn", e);
                    Toast.makeText(this, "Có lỗi xảy ra khi tìm thông tin bàn", Toast.LENGTH_SHORT).show();
                });
    }
}