package vn.posicode.chuyende.QuanLyBan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.posicode.chuyende.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore; // Sử dụng firestore cho Firestore instance
    private EditText editTextTableName, editTextTableDescription;
    private LinearLayout tableListLayout;
    private Button addButton, editButton;
    private View currentSelectedTable;
    private List<View> tableViewsList = new ArrayList<>(); // Danh sách các view bàn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        // Khởi tạo FirebaseFirestore
        firestore = FirebaseFirestore.getInstance();

        // Khởi tạo các view
        editTextTableName = findViewById(R.id.editTextTableName);
        editTextTableDescription = findViewById(R.id.editTextTableDescription);
        tableListLayout = findViewById(R.id.tableListLayout);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        // Tải dữ liệu bàn đã lưu từ Firestore
        loadTableData();

        // Set onClickListener cho nút Thêm
        addButton.setOnClickListener(v -> addTable());

        // Set onClickListener cho nút Sửa
        editButton.setOnClickListener(v -> editTable());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
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

        saveTableToFirestore(tableName, tableDescription); // Lưu bàn vào Firestore
        clearInputFields();
    }

    // Hàm lưu bàn vào Firestore
    private void saveTableToFirestore(String tableName, String tableDescription) {
        // Tạo đối tượng table chứa thông tin bàn
        Table table = new Table(tableName, tableDescription);

        // Thêm dữ liệu vào Firestore
        firestore.collection("table1")
                .add(table)
                .addOnSuccessListener(documentReference -> {
                    // Lấy documentId từ Firestore
                    String documentId = documentReference.getId();

                    // Lưu documentId và hiển thị bàn trên giao diện
                    addTableToLayout(tableName, tableDescription, documentId);
                    showToast("Đã thêm bàn thành công");
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi thêm bàn: " + e.getMessage());
                });
    }

    // Hàm hiển thị bàn trong MainActivity với documentId
    private void addTableToLayout(String tableName, String tableDescription, String documentId) {
        View tableView = getLayoutInflater().inflate(R.layout.table_item, null);
        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        Button deleteButton = tableView.findViewById(R.id.deleteButton);

        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        // Lưu documentId dưới dạng tag cho View
        tableView.setTag(documentId);

        // Thêm chức năng xóa bàn
        deleteButton.setOnClickListener(v -> deleteTable(tableView));

        // Set click vào bàn để chỉnh sửa
        tableView.setOnClickListener(v -> selectTableForEditing(tableView, tableName, tableDescription));

        // Thêm khoảng cách giữa các bàn
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8); // Set khoảng cách trên và dưới của mỗi bàn
        tableView.setLayoutParams(params);

        // Thêm bàn vào danh sách và layout
        tableViewsList.add(tableView);
        updateTableLayout(); // Sắp xếp và hiển thị lại danh sách bàn
    }

    // Hàm chọn bàn để chỉnh sửa
    private void selectTableForEditing(View tableView, String tableName, String tableDescription) {
        currentSelectedTable = tableView;
        editTextTableName.setText(tableName);
        editTextTableDescription.setText(tableDescription);
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

        // Lấy documentId của bàn hiện tại
        String documentId = (String) currentSelectedTable.getTag();

        // Cập nhật dữ liệu trong Firestore
        firestore.collection("table1").document(documentId)
                .update("name", newTableName, "description", newTableDescription)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật giao diện sau khi thành công
                    TextView tableNameTextView = currentSelectedTable.findViewById(R.id.tableNameTextView);
                    TextView tableDescriptionTextView = currentSelectedTable.findViewById(R.id.tableDescriptionTextView);
                    tableNameTextView.setText(newTableName);
                    tableDescriptionTextView.setText(newTableDescription);

                    // Cập nhật lại view bàn trong tableViewsList
                    int index = tableViewsList.indexOf(currentSelectedTable);
                    if (index != -1) {
                        View updatedTableView = currentSelectedTable;
                        tableViewsList.set(index, updatedTableView); // Cập nhật danh sách với view đã chỉnh sửa
                    }

                    // Làm mới lại danh sách trên giao diện
                    updateTableLayout();

                    // Xóa lựa chọn hiện tại sau khi chỉnh sửa
                    clearInputFields();
                    currentSelectedTable = null;
                    showToast("Đã sửa bàn thành công");
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi sửa bàn: " + e.getMessage());
                });
    }


    // Hàm xóa bàn
    private void deleteTable(View tableView) {
        // Lấy documentId của bàn cần xóa
        String documentId = (String) tableView.getTag();

        // Xóa bàn từ Firestore
        firestore.collection("table1").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Xóa bàn từ layout sau khi thành công
                    tableViewsList.remove(tableView);
                    tableListLayout.removeView(tableView);
                    showToast("Đã xóa bàn thành công");
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi xóa bàn: " + e.getMessage());
                });
    }

    // Hàm tải các bàn đã lưu từ Firestore
    private void loadTableData() {
        firestore.collection("table1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String tableName = document.getString("name");
                            String tableDescription = document.getString("description");
                            String documentId = document.getId(); // Lấy documentId của từng bàn
                            addTableToLayout(tableName, tableDescription, documentId);
                        }
                    } else {
                        showToast("Lỗi khi tải dữ liệu: " + task.getException().getMessage());
                    }
                });
    }

    // Hàm cập nhật giao diện khi sắp xếp lại danh sách bàn theo số thứ tự
    private void updateTableLayout() {
        // Xóa tất cả các bàn trong layout hiện tại
        tableListLayout.removeAllViews();

        // Sắp xếp danh sách bàn theo số thứ tự
        Collections.sort(tableViewsList, (view1, view2) -> {
            // Lấy tên của bàn từ TextView
            String name1 = ((TextView) view1.findViewById(R.id.tableNameTextView)).getText().toString();
            String name2 = ((TextView) view2.findViewById(R.id.tableNameTextView)).getText().toString();

            // Trích xuất số từ tên bàn, ví dụ: "Ban 1" -> 1
            int tableNumber1 = extractNumberFromTableName(name1);
            int tableNumber2 = extractNumberFromTableName(name2);

            // So sánh số thứ tự bàn để sắp xếp
            return Integer.compare(tableNumber1, tableNumber2);
        });

        // Thêm lại các bàn theo thứ tự đã sắp xếp
        for (View sortedTableView : tableViewsList) {
            tableListLayout.addView(sortedTableView);
        }
    }

    // Hàm phụ để trích xuất số từ tên bàn, ví dụ: "Ban 1" -> 1
    private int extractNumberFromTableName(String tableName) {
        // Trích xuất tất cả các chữ số từ chuỗi
        String numberString = tableName.replaceAll("[^0-9]", "");
        try {
            // Chuyển đổi chuỗi số thành số nguyên
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            // Trường hợp không có số trong tên, trả về số mặc định là 0
            return 0;
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
