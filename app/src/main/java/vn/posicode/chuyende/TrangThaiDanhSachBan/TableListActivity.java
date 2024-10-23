package vn.posicode.chuyende.TrangThaiDanhSachBan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.Menu.ManageActivity;

public class TableListActivity extends AppCompatActivity {

    private FirebaseFirestore firestore; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();

        // Tải danh sách bàn từ Firestore
        //loadTableData();

        // Xử lý sự kiện nút Quay lại
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút Quản lý (Chuyển đến màn hình ManageActivity)
        ImageButton manageButton = findViewById(R.id.backButton);
        manageButton.setOnClickListener(v -> {
            Intent intent = new Intent(TableListActivity.this, ManageActivity.class);
            startActivity(intent);
        });
    }

    // Hàm tải danh sách bàn từ Firestore và sắp xếp
    private void loadTableData() {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);
        tableListLayout.removeAllViews(); // Xóa danh sách hiện có trước khi tải dữ liệu mới

        // Lấy dữ liệu từ Firestore
        firestore.collection("table1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> tableList = new ArrayList<>();

                        // Duyệt qua các document lấy từ Firestore và thêm vào danh sách
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            tableList.add(document);
                        }

                        // Sắp xếp danh sách theo tên hoặc số bàn
                        Collections.sort(tableList, new Comparator<DocumentSnapshot>() {
                            @Override
                            public int compare(DocumentSnapshot t1, DocumentSnapshot t2) {
                                // Lấy tên bàn và tách số từ tên, ví dụ: "Table 1", "Table 2"
                                String tableName1 = t1.getString("name").replaceAll("[^0-9]", "");
                                String tableName2 = t2.getString("name").replaceAll("[^0-9]", "");

                                // So sánh số thứ tự bàn
                                return Integer.compare(Integer.parseInt(tableName1), Integer.parseInt(tableName2));
                            }
                        });

                        // Hiển thị từng bàn đã sắp xếp
                        for (DocumentSnapshot document : tableList) {
                            String tableName = document.getString("name");
                            String tableDescription = document.getString("description");
                            String tableStatus = document.getString("status"); // Lấy trạng thái bàn

                            if (tableName != null && tableDescription != null && tableStatus != null) {
                                addTableToLayout(tableName, tableDescription, tableStatus); // Thêm bàn vào layout
                            }
                        }
                    } else {
                        Log.e("TableListActivity", "Lỗi khi tải dữ liệu: " + task.getException().getMessage());
                    }
                });
    }

    // Hàm thêm bàn vào layout
    private void addTableToLayout(String tableName, String tableDescription, String tableStatus) {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);

        // Kiểm tra xem hàm có bị gọi nhiều lần không
        Log.d("DEBUG", "Adding table: " + tableName);

        // Sử dụng layout table_item_tablelist.xml để hiển thị từng bàn
        View tableView = getLayoutInflater().inflate(R.layout.table_item_tablelist, null);

        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        ImageView tableStatusImage = tableView.findViewById(R.id.tableStatusImage); // Thay RadioButton bằng ImageView

        // Cài đặt tên và mô tả cho bàn
        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        // Thiết lập hình ảnh dựa trên trạng thái bàn
        switch (tableStatus) {
            case "Đã đặt":
                tableStatusImage.setImageResource(R.drawable.circle_yellow);
                break;
            case "Đang sử dụng":
                tableStatusImage.setImageResource(R.drawable.circle_red);
                break;
            case "Có sẵn":
            default:
                tableStatusImage.setImageResource(R.drawable.circle_grey);
                break;
        }

        // Thêm khoảng cách giữa các bàn
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8); // 8dp khoảng cách dưới
        tableView.setLayoutParams(params);

        // Thêm bàn vào layout
        tableListLayout.addView(tableView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTableData(); // Tải lại dữ liệu khi quay lại màn hình TableListActivity
    }
}