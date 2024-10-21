package vn.posicode.chuyende.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.QLNhanVien;
import vn.posicode.chuyende.models.NguoiDung;

public class NguoiDungAdapter extends RecyclerView.Adapter<NguoiDungAdapter.ViewHolder> {
    List<NguoiDung> list;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(NguoiDung user);
    }

    public NguoiDungAdapter(List<NguoiDung> list, OnItemClickListener listener) {
        this.list = list;
        this.onItemClickListener = listener;
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nguoidung, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NguoiDungAdapter.ViewHolder holder, int position) {
        NguoiDung user = list.get(position);
        holder.tvName.setText(user.getTenNhanVien());
        if (user.getRoles() == 0) {
            holder.tvRole.setText("Quản lý");
        } else if (user.getRoles() == 1) {
            holder.tvRole.setText("Phục vụ");
        } else if (user.getRoles() == 2) {
            holder.tvRole.setText("Thu ngân");
        } else {
            holder.tvRole.setText("Đầu bếp");
        }
        holder.tvNgayTao.setText(user.getNgayTao());
        holder.tvNgayCapNhat.setText(user.getNgayCapNhat());
//        if (position == selectedPosition) {
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
//        } else {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_background));
//        }
//        int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                selectedPosition = pos;
//                notifyDataSetChanged();
                onItemClickListener.onItemClick(user);
            }
        });

        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(user.getUid(), holder.itemView.getContext()); // Truyền context
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvNgayTao, tvNgayCapNhat;
        Button btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTen);
            tvRole = itemView.findViewById(R.id.tvVaiTro);
            tvNgayTao = itemView.findViewById(R.id.tvNgayTao);
            tvNgayCapNhat = itemView.findViewById(R.id.tvNgayCapNhat);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

    private void showDeleteConfirmationDialog(String uid, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa người dùng");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng này?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(uid, context); // Gọi phương thức xóa và truyền context
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng hộp thoại
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUser(String uid, Context context) {
        user = mAuth.getCurrentUser();
        firestore.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Xóa thành công ☑️", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("NguoiDungAdapter", "Error deleting user from Auth", e);
                                    }
                                });
                        // Cập nhật danh sách người dùng
                        ((QLNhanVien) context).docDulieu();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
