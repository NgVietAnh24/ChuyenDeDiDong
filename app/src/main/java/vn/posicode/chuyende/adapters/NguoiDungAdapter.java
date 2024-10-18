package vn.posicode.chuyende.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.QLNhanVien;
import vn.posicode.chuyende.models.NguoiDung;

public class NguoiDungAdapter extends RecyclerView.Adapter<NguoiDungAdapter.ViewHolder> {
    List<NguoiDung> list;

    public NguoiDungAdapter(List<NguoiDung> list) {
        this.list = list;
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
        if(user.getRoles() == 0){
            holder.tvRole.setText("Quản lý");
        } else if (user.getRoles() == 1) {
            holder.tvRole.setText("Phục vụ");
        }else if (user.getRoles() == 2) {
            holder.tvRole.setText("Thu ngân");
        }else {
            holder.tvRole.setText("Đầu bếp");
        }
        holder.tvNgayTao.setText(user.getNgayTao());
        holder.tvNgayCapNhat.setText(user.getNgayCapNhat());
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Chưa xóa được ⚠️",Toast.LENGTH_SHORT).show();
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
}
