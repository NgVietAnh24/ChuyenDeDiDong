package vn.posicode.chuyende.activities.manages;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapters.MonDaBan_Adapter;
import vn.posicode.chuyende.models.MonDaBan;

public class ThongKeMonDaBan extends AppCompatActivity {
        private RecyclerView recMonDaBan;
        private ImageButton btnCalendar,btnBack;
        private TextView tv_nullMonDaChon;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private ArrayList<MonDaBan> listMonDaBan = new ArrayList<>();
        private MonDaBan_Adapter monDaBan_adapter;
        private String ngayHienTai = ngayHienTai();
        private Calendar selectedDate = Calendar.getInstance();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_thong_ke_mon_da_ban);
            connectXML();
            monDaBan_adapter = new MonDaBan_Adapter(listMonDaBan);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1); // 2 cột
            recMonDaBan.setLayoutManager(gridLayoutManager);

            recMonDaBan.setAdapter(monDaBan_adapter);
            recMonDaBan.setLayoutManager(gridLayoutManager);
            //  docMonDaBan();
            locDanhSach(ngayHienTai);
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
                    finish();
                }
            });
        }

        private void connectXML() {
            recMonDaBan = findViewById(R.id.recMonDaBan);
            btnCalendar = findViewById(R.id.btnCalendar);
            btnBack = findViewById(R.id.btnBack);
            tv_nullMonDaChon = findViewById(R.id.tv_nullMonDaChon);
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
                        if (selectedDateString != null) {
                            locDanhSach(selectedDateString);
                        }
                    }, year, month, day);

            datePickerDialog.show();
        }

        //    Loc danh sach theo ngay
        private void locDanhSach(String selectedDate) {
            listMonDaBan.clear();
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
                                                capNhatSoLuong(tenMon, soLuong, gia, formattedDate);
                                            }
                                            monDaBan_adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                    if (!check) {
                        tv_nullMonDaChon.setVisibility(View.VISIBLE);
                        recMonDaBan.setVisibility(View.GONE);
                    } else {
                        tv_nullMonDaChon.setVisibility(View.GONE);
                        recMonDaBan.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


        //kiem tra mon trung
        private void capNhatSoLuong(String tenMon, int soLuong, int gia, String ngayBan) {
            boolean found = false; // Biến để kiểm tra xem món ăn đã tồn tại hay chưa
            for (MonDaBan daBanModels : listMonDaBan) {
                if (daBanModels.getTenMon().equals(tenMon) && daBanModels.getNgay().equals(ngayBan)) {
                    // Nếu món ăn đã tồn tại, cập nhật số lượng và tổng tiền
                    daBanModels.setSoLuong(daBanModels.getSoLuong() + soLuong);
                    daBanModels.setTongTien(daBanModels.getTongTien() + (soLuong * gia)); // Cập nhật tổng tiền
                    found = true; // Đánh dấu là đã tìm thấy món ăn
                    Log.d("Update", "Cập nhật món: " + tenMon + ", Số lượng: " + daBanModels.getSoLuong() + ", Tổng tiền: " + daBanModels.getTongTien());
                    break; // Thoát khỏi vòng lặp
                }
            }
            // Nếu món ăn chưa tồn tại, thêm mới vào danh sách
            if (!found) {
                listMonDaBan.add(new MonDaBan(tenMon, soLuong, soLuong * gia, ngayBan));
                Log.d("Add", "Thêm món mới: " + tenMon + ", Số lượng: " + soLuong + ", Tổng tiền: " + (soLuong * gia));
            }
        }

        //    loc ngay hien tai
        private String ngayHienTai() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date());
        }

    }