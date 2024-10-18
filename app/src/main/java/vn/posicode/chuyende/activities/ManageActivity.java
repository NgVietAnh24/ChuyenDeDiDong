package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class ManageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        // Nút quay lại
        ImageButton backArrowButton = findViewById(R.id.backButton);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn hình TableListActivity
                finish(); // Kết thúc activity hiện tại, quay về activity trước đó
            }
        });

        // Quản lý bàn
        TextView manageTable = findViewById(R.id.manageTableTextView);
        manageTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình quản lý bàn
                Intent intent = new Intent(ManageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
