package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapter.SelectedFood;
import vn.posicode.chuyende.adapter.SelectedFoodAdapter;

public class MonAnDaChonActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectedFoodAdapter adapter;
    private List<SelectedFood> foodList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsmdachon);

        recyclerView = findViewById(R.id.recyclerViewMon);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodList = new ArrayList<>();
        adapter = new SelectedFoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Get data from Firestore
        db.collection("selected_foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            SelectedFood food = document.toObject(SelectedFood.class);
                            foodList.add(food);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });



    }

}
