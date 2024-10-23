package vn.posicode.chuyende.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import vn.posicode.chuyende.R;

public class CategoryItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_item);

//        // Thiết lập tiêu đề cho Activity
//        TextView title = findViewById(R.id.category_itemID);
//        title.setText("Quản lý danh mục con");

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
