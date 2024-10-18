package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import vn.posicode.chuyende.R;

public class TableListActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TablePrefs";
    private static final String TABLE_COUNT_KEY = "table_count";
    private static final String TABLE_NAME_KEY_PREFIX = "table_name_";
    private static final String TABLE_DESC_KEY_PREFIX = "table_desc_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);
        loadTableData();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Kết thúc hoạt động hiện tại và quay lại màn hình trước đó
        });

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String tableName = intent.getStringExtra("tableName");
        String tableDescription = intent.getStringExtra("tableDescription");

        // Nếu có dữ liệu (tableName và tableDescription không null)
        if (tableName != null && tableDescription != null) {
            addTableToLayout(tableName, tableDescription);
        }

        // Ánh xạ nút quản lý (ImageButton) và xử lý sự kiện khi người dùng nhấn
        ImageButton manageButton = findViewById(R.id.backButton); // Đổi id này thành id của nút bạn cần dùng
        manageButton.setOnClickListener(v -> {
            // Mở màn hình quản lý khi nhấn nút
            Intent intent1 = new Intent(TableListActivity.this, ManageActivity.class);
            startActivity(intent1);
        });
    }

    private void loadTableData() {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);
        tableListLayout.removeAllViews(); // Xóa danh sách hiện có trước khi tải dữ liệu mới

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int tableCount = sharedPreferences.getInt(TABLE_COUNT_KEY, 0);

        // Hiển thị từng bàn đã lưu
        for (int i = 0; i < tableCount; i++) {
            String tableName = sharedPreferences.getString(TABLE_NAME_KEY_PREFIX + i, "");
            String tableDescription = sharedPreferences.getString(TABLE_DESC_KEY_PREFIX + i, "");

            if (!tableName.isEmpty() && !tableDescription.isEmpty()) {
                addTableToLayout(tableName, tableDescription); // Hiển thị bàn trong layout
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTableData(); // Tải lại dữ liệu khi quay lại màn hình TableListActivity
    }

    private void addTableToLayout(String tableName, String tableDescription) {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);

        // Sử dụng layout table_item_tablelist.xml thay vì table_item.xml
        View tableView = getLayoutInflater().inflate(R.layout.table_item_tablelist, null);

        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        ImageView tableStatusImage = tableView.findViewById(R.id.tableStatusImage); // Thay RadioButton bằng ImageView

        // Cài đặt tên và mô tả cho bàn
        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        // Thiết lập hình tròn màu xám cho bàn
        tableStatusImage.setImageResource(R.drawable.circle_grey); // Sử dụng hình tròn màu xám

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
}
