package vn.vietanhnguyen.khachhangdatmon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.vietanhnguyen.khachhangdatmon.Api.CreateOrder;
import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.adapters.DaChonAdapter;
import vn.vietanhnguyen.khachhangdatmon.adapters.MonAnThanhToanAdapter;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToan extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView tvMaHD, tvTenKH, tvSoDT, tvGhiChu, tvTongTien, tvTime, tvDate;
    private Button btnThanhToan, btnBackHome;
    private RecyclerView recyclerViewMonAn;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private long tongTien;
    MonAnThanhToanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        Event();

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        List<MonAn> monDaChon = getIntent().getParcelableArrayListExtra("monDaChon");
        if (monDaChon != null && !monDaChon.isEmpty()) {
            // Tính tổng tiền
            tongTien = 0;
            for (MonAn monAn : monDaChon) {
                long gia = Long.parseLong(monAn.getPrice());
                tongTien += gia * monAn.getSoLuong();
            }
            String formattedPrice = formatCurrency(tongTien);
            tvTongTien.setText("Tổng tiền: " + formattedPrice + " VND");

            // Khởi tạo adapter và gán cho RecyclerView
            adapter = new MonAnThanhToanAdapter(monDaChon);
            recyclerViewMonAn.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewMonAn.setAdapter(adapter);
        } else {
            Log.e("ThanhToan", "Danh sách món ăn không hợp lệ");
        }

        String totalString = String.format("%d", tongTien);

        String userId = firebaseAuth.getCurrentUser().getUid();

        // Truy vấn thông tin khách hàng từ Firestore
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Lấy thông tin khách hàng từ Firestore
                    String tenKH = document.getString("tenNhanVien");
                    String soDT = document.getString("sDT");

                    String ghiChu = getIntent().getStringExtra("ghiChu");

                    Date now = new Date();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String time = timeFormat.format(now);
                    String date = dateFormat.format(now);
                    DecimalFormat deci = new DecimalFormat("#,###");

                    // Hiển thị thông tin
                    tvMaHD.setText("Mã hóa đơn: ");
                    tvTenKH.setText("Tên khách hàng: " + tenKH);
                    tvSoDT.setText("Số điện thoại: " + soDT);
                    tvGhiChu.setText("Ghi chú:  (Chuyển khoản) - " + ghiChu);
                    tvTongTien.setText("Tổng tiền: " + deci.format(tongTien) + " VND");
                    tvTime.setText(time);
                    tvDate.setText(date);

                    StrictMode.ThreadPolicy policy = new
                            StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // ZaloPay SDK Init
                    ZaloPaySDK.init(553, Environment.SANDBOX);

                    btnThanhToan.setOnClickListener(v -> {
                        CreateOrder orderApi = new CreateOrder();

                        try {
                            JSONObject data = orderApi.createOrder(totalString);
                            String code = data.getString("returncode");

                            if (code.equals("1")) {
                                String token = data.getString("zptranstoken");
                                ZaloPaySDK.getInstance().payOrder(ThanhToan.this, token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String s, String s1, String s2) {
                                        Intent intent1 = new Intent(ThanhToan.this, ThanhToan.class);
                                        intent1.putExtra("result", "Thanh toán thành công");
                                        startActivity(intent1);

                                        // Cập nhật trạng thái bàn thành "trống" sau khi thanh toán thành công
                                        String banId = getIntent().getStringExtra("id");
                                        firestore.collection("tables").document(banId)
                                                .update("status", "Trống")
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Log.d("ThanhToan", "Cập nhật trạng thái bàn thành công.");
                                                    } else {
                                                        Log.d("ThanhToan", "Lỗi khi cập nhật trạng thái bàn: ", task.getException());
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onPaymentCanceled(String s, String s1) {
                                        Intent intent1 = new Intent(ThanhToan.this, ThanhToan.class);
                                        intent1.putExtra("result", "Hủy thanh toán");
                                        startActivity(intent1);
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                        Intent intent1 = new Intent(ThanhToan.this, ThanhToan.class);
                                        intent1.putExtra("result", "lỗi thanh toán");
                                        startActivity(intent1);
                                    }
                                });

                                // Tạo dữ liệu hóa đơn
                                Map<String, Object> invoiceData = new HashMap<>();
                                invoiceData.put("ban_id", getIntent().getStringExtra("id"));
                                invoiceData.put("tong_tien", tongTien);
                                invoiceData.put("gio_tao", time);
                                invoiceData.put("ngay_tao", date);
                                invoiceData.put("tinh_trang", "Đã thanh toán");
                                invoiceData.put("ten_khach_hang", tenKH);
                                invoiceData.put("so_dt", soDT);
                                invoiceData.put("ghi_chu", "(Chuyển khoản) -" + ghiChu);

                                // Thêm hóa đơn vào Firestore và lấy ID
                                firestore.collection("invoices").add(invoiceData)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                String maHD = task1.getResult().getId();

                                                tvMaHD.setText("Mã hóa đơn: " + maHD);


                                                // Lưu thông tin món ăn
                                                for (MonAn monAn : monDaChon) {
                                                    Map<String, Object> invoiceItemData = new HashMap<>();
                                                    invoiceItemData.put("ten_mon_an", monAn.getName());
                                                    invoiceItemData.put("gia", Integer.parseInt(monAn.getPrice()));
                                                    invoiceItemData.put("so_luong", monAn.getSoLuong());
                                                    invoiceItemData.put("hoa_don_id", maHD);

                                                    // Thêm món ăn vào Firestore
                                                    firestore.collection("invoice_items").add(invoiceItemData)
                                                            .addOnCompleteListener(task2 -> {
                                                                if (task2.isSuccessful()) {
                                                                    showAlert("Chúc quý khách ngon miệng ☺️");
                                                                } else {
                                                                    Log.d("ThanhToan", "Lỗi khi lưu thông tin món ăn: ", task2.getException());
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.d("ThanhToan", "Lỗi khi lưu thông tin hóa đơn: ", task1.getException());
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });


                } else {
                    Log.d("ThanhToan", "Không tìm thấy thông tin người dùng.");
                }
            } else {
                Log.d("ThanhToan", "Lỗi khi truy vấn thông tin người dùng: ", task.getException());
            }

        });

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v ->
                finish()
        );

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThanhToan.this, Home.class);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    public static String formatCurrency(long amount) {
        StringBuilder formatted = new StringBuilder();
        String amountStr = Long.toString(amount);
        int length = amountStr.length();
        int count = 0;

        for (int i = length - 1; i >= 0; i--) {
            formatted.append(amountStr.charAt(i));
            count++;
            if (count % 3 == 0 && i != 0) {
                formatted.append("."); // Thêm dấu chấm sau mỗi 3 ký tự
            }
        }

        return formatted.reverse().toString(); // Đảo ngược chuỗi
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) ->
                {
                    DanhSachDaChon.btnThanhToan.setVisibility(View.GONE);
                    DanhSachDaChon.btnHuyDon.setVisibility(View.GONE);
                    DanhSachDaChon.edtGhiChu.setEnabled(false);
                    DanhSachDaChon.overlayView.setVisibility(View.VISIBLE);
                    DanhSachDaChon.btnBackHome.setVisibility(View.VISIBLE);
                    btnBackHome.setVisibility(View.VISIBLE);
                    DanhSachDaChon.btnBack.setVisibility(View.GONE);
                    btnThanhToan.setVisibility(View.GONE);
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void Event() {
        btnBack = findViewById(R.id.backButton);
        tvMaHD = findViewById(R.id.invoiceIdTextView);
        tvTenKH = findViewById(R.id.customerNameTextView);
        tvSoDT = findViewById(R.id.customerPhoneTextView);
        tvGhiChu = findViewById(R.id.noteTextView);
        tvTongTien = findViewById(R.id.totalTextView);
        tvTime = findViewById(R.id.invoiceTimeTextView);
        tvDate = findViewById(R.id.invoiceDateTextView);
        btnThanhToan = findViewById(R.id.paymentButton);
        btnBackHome = findViewById(R.id.btnBackHome);
        recyclerViewMonAn = findViewById(R.id.itemsListView);
    }
}
