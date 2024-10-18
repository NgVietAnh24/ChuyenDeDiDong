package vn.posicode.chuyende;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import vn.posicode.chuyende.activities.Login;
import vn.posicode.chuyende.activities.QLNhanVien;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogout;
    private ImageView imgSkill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imgSkill = findViewById(R.id.btnSkill);
        btnLogout = findViewById(R.id.btnLogout);

        imgSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QLNhanVien.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                // Chuyển hướng người dùng về màn hình đăng nhập (nếu cần)
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();  // Đóng màn hình hiện tại
            }
        });

        mAuth = FirebaseAuth.getInstance();
        String email = "anh2442004@gmail.com";
        String passWord = "anh@123456";

//        mAuth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("Main1", "createUserWithEmail:success");
//                    FirebaseUser user = mAuth.getCurrentUser();
//
//                    Toast.makeText(getApplicationContext(), user.getEmail(), Toast.LENGTH_LONG).show();
//                }else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("Main", "createUserWithEmail:failure", task.getException());
//                    Toast.makeText(MainActivity.this, task.getException().getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
}