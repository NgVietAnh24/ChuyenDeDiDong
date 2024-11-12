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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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
import vn.posicode.chuyende.R;

public class SelectedFoodActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private LinearLayout selectedFoodLayout;
    private Button btnCancelOrder, btnPay, btnMakeAll;
    private EditText etNote;
    private TextView tvTableName;
    private ArrayList<SelectedFood> selectedFoodList;
    private FirebaseFirestore db;
    private String tableId;
    private String tableName;
    private SelectedFoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(this).get(SelectedFoodViewModel.class);
        initializeViews();
        setupListeners();

        // Retrieve the list of selected foods and table name
        ArrayList<Food> originalFoodList = getIntent().getParcelableArrayListExtra("selectedFoodList");
        tableName = getIntent().getStringExtra("tableName");
        tvTableName.setText("Tên bàn: " + tableName);

        // Initialize the selected food list
        selectedFoodList = viewModel.getSelectedFoodList().getValue();
        if (selectedFoodList == null) {
            selectedFoodList = new ArrayList<>();
        }

        // Convert Food to SelectedFood
        if (originalFoodList != null && !originalFoodList.isEmpty()) {
            for (Food food : originalFoodList) {
                SelectedFood selectedFood = new SelectedFood(
                        food.getId(),
                        "", // tableId will be set later
                        food.getName(),
                        food.getPrice(),
                        food.getImage()
                );
                selectedFoodList.add(selectedFood);
                addFoodView(selectedFood);
            }
            viewModel.setSelectedFoodList(selectedFoodList); // Set the list in ViewModel
        } else {
            // Load previously selected foods from ViewModel
            selectedFoodList = viewModel.getSelectedFoodList().getValue();
            if (selectedFoodList != null) {
                for (SelectedFood food : selectedFoodList) {
                    addFoodView(food);
                }
            }
        }

        // Set tableId
        findAndSetTableId();
    }

    private void findAndSetTableId() {
        db.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot tableDoc = queryDocumentSnapshots.getDocuments().get(0);
                        tableId = tableDoc.getId();

                        // Save selected foods to Firestore
                        if (!selectedFoodList.isEmpty()) {
                            for (SelectedFood selectedFood : selectedFoodList) {
                                selectedFood.setTableId(tableId);
                            }
                            saveSelectedFoodsToFirebase();
                        } else {
                            loadSelectedFoodsFromFirebase();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding table info", e);
                    Toast.makeText(this, "Có lỗi xảy ra khi tìm thông tin bàn", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSelectedFoodsToFirebase() {
        if (tableId != null && selectedFoodList != null && !selectedFoodList.isEmpty()) {
            db.collection("selected_foods")
                    .whereEqualTo("tableId", tableId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            doc.getReference().delete();
                        }

                        for (SelectedFood selectedFood : selectedFoodList) {
                            selectedFood.setTableId(tableId);
                            db.collection("selected_foods")
                                    .add(selectedFood.toMap())
                                    .addOnSuccessListener(documentReference -> {
                                        selectedFood.setId(documentReference.getId());
                                        Log.d("SaveSelectedFood", "Saved food: " + selectedFood.getFoodName());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SaveSelectedFood", "Error saving food", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SaveSelectedFood", "Error querying selected foods", e);
                    });
        }
    }

    private void loadSelectedFoodsFromFirebase() {
        if (tableId != null) {
            db.collection("selected_foods")
                    .whereEqualTo("tableId", tableId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        selectedFoodList.clear();
                        selectedFoodLayout.removeAllViews();

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            SelectedFood selectedFood = SelectedFood.fromDocumentSnapshot(doc);
                            selectedFoodList.add(selectedFood);
                            addFoodView(selectedFood);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoadSelectedFoods", "Error loading selected foods", e);
                    });
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnQuayLai);
        selectedFoodLayout = findViewById(R.id.selected_food_list);
        btnCancelOrder = findViewById(R.id.btnHuyDon);
        btnPay = findViewById(R.id.btnThanhToan);
        btnMakeAll = findViewById(R.id.btnLamTatCa);
        etNote = findViewById(R.id.etGhiChu);
        tvTableName = findViewById(R.id.tvTieuDe);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            viewModel.setSelectedFoodList(selectedFoodList);
            saveSelectedFoodsToFirebase();
            finish();
        });

        btnCancelOrder.setOnClickListener(v -> {
            updateTableStatus("Trống");
            Toast.makeText(this, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnPay.setOnClickListener(v -> createAndShowInvoice());

        btnMakeAll.setOnClickListener(v -> {
            for (int i = 0; i < selectedFoodLayout.getChildCount(); i++) {
                View foodView = selectedFoodLayout.getChildAt(i);
                TextView tvStatus = foodView.findViewById(R.id.tvTrangThai);
                tvStatus.setText("Đã chọn làm");
            }
            Toast.makeText(this, "Đã chọn làm tất cả các món", Toast.LENGTH_SHORT).show();
        });
    }

    private void addFoodView(SelectedFood selectedFood) {
        View foodView = LayoutInflater.from(this).inflate(R.layout.selected_food_item, selectedFoodLayout, false);
        foodView.setTag(selectedFood.getFoodId());

        ImageView imgFood = foodView.findViewById(R.id.imgMonAn);
        TextView tvFoodName = foodView.findViewById(R.id.tvTenMonAn);
        TextView tvFoodPrice = foodView.findViewById(R.id.tvGiaMonAn);
        TextView tvStatus = foodView.findViewById(R.id.tvTrangThai);
        TextView tvQuantity = foodView.findViewById(R.id.tvSoLuong);

        Glide.with(this)
                .load(selectedFood.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(imgFood);

        tvFoodName.setText(selectedFood.getFoodName());
        tvFoodPrice.setText(String.format("%,d VNĐ", (long)(selectedFood.getPrice())));
        tvStatus.setText(selectedFood.getStatus());
        tvQuantity.setText(String.valueOf(selectedFood.getQuantity()));

        setupFoodViewListeners(foodView, selectedFood, tvStatus, tvQuantity);
        selectedFoodLayout.addView(foodView);
    }

    private void setupFoodViewListeners(View foodView, SelectedFood selectedFood, TextView tvStatus, TextView tvQuantity) {
        foodView.findViewById(R.id.btnChonLam).setOnClickListener(v -> {
            selectedFood.setStatus("Đã chọn làm");
            tvStatus.setText("Đã chọn làm");
            updateSelectedFoodInFirestore(selectedFood);
            Toast.makeText(this, "Đã chọn làm món " + selectedFood.getFoodName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnDaLay).setOnClickListener(v -> {
            selectedFood.setStatus("Đã lấy");
            tvStatus.setText("Đã lấy");
            updateSelectedFoodInFirestore(selectedFood);
            Toast.makeText(this, "Đã lấy món " + selectedFood .getFoodName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnXoa).setOnClickListener(v -> {
            selectedFoodLayout.removeView(foodView);
            selectedFoodList.remove(selectedFood);
            if (selectedFood.getId() != null) {
                db.collection("selected_foods").document(selectedFood.getId()).delete();
            }
            Toast.makeText(this, "Đã xóa món " + selectedFood.getFoodName(), Toast.LENGTH_SHORT).show();
        });

        foodView.findViewById(R.id.btnTang).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString()) + 1;
            tvQuantity.setText(String.valueOf(quantity));
            selectedFood.setQuantity(quantity);
            updateSelectedFoodInFirestore(selectedFood);
        });

        foodView.findViewById(R.id.btnGiam).setOnClickListener(v -> {
            int quantity = Integer.parseInt(tvQuantity.getText().toString());
            if (quantity > 1) {
                tvQuantity.setText(String.valueOf(quantity - 1));
                selectedFood.setQuantity(quantity - 1);
                updateSelectedFoodInFirestore(selectedFood);
            }
        });
    }

    private void createAndShowInvoice() {
        db.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot tableDoc = queryDocumentSnapshots.getDocuments().get(0);
                        createInvoiceInFirestore(tableDoc, invoice -> {
                            Intent intent = new Intent(this, InvoiceDetailActivity.class);
                            intent.putExtra("invoiceId", invoice.getId());
                            startActivity(intent);
                            finish();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding table info", e);
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
                    addInvoiceItems(invoiceId);

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

    interface OnInvoiceCreatedListener {
        void onInvoiceCreated(Invoice invoice);
    }

    private double calculateTotalAmount() {
        double totalAmount = 0;
        for (SelectedFood selectedFood : selectedFoodList) {
            totalAmount += selectedFood.getPrice() * selectedFood.getQuantity();
        }
        return totalAmount;
    }

    private void addInvoiceItems(String invoiceId) {
        for (SelectedFood selectedFood : selectedFoodList) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("hoa_don_id", invoiceId);
            itemData.put("ten_mon_an", selectedFood.getFoodName());
            itemData.put("so_luong", selectedFood.getQuantity());
            itemData.put("gia", selectedFood.getPrice());
            db.collection("invoice_items")
                    .add(itemData)
                    .addOnFailureListener(e -> {
                        Log.e("InvoiceItems ", "Error adding invoice items", e);
                    });
        }
    }

    private void updateTableStatus(String status) {
        db.collection("tables")
                .whereEqualTo("name", tableName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot tableDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String tableId = tableDoc.getId();
                        Map<String, Object> tableData = new HashMap<>();
                        tableData.put("status", status);

                        db.collection("tables").document(tableId)
                                .update(tableData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Updated table status successfully");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error updating table status", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error finding table info", e);
                });
    }

    private void updateSelectedFoodInFirestore(SelectedFood selectedFood) {
        if (selectedFood.getId() != null) {
            db.collection("selected_foods").document(selectedFood.getId())
                    .update(selectedFood.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("UpdateSelectedFood", "Updated food: " + selectedFood.getFoodName());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UpdateSelectedFood", "Error updating food", e);
                    });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("selectedFoodList", selectedFoodList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedFoodList = savedInstanceState.getParcelableArrayList("selectedFoodList");
        if (selectedFoodList != null) {
            for (SelectedFood selectedFood : selectedFoodList) {
                addFoodView(selectedFood);
            }
        }
    }
}