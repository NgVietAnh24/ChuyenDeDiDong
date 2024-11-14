package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.MonAn_Adapter;
import vn.posicode.chuyende.adapter.MonDaChon_Adapter;
import vn.posicode.chuyende.models.MonAnModel;
import vn.posicode.chuyende.models.MonDaChonModel;

public class MonDaChonActivity extends AppCompatActivity {
    private RecyclerView recMonDaChon;
    private MonDaChon_Adapter monDaChonAdapter;
    private ArrayList<MonDaChonModel> monDaChonList;
    private ImageButton btnBack;
    private ArrayList<MonDaChonModel> updatedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_da_chon);
        connectXML();
        monDaChonList = new ArrayList<>();
        // Nhận dữ liệu từ màn hình trước (MonDaChonModel list)
        Intent intent = getIntent();
        monDaChonList = intent.getParcelableArrayListExtra("monAnDaChon");

        // Hiển thị danh sách món ăn đã chọn
        monDaChonAdapter = new MonDaChon_Adapter(MonDaChonActivity.this, monDaChonList);
        recMonDaChon.setAdapter(monDaChonAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recMonDaChon.setLayoutManager(linearLayout);

//        back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int totalQuantity = 0;
                for (MonDaChonModel item : monDaChonList) {
                    if (item.getSoLuong() > 0) {
                        totalQuantity += item.getSoLuong(); // Cộng dồn số lượng của món
                    }
                }
                // Trả về danh sách đã cập nhật
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("updatedMonDaChon", monDaChonList);
                resultIntent.putExtra("totalQuantity", totalQuantity);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    private void connectXML() {
        recMonDaChon = findViewById(R.id.listMonAnDaChon);
        btnBack = findViewById(R.id.btnBack);
    }
}