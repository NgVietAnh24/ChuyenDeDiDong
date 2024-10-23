package vn.posicode.chuyende.QuanLyBan;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter; // Thêm import cho ArrayAdapter
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

    private FirebaseFirestore firestore;
    private EditText editTextTableName, editTextTableDescription;
    private Spinner statusSpinner;
    private LinearLayout tableListLayout;
    private Button addButton, editButton;
    private View currentSelectedTable;
    private List<View> tableViewsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        editTextTableName = findViewById(R.id.editTextTableName);
        editTextTableDescription = findViewById(R.id.editTextTableDescription);
        statusSpinner = findViewById(R.id.statusSpinner);
        tableListLayout = findViewById(R.id.tableListLayout);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);

        // Thêm adapter cho Spinner
        String[] statuses = {"Có sẵn", "Đã đặt", "Đang sử dụng"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        loadTableData();

        addButton.setOnClickListener(v -> addTable());
        editButton.setOnClickListener(v -> editTable());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void addTable() {
        String tableName = editTextTableName.getText().toString().trim();
        String tableDescription = editTextTableDescription.getText().toString().trim();
        String tableStatus = statusSpinner.getSelectedItem() != null ? statusSpinner.getSelectedItem().toString() : null;

        if (tableName.isEmpty() || tableDescription.isEmpty() || tableStatus == null) {
            showToast("Vui lòng nhập đủ thông tin");
            return;
        }

        saveTableToFirestore(tableName, tableDescription, tableStatus);
        clearInputFields();
    }

    private void saveTableToFirestore(String tableName, String tableDescription, String tableStatus) {
        // Tạo một đối tượng Table
        Table table = new Table(tableName, tableDescription, tableStatus);

        // Lưu dữ liệu vào Firestore
        firestore.collection("table1")
                .add(table)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    addTableToLayout(tableName, tableDescription, documentId, tableStatus);
                    showToast("Đã thêm bàn thành công");
                })
                .addOnFailureListener(e -> {
                    showToast("Lỗi khi thêm bàn: " + e.getMessage());
                    Log.e("FirestoreError", "Lỗi khi thêm bàn: ", e);
                });
    }



    private void addTableToLayout(String tableName, String tableDescription, String documentId, String tableStatus) {
        if (tableName == null || tableDescription == null || documentId == null || tableStatus == null) {
            Log.e("MainActivity", "Không thể thêm bàn: một hoặc nhiều giá trị là null");
            return;
        }

        View tableView = getLayoutInflater().inflate(R.layout.table_item, null);
        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        ImageView tableStatusImage = tableView.findViewById(R.id.tableStatusImage);
        Button deleteButton = tableView.findViewById(R.id.deleteButton);

        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        switch (tableStatus) {
            case "occupied":
                tableStatusImage.setImageResource(R.drawable.circle_red);
                break;
            case "reserved":
                tableStatusImage.setImageResource(R.drawable.circle_yellow);
                break;
            case "available":
            default:
                tableStatusImage.setImageResource(R.drawable.circle_grey);
                break;
        }

        tableView.setTag(documentId);
        deleteButton.setOnClickListener(v -> deleteTable(tableView));
        tableView.setOnClickListener(v -> selectTableForEditing(tableView, tableName, tableDescription, tableStatus));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        tableView.setLayoutParams(params);

        tableViewsList.add(tableView);
        updateTableLayout();
    }

    private void selectTableForEditing(View tableView, String tableName, String tableDescription, String tableStatus) {
        currentSelectedTable = tableView;
        editTextTableName.setText(tableName);
        editTextTableDescription.setText(tableDescription);

        switch (tableStatus) {
            case "occupied":
                statusSpinner.setSelection(0);
                break;
            case "reserved":
                statusSpinner.setSelection(1);
                break;
            case "available":
            default:
                statusSpinner.setSelection(2);
                break;
        }
    }

    private void editTable() {
        if (currentSelectedTable == null) {
            showToast("Vui lòng chọn bàn để sửa");
            return;
        }

        String newTableName = editTextTableName.getText().toString().trim();
        String newTableDescription = editTextTableDescription.getText().toString().trim();
        String newTableStatus = statusSpinner.getSelectedItem().toString();

        if (newTableName.isEmpty() || newTableDescription.isEmpty() || newTableStatus.isEmpty()) {
            showToast("Vui lòng nhập đủ thông tin");
            return;
        }

        String documentId = (String) currentSelectedTable.getTag();

        firestore.collection("table1").document(documentId)
                .update("name", newTableName, "description", newTableDescription, "status", newTableStatus)
                .addOnSuccessListener(aVoid -> {
                    TextView tableNameTextView = currentSelectedTable.findViewById(R.id.tableNameTextView);
                    TextView tableDescriptionTextView = currentSelectedTable.findViewById(R.id.tableDescriptionTextView);
                    ImageView tableStatusImage = currentSelectedTable.findViewById(R.id.tableStatusImage);

                    tableNameTextView.setText(newTableName);
                    tableDescriptionTextView.setText(newTableDescription);

                    switch (newTableStatus) {
                        case "occupied":
                            tableStatusImage.setImageResource(R.drawable.circle_red);
                            break;
                        case "reserved":
                            tableStatusImage.setImageResource(R.drawable.circle_yellow);
                            break;
                        case "available":
                        default:
                            tableStatusImage.setImageResource(R.drawable.circle_grey);
                            break;
                    }

                    int index = tableViewsList.indexOf(currentSelectedTable);
                    if (index != -1) {
                        View updatedTableView = currentSelectedTable;
                        tableViewsList.set(index, updatedTableView);
                    }

                    updateTableLayout();
                    clearInputFields();
                    currentSelectedTable = null;
                    showToast("Đã sửa bàn thành công");
                })
                .addOnFailureListener(e -> showToast("Lỗi khi sửa bàn: " + e.getMessage()));
    }

    private void loadTableData() {
        firestore.collection("table1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String tableName = document.getString("name");
                            String tableDescription = document.getString("description");
                            String tableStatus = document.getString("status");
                            String documentId = document.getId();

                            if (tableName != null && tableDescription != null && tableStatus != null && !tableStatus.isEmpty()) {
                                addTableToLayout(tableName, tableDescription, documentId, tableStatus);
                            } else {
                                Log.e("MainActivity", "Một trong các giá trị là null hoặc rỗng: name=" + tableName + ", description=" + tableDescription + ", status=" + tableStatus);
                            }
                        }
                    } else {
                        showToast("Lỗi khi tải dữ liệu: " + task.getException().getMessage());
                    }
                });
    }

    private void deleteTable(View tableView) {
        String documentId = (String) tableView.getTag();

        firestore.collection("table1").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    tableViewsList.remove(tableView);
                    updateTableLayout();
                    showToast("Đã xóa bàn thành công");
                })
                .addOnFailureListener(e -> showToast("Lỗi khi xóa bàn: " + e.getMessage()));
    }

    private void updateTableLayout() {
        tableListLayout.removeAllViews();
        for (View tableView : tableViewsList) {
            tableListLayout.addView(tableView);
        }
    }

    private void clearInputFields() {
        editTextTableName.setText("");
        editTextTableDescription.setText("");
        statusSpinner.setSelection(0);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
