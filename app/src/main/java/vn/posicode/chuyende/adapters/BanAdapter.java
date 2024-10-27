package vn.posicode.chuyende.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.Ban;


public class BanAdapter extends RecyclerView.Adapter<BanAdapter.ViewHolder> {
    private List<Ban> list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(Ban ban);
    }

    public BanAdapter(List<Ban> list, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull BanAdapter.ViewHolder holder, int position) {
        Ban ban = new Ban();
        holder.tvTenBan.setText(ban.getTenBan());
        holder.tvMoTa.setText(ban.getMoTa());
        if(ban.getState() == 0){
            holder.vState.setBackgroundColor(R.drawable.rounded_trang_thai);
        }else if(ban.getState() == 1){
            holder.vState.setBackgroundColor(R.drawable.rounded_trang_thai_da_dat);
        }else if(ban.getState() == 2){
            holder.vState.setBackgroundColor(R.drawable.rounded_trang_thai_da_dat);
        }
    }

    @NonNull
    @Override
    public BanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBan, tvMoTa;
        View vState;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBan = itemView.findViewById(R.id.tvTenBan);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            vState = itemView.findViewById(R.id.vState);
        }
    }
}
