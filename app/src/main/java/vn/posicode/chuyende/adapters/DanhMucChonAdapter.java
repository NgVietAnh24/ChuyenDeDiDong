package vn.posicode.chuyende.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.DanhMuc;

public class DanhMucChonAdapter extends RecyclerView.Adapter<DanhMucChonAdapter.ViewHolder> {
    private final List<DanhMuc> list;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DanhMuc danhMuc);
    }

    public DanhMucChonAdapter(List<DanhMuc> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DanhMucChonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danh_muc_chon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhMucChonAdapter.ViewHolder holder, int position) {
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
