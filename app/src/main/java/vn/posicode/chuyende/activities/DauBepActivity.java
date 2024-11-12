package vn.posicode.chuyende.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.DauBepAdapter;
import vn.posicode.chuyende.adapter.DauBep; // Import lớp DauBep từ gói adapter

public class DauBepActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DauBepAdapter dauBepAdapter;
    private List<DauBep> dauBepList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homedaubep);

        recyclerView = findViewById(R.id.listDauBep);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sử dụng lớp DauBep từ gói adapter
        dauBepList = new ArrayList<>();
        dauBepList.add(new DauBep("Bánh mì thịt", "Số lượng: 1", "07:00"));
        dauBepList.add(new DauBep("Phở bò", "Số lượng: 2", "08:00"));
        dauBepList.add(new DauBep("Cơm tấm", "Số lượng: 3", "09:00"));

        dauBepAdapter = new DauBepAdapter(dauBepList);
        recyclerView.setAdapter(dauBepAdapter);
    }
}
