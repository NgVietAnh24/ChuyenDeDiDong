package vn.vietanhnguyen.khachhangdatmon.activities.login_signup_forgot;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.activities.Loading;
import vn.vietanhnguyen.khachhangdatmon.activities.Home;


public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin, btnDangKy;
    private TextView  btnForgot;
    private Loading loading;
    private ImageView imgEye;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Event();

        loading = new Loading(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent!=null) {
            Bundle ex = intent.getExtras();
            if (ex!=null){
                edtUsername.setText(ex.getString("email"));
                edtPassword.setText(ex.getString("password"));
            }}

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

                loading.show();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser  user = mAuth.getCurrentUser ();

                                    // Kiểm tra xem email đã được xác thực chưa
                                    if (user != null && !user.isEmailVerified()) {
                                        loading.cancel();
                                        showAlert("Vui lòng xác thực email của bạn trước khi đăng nhập.");
//                                        Toast.makeText(Login.this, "Vui lòng xác thực email của bạn trước khi đăng nhập.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    firestore.collection("users").document(user.getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        Log.d(TAG, "Document data: " + document.getData());
                                                        if (document.exists()) {
                                                            int role = document.getLong("roles").intValue(); // Lấy role từ Firestore
                                                            loading.cancel();

                                                            if (role == 4) {
                                                                startActivity(new Intent(Login.this, Home.class));
                                                            }
                                                        } else {
                                                            loading.cancel();
                                                            Toast.makeText(Login.this, "Người dùng không tồn tại trong Firestore", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        loading.cancel();
                                                        Log.w(TAG, "Lỗi khi lấy dữ liệu", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    // Đăng nhập thất bại
                                    loading.cancel();
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Tài khoản hoặc mật khẩu không đúng!⚠️", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPass.class);
                startActivity(intent);
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ẩn mật khẩu
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgEye.setImageResource(R.drawable.eye_hide);
        } else {
            // Hiển thị mật khẩu
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imgEye.setImageResource(R.drawable.eye_show);
        }
        isPasswordVisible = !isPasswordVisible;

        // Đưa con trỏ về cuối trường nhập mật khẩu
        edtPassword.setSelection(edtPassword.getText().length());
    }

    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void Event() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnDangKy = findViewById(R.id.btnSignUp);
        btnForgot = findViewById(R.id.btnForgot);
        imgEye = findViewById(R.id.imgEye);
    }
}