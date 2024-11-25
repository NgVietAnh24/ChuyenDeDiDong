package vn.posicode.chuyende.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.MonDaBan_Adapter;
import vn.posicode.chuyende.models.MonDaBanModels;

public class ThongKeMonDaBan extends AppCompatActivity {
    private RecyclerView recMonDaBan;
    private ImageButton btnCalendar, btnBack;
    private TextView tv_nullMonDaChon, tv_doanhthu;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<MonDaBanModels> listMonDaBan = new ArrayList<>();
    private MonDaBan_Adapter monDaBan_adapter;
    private Calendar selectedDate = Calendar.getInstance();
    private int tongDoanhThuTrongNgay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke_mon_da_ban);
        connectXML();
        monDaBan_adapter = new MonDaBan_Adapter(listMonDaBan);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recMonDaBan.setLayoutManager(gridLayoutManager);

        recMonDaBan.setAdapter(monDaBan_adapter);
        recMonDaBan.setLayoutManager(gridLayoutManager);
        //  docMonDaBan();
//        loc danh sach theo ngày mới nhất
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateStr = sdf.format(new Date());
        locDanhSach(currentDateStr);
//        Mở dialog datetime
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void connectXML() {
        recMonDaBan = findViewById(R.id.recMonDaBan);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnBack = findViewById(R.id.btnBack);
        tv_nullMonDaChon = findViewById(R.id.tv_nullMonDaChon);
        tv_doanhthu = findViewById(R.id.tv_doanhthu);
    }


    //    doc cac mon da ban tu 2 collection
    /*
    private void docMonDaBan() {
        listMonDaBan.clear();

        db.collection("invoices").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot invoice : task.getResult()) {
                    String hoaDonId = invoice.getId();
                    Date ngayTaoDate = null;
                    if (invoice.contains("ngay_tao")) {
                        if (invoice.get("ngay_tao") instanceof String) {
                            String ngayTaoString = invoice.getString("ngay_tao");
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                                ngayTaoDate = sdf.parse(ngayTaoString);
                                Log.d("Firestore", "Ngày tạo (String): " + ngayTaoString);
                            } catch (Exception e) {
                                Log.w("Firestore", "Lỗi khi chuyển đổi ngày: " + ngayTaoString, e);
                            }
                        } else if (invoice.get("ngay_tao") instanceof Timestamp) {
                            Timestamp ngayTaoTimestamp = invoice.getTimestamp("ngay_tao");
                            if (ngayTaoTimestamp != null) {
                                ngayTaoDate = ngayTaoTimestamp.toDate();
                                Log.d("Firestore", "Ngày tạo (Timestamp): " + ngayTaoDate.toString());
                            }
                        } else {
                            Log.w("Firestore", "Trường 'ngay_tao' không phải là String hoặc Timestamp");
                        }
                    }

                    Date finalNgayTaoDate = ngayTaoDate;
                    db.collection("invoice_items").whereEqualTo("hoa_don_id", hoaDonId).get().addOnCompleteListener(itemsTask -> {
                        if (itemsTask.isSuccessful() && itemsTask.getResult() != null) {
                            for (QueryDocumentSnapshot item : itemsTask.getResult()) {
                                String tenMon = item.getString("ten_mon_an");
                                int soLuong = item.getLong("so_luong").intValue();
                                int gia = item.getLong("gia").intValue();
                                double totalPrice = soLuong * gia;
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String formattedDate = finalNgayTaoDate != null ? outputFormat.format(finalNgayTaoDate) : "###";

                                // Thêm vào danh sách hiển thị
                                // listMonDaBan.add(new MonDaBanModels(tenMon, soLuong, (int) totalPrice, formattedDate));
                                capNhatSoLuong(tenMon, soLuong, gia, formattedDate);
                            }
                            // Cập nhật giao diện mỗi lần lấy xong dữ liệu của một hóa đơn
                            monDaBan_adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
     */

    //Show dialog datetime
    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String selectedDateString = sdf.format(selectedDate.getTime());
