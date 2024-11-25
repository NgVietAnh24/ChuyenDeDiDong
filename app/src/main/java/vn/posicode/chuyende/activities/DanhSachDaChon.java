package vn.posicode.chuyende.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.posicode.chuyende.R;

public class DanhSachDaChon extends AppCompatActivity {
    private AppCompatButton btnMonDaChon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.danh_sach_da_chon_layout);
        Event();
    }

    private void Event() {
    }
}