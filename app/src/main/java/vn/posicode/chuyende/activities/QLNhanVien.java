package vn.posicode.chuyende.activities;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.adapters.NguoiDungAdapter;
import vn.posicode.chuyende.models.NguoiDung;

public class QLNhanVien extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    FirebaseStorage storage;
    List<NguoiDung> list;
    private NguoiDungAdapter nguoiDungAdapter;
    private RecyclerView listNhanVien;
    private Spinner spVaiTro;
    private AppCompatButton btnThem, btnSua;
    private ImageView imgMatTruocCCCD, imgMatSauCCCD, btnBack;
    private TextInputEditText edtTenDangNhap, edtTenNhanVien, edtMatKhau, edtSoDT, edtSoCCCD, edtNgayCap;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private Uri imageUri;
    private int currentImageView = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ql_nhanvien_layout);
        Event();
        // Khởi tạo biến thư viện Firebase
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");
        list = new ArrayList<>();
        // Khởi tạo Adapter và gán cho RecyclerView
        nguoiDungAdapter = new NguoiDungAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        listNhanVien.setLayoutManager(layoutManager);
        listNhanVien.setAdapter(nguoiDungAdapter);
//        ghiDulieu();

        docDulieu();

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtTenDangNhap.getText().toString();
                String name = edtTenNhanVien.getText().toString();
                String password = edtMatKhau.getText().toString();
                String soDT = edtSoDT.getText().toString();
                String soCCCD = edtSoCCCD.getText().toString();
                String ngayCap = edtNgayCap.getText().toString();
                String selectedRole = spVaiTro.getSelectedItem().toString();

                // Lấy thời gian hiện tại và định dạng nó thành chuỗi ngày giờ theo ý muốn
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String ngayTao = sdf.format(new Date());
                String ngayCapNhat = sdf.format(new Date());

                int role = 0;

                // Gán role dựa trên giá trị của Spinner
                if (selectedRole.equals("Phục vụ")) {
                    role = 1;
                } else if (selectedRole.equals("Thu ngân")) {
                    role = 2;
                } else if (selectedRole.equals("Đầu bếp")) {
                    role = 3;
                }

                // Đăng ký người dùng vào Firebase Authentication
                int finalRole = role;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(QLNhanVien.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid(); // Lấy UID từ Firebase Authentication

                                        // Tạo thông tin người dùng trong Firestore với UID làm ID tài liệu
                                        NguoiDung newUser = new NguoiDung(email, name, soDT, soCCCD, ngayCap, finalRole, ngayTao, ngayCapNhat);

                                        firestore.collection("users").document(uid)
                                                .set(newUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            docDulieu();
                                                            Toast.makeText(QLNhanVien.this, "Thêm thành công ☑️", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.d(TAG, "Error adding document", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(QLNhanVien.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Error registering user", task.getException());
                                }
                            }
                        });
            }
        });


        // Tạo danh sách dữ liệu cho Spinner
        ArrayList<String> spArray = new ArrayList<>();
        spArray.add("Phục vụ");
        spArray.add("Thu ngân");
        spArray.add("Đầu bếp");


        // Tạo Adapter cho Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, spArray);

        // Gán Adapter cho Spinner
        spVaiTro.setAdapter(spinnerArrayAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgMatTruocCCCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog(1);
            }
        });

        imgMatSauCCCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog(2);
            }
        });

    }


    private void showImageSourceDialog(int imageViewIndex) {
        currentImageView = imageViewIndex; // Ghi lại ImageView hiện tại
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hình ảnh từ");
        builder.setItems(new CharSequence[]{"Thư viện", "Camera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    chooseImageFromGallery();
                } else {
                    openCamera();
                }
            }
        });
        builder.show();
    }



    // Chọn ảnh từ thư viện
    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    // Mở camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    // Xử lý kết quả trả về
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                uploadImageToFirebaseStorage();
                if (imageUri != null) {
                    if (currentImageView == 1) {
                        imgMatTruocCCCD.setImageURI(imageUri); // Hiển thị ảnh từ thư viện cho imageView1
                    } else {
                        imgMatSauCCCD.setImageURI(imageUri); // Hiển thị ảnh từ thư viện cho imageView2
                    }
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUriFromBitmap(photo);
                uploadImageToFirebaseStorage();
                if (imageUri != null) {
                    if (currentImageView == 1) {
                        imgMatTruocCCCD.setImageURI(imageUri); // Hiển thị ảnh từ thư viện cho imageView1
                    } else {
                        imgMatSauCCCD.setImageURI(imageUri); // Hiển thị ảnh từ thư viện cho imageView2
                    }
                }
            }
        }
    }

    // Chuyển Bitmap sang Uri
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Camera Image", null);
        return Uri.parse(path);
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            // Khởi tạo Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Tạo tên file unique cho ảnh
            StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

            // Upload ảnh
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Lấy URL của ảnh sau khi upload thành công
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    // URL của ảnh đã upload: imageUrl
                                    Toast.makeText(QLNhanVien.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QLNhanVien.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không có ảnh được chọn", Toast.LENGTH_SHORT).show();
        }
    }


    private void ghiDulieu() {
        CollectionReference users = firestore.collection("users");

        Map<String, Object> user = new HashMap<>();
        user.put("TenDangNhap", "Anh2442004@gmail.com");
        user.put("TenNhanVien", "Nguyễn Việt Anh");
        user.put("SoDT", "035637012");
        user.put("SoCCCD", "037204000021");
        user.put("NgayCap", "24/8");
//        users.document("SE").set(user);
//        Log.e("AAA", "ghiDulieu: " + user);
//
//        Map<String, Object> data2 = new HashMap<>();
//        data2.put("TenDangNhap", "Anh244@gmail.com");
//        data2.put("TenNhanVien", "Việt Anh");
//        data2.put("SoDT", 0365637012);
//        data2.put("SoCCCD",037204000021);
//        data2.put("NgayCap", "24/9");
        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);

// Add a new document with a generated ID
        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private void docDulieu() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NguoiDung userData = document.toObject(NguoiDung.class);
                                list.add(userData);

                            }
                            Log.d(TAG, "Getting documents: ");
                            nguoiDungAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void Event() {
        listNhanVien = findViewById(R.id.listNhanVien);
        spVaiTro = findViewById(R.id.spNhanVien);
        btnThem = findViewById(R.id.btnThem);
        btnSua = findViewById(R.id.btnSua);
        btnBack = findViewById(R.id.btnBack);
        imgMatTruocCCCD = findViewById(R.id.imgMatTruocCCCD);
        imgMatSauCCCD = findViewById(R.id.imgMatSauCCCD);
        edtTenDangNhap = findViewById(R.id.iEdtEmail);
        edtTenNhanVien = findViewById(R.id.iEdtNhanVien);
        edtMatKhau = findViewById(R.id.iEdtMatKhau);
        edtSoDT = findViewById(R.id.iEdtSDT);
        edtSoCCCD = findViewById(R.id.iEdtCCCD);
        edtNgayCap = findViewById(R.id.iEdtNgayCap);
        imgMatSauCCCD = findViewById(R.id.imgMatSauCCCD);
        imgMatTruocCCCD = findViewById(R.id.imgMatTruocCCCD);
    }
}
