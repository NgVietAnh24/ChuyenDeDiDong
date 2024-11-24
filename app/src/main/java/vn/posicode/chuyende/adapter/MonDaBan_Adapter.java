package vn.posicode.chuyende.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.MonAnModel;
import vn.posicode.chuyende.models.MonDaBanModels;

public class MonDaBan_Adapter extends RecyclerView.Adapter<MonDaBan_Adapter.ViewHolder> {
    private ArrayList<MonDaBanModels> list_monDaBan;

    public MonDaBan_Adapter(ArrayList<MonDaBanModels> listMonDaBan) {
       this.list_monDaBan = listMonDaBan;
    }

    @NonNull
    @Override
    public MonDaBan_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_thongkemondaban, parent, false);
        return new MonDaBan_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonDaBan_Adapter.ViewHolder holder, int position) {
        MonDaBanModels monAnModel = list_monDaBan.get(position);
        holder.tvTenMon.setText(monAnModel.getTenMon());
        holder.tvSoLuong.setText(String.valueOf(monAnModel.getSoLuong()));
        holder.tvTongTien.setText(String.valueOf(monAnModel.getTongTien()));
        holder.tvNgay.setText(monAnModel.getNgay());
    }

    @Override
    public int getItemCount() {
        return list_monDaBan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenMon, tvSoLuong, tvTongTien, tvNgay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tv_tenMonDaBan);
            tvSoLuong = itemView.findViewById(R.id.tv_soLuongDaBan);
            tvTongTien = itemView.findViewById(R.id.tv_TongTienDaBan);
            tvNgay = itemView.findViewById(R.id.tv_NgayDaBan);
        }
    }
}
