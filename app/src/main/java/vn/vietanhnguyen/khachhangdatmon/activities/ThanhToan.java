package vn.vietanhnguyen.khachhangdatmon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private Button btnThanhToan;
    private RecyclerView recyclerViewMonAn;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private long tongTien;
    MonAnThanhToanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Khởi tạo giao diện
        Event();
        // Nhận danh sách món đã chọn từ Intent
        List<MonAn> monDaChon = getIntent().getParcelableArrayListExtra("monDaChon");
        // Sử dụng dữ liệu món ăn (cập nhật UI hoặc các thành phần khác)
        // Tính tổng tiền
        if (monDaChon != null) {
            tongTien = 0;
            for (MonAn monAn : monDaChon) {
                // Chuyển đổi giá sang long và tính tổng
                long gia = Long.parseLong(monAn.getPrice());
                tongTien += gia * monAn.getSoLuong();
                Log.d("TTT", "ssssssssssssssss " + gia);
            }

            // Hiển thị tổng tiền
            tvTongTien.setText("Tổng tiền: " + tongTien + " VND");

            // Setup the adapter with the updated list
            adapter = new MonAnThanhToanAdapter(monDaChon);
            recyclerViewMonAn.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewMonAn.setAdapter(adapter);
        }

        String totalString = String.format("%d", tongTien);

        // Lấy UID của người dùng hiện tại
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

                    // Nhận dữ liệu ghi chú từ trường nhập trong màn hình đã chọn món
                    String ghiChu = getIntent().getStringExtra("ghiChu");

                    Date now = new Date();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String time = timeFormat.format(now);
                    String date = dateFormat.format(now);

                    // Tạo mã hóa đơn tự động (ví dụ: sử dụng thời gian hiện tại hoặc một giá trị ngẫu nhiên)
                    String maHD = "HD" + System.currentTimeMillis(); // Mã hóa đơn dựa trên thời gian hiện tại

                    // Hiển thị thông tin hóa đơn
                    tvMaHD.setText("Mã hóa đơn: " + maHD);
                    tvTenKH.setText("Tên khách hàng: " + tenKH);
                    tvSoDT.setText("Số điện thoại: " + soDT);
                    tvGhiChu.setText("Ghi chú:  (Chuyển khoản) - " + ghiChu);
                    tvTongTien.setText("Tổng tiền: " + tongTien + " VND");
                    tvTime.setText(time);
                    tvDate.setText(date);

                    StrictMode.ThreadPolicy policy = new
                            StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // ZaloPay SDK Init
                    ZaloPaySDK.init(553, Environment.SANDBOX);

                    // Xử lý nút thanh toán
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
                                    Intent intent1 = new Intent(ThanhToan.this, DanhSachDaChon.class);
                                    intent1.putExtra("result", "Thanh toán thành công");
                                    intent1.putExtra("updateUI", true);
                                    startActivity(intent1);
                                    // Cập nhật trạng thái bàn thành "trống" sau khi thanh toán thành công
                                    String banId = getIntent().getStringExtra("id"); // Lấy ID bàn từ Intent
                                    firestore.collection("tables").document(banId)
                                            .update("status", "Trống") // Cập nhật trạng thái bàn
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
                                    Intent intent1 = new Intent(ThanhToan.this, DanhSachDaChon.class);
                                    intent1.putExtra("result", "Hủy thanh toán");
                                }

                                @Override
                                public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                    Intent intent1 = new Intent(ThanhToan.this, DanhSachDaChon.class);
                                    intent1.putExtra("result", "lỗi thanh toán");
                                }
                            });
                        }



                        Intent intentId = getIntent();

                        // Lưu thông tin hóa đơn vào Firestore
                        Map<String, Object> invoiceData = new HashMap<>();
                        invoiceData.put("ban_id", intentId.getStringExtra("id"));
                        invoiceData.put("tong_tien", tongTien);
                        invoiceData.put("gio_tao", time);
                        invoiceData.put("ngay_tao", date);
                        invoiceData.put("tinh_trang", "Đã thanh toán");
                        invoiceData.put("ten_khach_hang", tenKH);
                        invoiceData.put("so_dt", soDT);
                        invoiceData.put("ghi_chu", "(Chuyển khoản) -" + ghiChu);

                        firestore.collection("invoices").document(maHD)
                                .set(invoiceData)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
//                                            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Log.d("ThanhToan", "Lỗi khi lưu thông tin thanh toán: ", task1.getException());
                                    }
                                });

                        for (MonAn monAn : monDaChon) {
                            Map<String, Object> invoiceItemData = new HashMap<>();
                            invoiceItemData.put("ten_mon_an", monAn.getName());
                            invoiceItemData.put("gia", Integer.parseInt(monAn.getPrice()));  // Đảm bảo monAn.getPrice() là kiểu String chứa số
                            invoiceItemData.put("so_luong", monAn.getSoLuong());
                            invoiceItemData.put("hoa_don_id", maHD);

                            // Tạo một document id duy nhất cho từng món ăn, ví dụ sử dụng tên món ăn và mã hóa đơn
                            String documentId = maHD + "_" + monAn.getName();

                            firestore.collection("invoice_items").document(documentId)
                                    .set(invoiceItemData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
//                                        Log.d("ThanhToan", "Lưu thành công thông tin món " + monAn.getName());
                                        } else {
                                            Log.d("ThanhToan", "Lỗi khi lưu thông tin thanh toán: ", task1.getException());
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
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
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
        recyclerViewMonAn = findViewById(R.id.itemsListView); // Đảm bảo RecyclerView có id là itemsRecyclerView
    }
}
