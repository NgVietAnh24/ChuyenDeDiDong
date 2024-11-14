package vn.vietanhnguyen.khachhangdatmon.activities.login_signup_forgot;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

import vn.vietanhnguyen.khachhangdatmon.R;


public class ForgotPass extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnForGot;
    private ImageView btnBack;
    private EditText edtEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        btnForGot = findViewById(R.id.btnGui);
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtNhapEmail);
        mAuth = FirebaseAuth.getInstance();

        btnForGot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                if (email.isEmpty()) {
                    showAlert("Email không được bỏ trống!");
                    return;
                }

                // Kiểm tra xem email có tồn tại trong Firebase Authentication hay không
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();

                            // Nếu danh sách signInMethods rỗng, email không tồn tại
                            if (signInMethods == null || signInMethods.isEmpty()) {
                                Toast.makeText(ForgotPass.this, "Email không tồn tại trong hệ thống.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Nếu email tồn tại, kiểm tra xem người dùng đã xác thực chưa
                            mAuth.signInWithEmailAndPassword(email, "dummyPassword").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null && !user.isEmailVerified()) {
                                            showAlert("Email chưa được xác thực. Vui lòng xác thực email trước khi đặt lại mật khẩu.");
                                            return;
                                        }

                                        // Gửi email đặt lại mật khẩu
                                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    showAlert("Chúng tôi đã gửi thông tin đến hộp thư trong mail của bạn để đổi mật khẩu!");
                                                } else {
                                                    Toast.makeText(ForgotPass.this, "Không thể gửi mail. Hãy kiểm tra lại địa chỉ email", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ForgotPass.this, "Không thể xác thực người dùng. Hãy kiểm tra lại địa chỉ email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ForgotPass.this, "Không thể kiểm tra email. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> {
                    Intent intent = new Intent(ForgotPass.this, Login.class);
                    startActivity(intent);
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}