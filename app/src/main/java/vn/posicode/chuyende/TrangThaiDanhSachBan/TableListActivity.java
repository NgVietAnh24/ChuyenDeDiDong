package vn.posicode.chuyende.TrangThaiDanhSachBan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.posicode.chuyende.DanhSachMonAn.DanhSachChonMon;
import vn.posicode.chuyende.Manage.ManageActivity;
import vn.posicode.chuyende.R;

public class TableListActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private BroadcastReceiver updateTableReceiver;
    private List<QueryDocumentSnapshot> originalTableList = new ArrayList<>();
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_list);

        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();

        searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTables(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadTableData();

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ImageButton manageButton = findViewById(R.id.backButton);
        manageButton.setOnClickListener(v -> {
            Intent intent = new Intent(TableListActivity.this, ManageActivity.class);
            startActivity(intent);
        });

        // Đăng ký BroadcastReceiver
        updateTableReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("UPDATE_TABLE_STATUS")) {
                    String tableName = intent.getStringExtra("tableName");
                    String status = intent.getStringExtra("status");
                    updateTableUI(tableName, status);
                }
            }
        };

        IntentFilter filter = new IntentFilter("UPDATE_TABLE_STATUS");
        registerReceiver(updateTableReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký BroadcastReceiver
        unregisterReceiver(updateTableReceiver);
    }

    private void loadTableData() {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);

        firestore.collection("tables")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("TableListActivity", "Lỗi lắng nghe: ", e);
                        return;
                    }

                    if (snapshots != null) {
                        originalTableList.clear();
                        tableListLayout.removeAllViews();
                        List<QueryDocumentSnapshot> tableList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : snapshots) {
                            if (document.getString("name") != null) {
                                tableList.add(document);
                                originalTableList.add(document);
                            }
                        }

                        Collections.sort(tableList, (t1, t2) -> {
                            String name1 = t1.getString("name");
                            String name2 = t2.getString("name");

                            if (name1 == null) return -1;
                            if (name2 == null) return 1;

                            try {
                                String num1 = name1.replaceAll("[^0-9]", "");
                                String num2 = name2.replaceAll("[^0-9]", "");

                                if (num1.isEmpty()) return -1;
                                if (num2.isEmpty()) return 1;

                                return Integer.compare(
                                        Integer.parseInt(num1),
                                        Integer.parseInt(num2)
                                );
                            } catch (NumberFormatException e1) {
                                return name1.compareTo(name2);
                            }
                        });

                        for (QueryDocumentSnapshot document : tableList) {
                            String tableName = document.getString("name");
                            String tableDescription = document.getString("description");
                            String tableStatus = document.getString("status");

                            if (tableName != null && tableDescription != null && tableStatus != null) {
                                addTableToLayout(tableName, tableDescription, tableStatus);
                            }
                        }
                    }
                });
    }

    private void filterTables(String searchText) {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);
        tableListLayout.removeAllViews();

        List<QueryDocumentSnapshot> filteredList = new ArrayList<>();

        for (QueryDocumentSnapshot document : originalTableList) {
            String tableName = document.getString("name");
            String tableDescription = document.getString("description");
            String tableStatus = document.getString("status");

            if (tableName != null &&
                    (tableName.toLowerCase().contains(searchText.toLowerCase()) ||
                            tableDescription.toLowerCase().contains(searchText.toLowerCase()) ||
                            tableStatus.toLowerCase().contains(searchText.toLowerCase()))) {
                filteredList.add(document);
            }
        }

        Collections.sort(filteredList, (t1, t2) -> {
            String name1 = t1.getString("name");
            String name2 = t2.getString("name");

            if (name1 == null) return -1;
            if (name2 == null) return 1;

            try {
                String num1 = name1.replaceAll("[^0-9]", "");
                String num2 = name2.replaceAll("[^0-9]", "");

                if (num1.isEmpty()) return -1;
                if (num2.isEmpty()) return 1;

                return Integer.compare(
                        Integer.parseInt(num1),
                        Integer.parseInt(num2)
                );
            } catch (NumberFormatException e1) {
                return name1.compareTo(name2);
            }
        });

        for (QueryDocumentSnapshot document : filteredList) {
            String tableName = document.getString("name");
            String tableDescription = document.getString("description");
            String tableStatus = document.getString("status");

            if (tableName != null && tableDescription != null && tableStatus != null) {
                addTableToLayout(tableName, tableDescription, tableStatus);
            }
        }
    }

    private void addTableToLayout(String tableName, String tableDescription, String tableStatus) {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);
        View tableView = getLayoutInflater().inflate(R.layout.table_item_tablelist, null);

        TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
        TextView tableDescriptionTextView = tableView.findViewById(R.id.tableDescriptionTextView);
        ImageView tableStatusImage = tableView.findViewById(R.id.tableStatusImage);

        tableNameTextView.setText(tableName);
        tableDescriptionTextView.setText(tableDescription);

        updateTableStatusUI(tableStatusImage, tableStatus);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        tableView.setLayoutParams(params);

        tableView.setOnClickListener(v -> {
            firestore.collection("tables")
                    .whereEqualTo("name", tableName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String currentStatus = document.getString("status");
                            if (currentStatus != null &&
                                    (currentStatus.equals("Trống") ||
                                            currentStatus.equals("Đã đặt") ||
                                            currentStatus.equals("Đang sử dụng"))) {
                                Intent intent = new Intent(TableListActivity.this, DanhSachChonMon.class);
                                intent.putExtra("tableName", tableName);
                                intent.putExtra("tableDescription", tableDescription);
                                intent.putExtra("documentId", document.getId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(this,
                                        "Không thể truy cập thông tin bàn!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Lỗi khi kiểm tra trạng thái bàn",
                                Toast.LENGTH_SHORT).show();
                        Log.e("TableList", "Error checking table status", e);
                    });
        });

        tableListLayout.addView(tableView);
    }

    private void updateTableStatusUI(ImageView statusImage, String status) {
        switch (status) {
            case "Đã đặt":
                statusImage.setImageResource(R.drawable.circle_yellow);
                break;
            case "Đang sử dụng":
                statusImage.setImageResource(R.drawable.circle_red);
                break;
            case "Trống":
            default:
                statusImage.setImageResource(R.drawable.circle_grey);
                break;
        }
    }

    private void updateTableUI(String tableName, String status) {
        LinearLayout tableListLayout = findViewById(R.id.tableListLayout);
        for (int i = 0; i < tableListLayout.getChildCount(); i++) {
            View tableView = tableListLayout.getChildAt(i);
            TextView tableNameTextView = tableView.findViewById(R.id.tableNameTextView);
            if (tableNameTextView.getText().toString().equals(tableName)) {
                ImageView tableStatusImage = tableView.findViewById(R.id.tableStatusImage);
                updateTableStatusUI(tableStatusImage, status);
                break;
            }
        }
    }
}