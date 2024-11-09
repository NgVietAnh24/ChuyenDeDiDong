package vn.vietanhnguyen.khachhangdatmon.adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.ViewHolder> {
    List<MonAn> list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(MonAn monAn);
    }

    public MonAnAdapter(List<MonAn> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull MonAnAdapter.ViewHolder holder, int position) {
        MonAn monAn = list.get(position);
        holder.tvTenMon.setText(monAn.getName());
        holder.tvGiaMon.setText(monAn.getPrice() + " VND");
        Glide.with(holder.itemView.getContext())
                .load(monAn.getImage())
                .placeholder(R.drawable.image_error)
                .error(R.drawable.image_error)
                .into(holder.imgFood);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public MonAnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mon_an, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgFood;
        TextView tvTenMon, tvGiaMon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imgFood);
            tvTenMon = itemView.findViewById(R.id.tvTenMonAn);
            tvGiaMon = itemView.findViewById(R.id.tvGiaMonAn);
        }
    }

    public void filterList(List<MonAn> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

}

