package vn.posicode.chuyende.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.posicode.chuyende.R;

public class PaymentNotify extends AppCompatActivity {

    TextView txtNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notify);

        txtNotification = findViewById(R.id.textViewNotify);

        Intent intent = getIntent();
        txtNotification.setText(intent.getStringExtra("result"));
    }
}