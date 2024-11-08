package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.posicode.chuyende.R;

public class ChonThanhToan extends AppCompatActivity {
    private Button btnTT;
    private EditText edtSL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_thanh_toan);
        btnTT = findViewById(R.id.btnTT);
        edtSL = findViewById(R.id.edtSL);

        btnTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSL.getText() == null || edtSL.getText().toString().isEmpty()){
                    Toast.makeText(ChonThanhToan.this, "Nhập số lượng muốn mua", Toast.LENGTH_SHORT).show();
                    return;
                }

                String soLuongString = edtSL.getText().toString();
                double total = Double.parseDouble(soLuongString) * (double) 1000000;
                Intent intent = new Intent(ChonThanhToan.this, ThanhToan.class);
                intent.putExtra("soluong", edtSL.getText().toString());
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });

    }
}