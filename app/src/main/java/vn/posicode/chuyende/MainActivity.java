package vn.posicode.chuyende;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.text.Normalizer;
import java.util.regex.Pattern;

import java.util.ArrayList;

import vn.posicode.chuyende.adapter.ListTable_Adapter;
import vn.posicode.chuyende.models.ListTable;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView_table;
    private ListTable_Adapter table_adapter;
    private ArrayList<ListTable> listTable;
    private ArrayList<ListTable> listTableSearch;
    private DrawerLayout drawerLayout;
    private ImageButton btnShowTool, btnBack, btnSearch;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectXML();
//        Them du lieu vao ban
        listTable = new ArrayList<>();
        listTable.add(new ListTable("Bàn 1", "4 - 5 nguoi", "true"));
        listTable.add(new ListTable("Bàn 2", "4 - 8 nguoi", "true"));
        listTable.add(new ListTable("Bàn 9", "4 - 8 nguoi", "true"));

//       Sử dụng danh sách tìm kiêếm
        listTableSearch = new ArrayList<>(listTable);
        table_adapter = new ListTable_Adapter(listTableSearch);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView_table.setAdapter(table_adapter);
        recyclerView_table.setLayoutManager(linearLayout);
//    Hiển thị nav
        btnShowTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
//   Thoat khoi nav

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
//        Tim kiem theo ten ban
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = edtSearch.getText().toString();
                Search(key);
            }
        });
    }
    //    Kết nối với xml
    private void connectXML() {
        recyclerView_table = findViewById(R.id.listTable);
        btnShowTool = findViewById(R.id.btnShowTool);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
    }
    //    Hàm tìm kiếm
    private void Search(String key) {
        listTableSearch.clear();
        String chuanHoaTuKhoa = chuanHoaChuoi(key.toLowerCase());
        if (key.isEmpty()) {
            listTableSearch.addAll(listTable);
        } else {
            for (ListTable table : listTable
            ) {
                String chuanHoaTenBan = chuanHoaChuoi(table.getNameTable().toLowerCase());
                if (chuanHoaTenBan.contains(chuanHoaTuKhoa)) {
                    listTableSearch.add(table);
                }
            }
        }
        table_adapter.notifyDataSetChanged();
    }
    //    hàm chuẩn hóa chuôĩ
    private String chuanHoaChuoi(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
}