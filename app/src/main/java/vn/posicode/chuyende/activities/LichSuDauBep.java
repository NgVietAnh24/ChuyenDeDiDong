package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DauBep;
import vn.posicode.chuyende.adapter.DauBepAdapter;

public class LichSuDauBep extends AppCompatActivity {

    private TextView tvlichsu;
    private RecyclerView rvLichSu;
    private DauBepAdapter dauBepAdapter;
    private List<DauBep> lichSuMonAnList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_nav_daubep);

        rvLichSu = findViewById(R.id.rv_lichsu);
        rvLichSu.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu mẫu
        lichSuMonAnList = new ArrayList<>();
        lichSuMonAnList.add(new DauBep("Bánh mì thịt", "Số lượng: 1", "07:00"));
        lichSuMonAnList.add(new DauBep("Phở bò", "Số lượng: 2", "08:00"));
        lichSuMonAnList.add(new DauBep("Cơm tấm", "Số lượng: 3", "09:00"));

        dauBepAdapter = new DauBepAdapter(lichSuMonAnList);
        rvLichSu.setAdapter(dauBepAdapter);
    }
}
