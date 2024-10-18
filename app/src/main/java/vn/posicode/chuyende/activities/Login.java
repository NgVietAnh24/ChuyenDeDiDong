package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vn.posicode.chuyende.MainActivity;
import vn.posicode.chuyende.R;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin, btnForgot;
    private Loading loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Event();

        loading = new Loading(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                // Kiểm tra điều kiện
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Không được bỏ trống!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValidEmail(email)) {
                    Toast.makeText(Login.this, "Địa chỉ email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng nhập người dùng
                loading.show();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Lấy thông tin người dùng từ Firestore sau khi đăng nhập thành công
                                    firestore.collection("users").document(user.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Log.d(TAG, "Document data: ");
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Document data: ");
                                                        DocumentSnapshot document = task.getResult();
                                                        Log.d(TAG, "Document data: " + document.getData());
                                                        if (document.exists()) {
                                                            int role = document.getLong("roles").intValue(); // Lấy role từ Firestore
                                                            loading.cancel();

                                                            // Điều hướng người dùng dựa trên role
                                                            switch (role) {
                                                                case 0:
                                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                                    break;
                                                                case 1:
                                                                    startActivity(new Intent(Login.this, HomeNhanVien.class));
                                                                    break;
                                                                case 2:
                                                                    startActivity(new Intent(Login.this, HomeThuNgan.class));
                                                                    break;
                                                                case 3:
                                                                    startActivity(new Intent(Login.this, HomeDauBep.class));
                                                                    break;
                                                                default:
                                                                    // Vai trò không xác định, quay về màn hình chính
                                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                                    break;
                                                            }
                                                        } else {
                                                            loading.cancel();
                                                            Toast.makeText(Login.this, "Người dùng không tồn tại trong Firestore", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        loading.cancel();
                                                        Log.w(TAG, "Error getting user data", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    // Đăng nhập thất bại
                                    loading.cancel();
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void Event() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgot = findViewById(R.id.btnForgot);
    }
}