//            loc danh sach theo ngay
                    locDanhSach(selectedDateString);
                }, year, month, day);

        datePickerDialog.show();
    }

    //    Loc danh sach theo ngay
    private void locDanhSach(String selectedDate) {
        listMonDaBan.clear();
        tongDoanhThuTrongNgay = 0;
        db.collection("invoices").addSnapshotListener((invoiceSnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error fetching invoices: ", error);
                return;
            }
            if (invoiceSnapshots != null) {
                boolean check = false;
                for (QueryDocumentSnapshot invoice : invoiceSnapshots) {
                    String hoaDonId = invoice.getId();
                    Date ngayTaoDate = null;
                    if (invoice.contains("ngay_tao")) {
                        if (invoice.get("ngay_tao") instanceof String) {
                            String ngayTaoString = invoice.getString("ngay_tao");
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                ngayTaoDate = sdf.parse(ngayTaoString);
                            } catch (Exception e) {
                                Log.w("Firestore", "Error parsing date: " + ngayTaoString, e);
                            }
                        } else if (invoice.get("ngay_tao") instanceof Timestamp) {
                            Timestamp ngayTaoTimestamp = invoice.getTimestamp("ngay_tao");
                            if (ngayTaoTimestamp != null) {
                                ngayTaoDate = ngayTaoTimestamp.toDate();
                            }
                        }
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = ngayTaoDate != null ? sdf.format(ngayTaoDate) : "###";
                    if (selectedDate.equals(formattedDate)) {
                        check = true;
                        db.collection("invoice_items")
                                .whereEqualTo("hoa_don_id", hoaDonId)
                                .addSnapshotListener((itemsSnapshots, itemsError) -> {
                                    if (itemsError != null) {
                                        Log.e("Firestore", "Error fetching invoice items: ", itemsError);
                                        return;
                                    }

                                    if (itemsSnapshots != null) {
                                        for (QueryDocumentSnapshot item : itemsSnapshots) {
                                            String tenMon = item.getString("ten_mon_an");
                                            int soLuong = item.getLong("so_luong").intValue();
                                            int gia = item.getLong("gia").intValue();
                                            //  tinh tong tien da ban trong ngay
                                            int tongTien = soLuong * gia;
                                            tongDoanhThuTrongNgay += tongTien;
                                            capNhatSoLuong(tenMon, soLuong, gia, tongTien, formattedDate);
                                        }
                                        DecimalFormat deci = new DecimalFormat("#,###");
                                        String tongDoanhThuDeciMal = deci.format(tongDoanhThuTrongNgay);
                                        tv_doanhthu.setText("Doanh thu: " + tongDoanhThuDeciMal + "đ");
                                        monDaBan_adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
                if (!check) {
                    tv_doanhthu.setText("Doanh thu: " + 0 + "đ");
                    tv_nullMonDaChon.setVisibility(View.VISIBLE);
                    tv_nullMonDaChon.setText("Không có dữ liệu cho ngày "+selectedDate);
                    recMonDaBan.setVisibility(View.GONE);
                } else {
                    tv_nullMonDaChon.setVisibility(View.GONE);
                    recMonDaBan.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //kiem tra mon trung
    private void capNhatSoLuong(String tenMon, int soLuong, int gia, int tongTien, String ngayBan) {
        for (MonDaBanModels daBanModels : listMonDaBan) {
            if (daBanModels.getTenMon().equals(tenMon) && daBanModels.getNgay().equals(ngayBan)) {
                daBanModels.setSoLuong(daBanModels.getSoLuong() + soLuong);
                daBanModels.setTongTien(daBanModels.getTongTien() + tongTien);
                return;
            }
        }
        listMonDaBan.add(new MonDaBanModels(tenMon, soLuong, gia, tongTien, ngayBan));
    }
}