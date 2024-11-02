package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapters.BanAdapter;
import vn.posicode.chuyende.adapters.DanhMucAdapter;
import vn.posicode.chuyende.adapters.MonAnAdapter;
import vn.posicode.chuyende.models.Ban;
import vn.posicode.chuyende.models.DanhMuc;
import vn.posicode.chuyende.models.MonAn;
import vn.posicode.chuyende.models.NguoiDung;

public class DatMonAn extends AppCompatActivity {
    FirebaseFirestore firestore;

    List<MonAn> listMonAn;
    List<DanhMuc> listDanhMuc;
    MonAnAdapter monAnAdapter;
    DanhMucAdapter danhMucAdapter;

    private EditText edtSearch;
    private RecyclerView listCategory, listFood;
    private AppCompatButton btnMonDaChon, btnDatTruoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dat_mon_an_layout);
        Event();


        //Khởi tạo từ firebase
        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutMonAn = new LinearLayoutManager(this);
        layoutMonAn.setOrientation(RecyclerView.VERTICAL);
        listFood.setLayoutManager(layoutMonAn);

        LinearLayoutManager layoutDanhMuc = new LinearLayoutManager(this);
        layoutDanhMuc.setOrientation(RecyclerView.VERTICAL);
        listCategory.setLayoutManager(layoutDanhMuc);
        //
        listMonAn = new ArrayList<>();
        monAnAdapter = new MonAnAdapter(listMonAn, new MonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MonAn monAn) {

            }
        });
        listFood.setAdapter(monAnAdapter);

        listDanhMuc = new ArrayList<>();
        danhMucAdapter = new DanhMucAdapter(listDanhMuc, new DanhMucAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (DanhMuc danhMuc) {

            }
        });
        listCategory.setAdapter(danhMucAdapter);
        docDulieuDanhMuc();
//        docDulieuMonAn();
    }
    public void docDulieuDanhMuc() {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listDanhMuc.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DanhMuc cateData = document.toObject(DanhMuc.class);
                                listDanhMuc.add(cateData);
                                Log.d("VVV", "getData: " + listDanhMuc.add(cateData));
                            }
                            Log.d("Getd", "Getting documents: ");
                            monAnAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void docDulieuMonAn() {
        firestore.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listMonAn.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MonAn foodData = document.toObject(MonAn.class);
                                listMonAn.add(foodData);
                                Log.d("VVV", "getData: " + listMonAn.add(foodData));
                            }
                            Log.d("Get", "Getting documents: ");
                            monAnAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void Event() {
        edtSearch = findViewById(R.id.edtSearch);
        listCategory = findViewById(R.id.listCategory);
        listFood = findViewById(R.id.listFood);
        btnMonDaChon = findViewById(R.id.btnMonDaChon);
        btnDatTruoc = findViewById(R.id.btnDatTruoc);
    }
}