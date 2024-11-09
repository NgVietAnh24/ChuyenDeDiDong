package vn.vietanhnguyen.khachhangdatmon.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.models.DanhMuc;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.ViewHolder> {
    private final List<DanhMuc> list;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DanhMuc danhMuc);
    }

    public DanhMucAdapter(List<DanhMuc> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DanhMucAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danh_muc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhMucAdapter.ViewHolder holder, int position) {
        DanhMuc danhMuc = list.get(position);
        if (danhMuc != null) {
            holder.btnCate.setText(danhMuc.getName());
            Log.d("A2", "onBindViewHolder: " + danhMuc.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(danhMuc);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView btnCate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCate = itemView.findViewById(R.id.itemCategory);
        }
    }
}
