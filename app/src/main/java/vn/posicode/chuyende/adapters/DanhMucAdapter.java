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

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.ViewHolder> {
    List<DanhMuc> list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick (DanhMuc danhMuc);
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
        holder.btnCate.setText(danhMuc.getName());
//        Log.d("A2", "onBindViewHolder: "+danhMuc.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView btnCate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCate = itemView.findViewById(R.id.itemCategory);
        }
    }
}
