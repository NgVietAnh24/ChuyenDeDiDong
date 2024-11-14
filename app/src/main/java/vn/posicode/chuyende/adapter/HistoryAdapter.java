// HistoryAdapter.java
package vn.posicode.chuyende.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.posicode.chuyende.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<DishHistory> dishHistoryList;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    // Constructor
    public HistoryAdapter(List<DishHistory> dishHistoryList, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.dishHistoryList = dishHistoryList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(DishHistory dishHistory);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DishHistory dishHistory);
    }

    // Tạo ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lichsu, parent, false); // Layout cho từng item
        return new ViewHolder(view);
    }

    // Liên kết dữ liệu với ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DishHistory dishHistory = dishHistoryList.get(position);
        holder.tvFoodName.setText(dishHistory.getTen_mon_an());
        holder.tvThoiGian.setText(dishHistory.getTime());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(dishHistory);
        });

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(dishHistory);
            return true; // Trả về true để cho biết sự kiện đã được xử lý
        });
    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return dishHistoryList.size();
    }

    // Lớp ViewHolder để lưu trữ các view cho từng item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvThoiGian;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
        }
    }
}