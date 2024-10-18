package vn.posicode.chuyende.activities;;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TablePrefs";
    private static final String TABLE_COUNT_KEY = "table_count";
    private static final String TABLE_NAME_KEY_PREFIX = "table_name_";
    private static final String TABLE_DESC_KEY_PREFIX = "table_desc_";
    private EditText editTextTableName, editTextTableDescription;
    private LinearLayout tableListLayout;
    private Button addButton, editButton;
    private View currentSelectedTable;
    private int currentSelectedTableIndex = -1; // Để lưu index của bàn đang được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        editTextTableName = findViewById(R.id.editTextTableName);
        editTextTableDescription = findViewById(R.id.editTextTableDescription);
        tableListLayout = findViewById(R.id.tableListLayout);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        // Tải dữ liệu bàn đã lưu
        loadTableData();

        // Set onClickListener cho nút Thêm
        addButton.setOnClickListener(v -> addTable());

        // Set onClickListener cho nút Sửa
        editButton.setOnClickListener(v -> editTable());


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Đóng hoạt động hiện tại và quay lại màn hình trước đó
            finish(); // Đây là cách để trở lại màn hình trước đó
        });

    }

    // Hàm thêm bàn
    private void addTable() {
        String tableName = editTextTableName.getText().toString().trim();
        String tableDescription = editTextTableDescription.getText().toString().trim();

        if (tableName.isEmpty() || tableDescription.isEmpty()) {
            showToast("Vui lòng nhập đủ thông tin");
            return;
        }

        saveTableData(tableName, tableDescription); // Lưu bàn vào SharedPreferences
        addTableToLayout(tableName, tableDescription); // Hiển thị bàn trên giao diện
        clearInputFields();
    }

    // Hàm hiển thị bàn trong MainActivity
    private void addTableToLayout(String tableName, String tableDescription) {
        View tableView = getLayoutInflater().inflate(R.layout.table_item, null);
        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        Button deleteButton = tableView.findViewById(R.id.deleteButton);

        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        // Thêm chức năng xóa bàn
        deleteButton.setOnClickListener(v -> deleteTable(tableView));

        // Set click vào bàn để chỉnh sửa
        tableView.setOnClickListener(v -> selectTableForEditing(tableView, tableName, tableDescription));

        // Thêm bàn vào layout
        tableListLayout.addView(tableView);
    }

    // Hàm lưu bàn vào SharedPreferences
    private void saveTableData(String tableName, String tableDescription) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int tableCount = sharedPreferences.getInt(TABLE_COUNT_KEY, 0);
        editor.putString(TABLE_NAME_KEY_PREFIX + tableCount, tableName);
        editor.putString(TABLE_DESC_KEY_PREFIX + tableCount, tableDescription);

        editor.putInt(TABLE_COUNT_KEY, tableCount + 1);
        editor.apply();
    }

    // Hàm chọn bàn để chỉnh sửa
    private void selectTableForEditing(View tableView, String tableName, String tableDescription) {
        currentSelectedTable = tableView;
        editTextTableName.setText(tableName);
        editTextTableDescription.setText(tableDescription);

        // Tìm chỉ số (index) của bàn được chọn
        currentSelectedTableIndex = tableListLayout.indexOfChild(tableView);
    }

    // Hàm chỉnh sửa bàn
    private void editTable() {
        if (currentSelectedTable == null) {
            showToast("Vui lòng chọn bàn để sửa");
            return;
        }

        String newTableName = editTextTableName.getText().toString().trim();
        String newTableDescription = editTextTableDescription.getText().toString().trim();

        if (newTableName.isEmpty() || newTableDescription.isEmpty()) {
            showToast("Vui lòng nhập đủ thông tin");
            return;
        }

        // Cập nhật dữ liệu trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TABLE_NAME_KEY_PREFIX + currentSelectedTableIndex, newTableName);
        editor.putString(TABLE_DESC_KEY_PREFIX + currentSelectedTableIndex, newTableDescription);
        editor.apply();

        // Cập nhật giao diện
        TextView tableNameTextView = currentSelectedTable.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = currentSelectedTable.findViewById(R.id.tableDescriptionTextView);
        tableNameTextView.setText(newTableName);
        tableDescriptionTextView.setText(newTableDescription);

        // Xóa lựa chọn hiện tại sau khi chỉnh sửa
        clearInputFields();
        currentSelectedTable = null;
    }

    // Hàm xóa bàn
    private void deleteTable(View tableView) {
        // Xóa bàn từ SharedPreferences
        int tableIndex = tableListLayout.indexOfChild(tableView);
        removeTableFromPreferences(tableIndex);

        // Xóa bàn khỏi layout
        tableListLayout.removeView(tableView);

        showToast("Bàn đã bị xóa");
    }

    // Hàm xóa bàn khỏi SharedPreferences
    private void removeTableFromPreferences(int tableIndex) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int tableCount = sharedPreferences.getInt(TABLE_COUNT_KEY, 0);

        // Xóa bàn ở vị trí cụ thể
        for (int i = tableIndex; i < tableCount - 1; i++) {
            String nextTableName = sharedPreferences.getString(TABLE_NAME_KEY_PREFIX + (i + 1), "");
            String nextTableDescription = sharedPreferences.getString(TABLE_DESC_KEY_PREFIX + (i + 1), "");
            editor.putString(TABLE_NAME_KEY_PREFIX + i, nextTableName);
            editor.putString(TABLE_DESC_KEY_PREFIX + i, nextTableDescription);
        }

        // Xóa bàn cuối cùng
        editor.remove(TABLE_NAME_KEY_PREFIX + (tableCount - 1));
        editor.remove(TABLE_DESC_KEY_PREFIX + (tableCount - 1));
        editor.putInt(TABLE_COUNT_KEY, tableCount - 1);

        editor.apply();
    }

    // Hàm tải các bàn đã lưu
    private void loadTableData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int tableCount = sharedPreferences.getInt(TABLE_COUNT_KEY, 0);

        for (int i = 0; i < tableCount; i++) {
            String tableName = sharedPreferences.getString(TABLE_NAME_KEY_PREFIX + i, "");
            String tableDescription = sharedPreferences.getString(TABLE_DESC_KEY_PREFIX + i, "");
            addTableToLayout(tableName, tableDescription);
        }
    }

    // Hàm hiển thị Toast
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Hàm xóa dữ liệu trong input
    private void clearInputFields() {
        editTextTableName.setText("");
        editTextTableDescription.setText("");
    }
}