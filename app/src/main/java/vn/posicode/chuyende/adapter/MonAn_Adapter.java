package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.MonAnModel;

public class MonAn_Adapter extends RecyclerView.Adapter<MonAn_Adapter.ViewHolder> {
    private ArrayList<MonAnModel> list_MonAn= new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public MonAn_Adapter(Context context, ArrayList<MonAnModel> list_MonAn) {
        if (list_MonAn != null) {
            this.list_MonAn = list_MonAn; // Only set if not null
        }
        this.context = context;
    }
    public interface OnItemClickListener {
        void onItemClick(String foodId); // Pass the food ID or any other data you need
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MonAn_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_monan, parent, false);
        return new MonAn_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonAn_Adapter.ViewHolder holder, int position) {
        MonAnModel monan = list_MonAn.get(position);
        holder.tv_tenMon.setText(monan.getName());
        holder.tv_giaMon.setText(monan.getPrice());
        String imageUrl = monan.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_monan)
                    .error(R.drawable.icon_monan)
                    .into(holder.imv_MonAn);
        } else {
            holder.imv_MonAn.setImageResource(R.drawable.icon_monan);
        }
// Lấy id món đã chọn
        String foodId = monan.getId();
        if (foodId != null && !foodId.isEmpty()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(foodId);
                    }
                }
            });
        } else {
            Log.e("MonAn_Adapter", "ID món ăn không hợp lệ tại vị trí " + position);
        }
    }

    @Override
    public int getItemCount() {
        if (list_MonAn == null) {
            return 0;
        }
        return list_MonAn.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imv_MonAn;
        private TextView tv_tenMon, tv_giaMon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_MonAn = itemView.findViewById(R.id.imv_MonAn);
            tv_tenMon = itemView.findViewById(R.id.tv_tenMon);
            tv_giaMon = itemView.findViewById(R.id.tv_giaMon);
        }

    }
}
