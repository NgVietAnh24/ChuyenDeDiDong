package vn.posicode.chuyende.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.MonDaChonModel;

public class MonDaChon_Adapter extends RecyclerView.Adapter<MonDaChon_Adapter.ViewHolder> {
    private ArrayList<MonDaChonModel> selectedItems;
    private Context context;

    //    Tạo phương thức callback
    public MonDaChon_Adapter(Context context, ArrayList<MonDaChonModel> selectedItems) {
        this.selectedItems = selectedItems;
        this.context = context;
    }


    public ArrayList<MonDaChonModel> getUpdatedList() {
        return selectedItems;
    }

    @NonNull
    @Override
    public MonDaChon_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_mondachon, parent, false);
        return new MonDaChon_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MonDaChonModel monAnModel = selectedItems.get(position);
        holder.tv_tenMonDaChon.setText(monAnModel.getTenMon());
        holder.tv_giaMonDaChon.setText(monAnModel.getThanhTien());
        holder.tv_SoLuongMonDaChon.setText(String.valueOf(monAnModel.getSoLuong()));

        String imageUrl = monAnModel.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_monan)
                    .error(R.drawable.icon_monan)
                    .into(holder.imv_MonAnDaChon);
        } else {
            holder.imv_MonAnDaChon.setImageResource(R.drawable.icon_monan);
        }

//        Xủ lí tăng giảm số lượng
        holder.btn_decreaseMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int soLuong = monAnModel.getSoLuong();
                if (soLuong > 1) {
                    soLuong--;
                    monAnModel.setSoLuong(soLuong);
                    selectedItems.set(position, monAnModel);
                    holder.tv_SoLuongMonDaChon.setText(String.valueOf(soLuong));
                    Log.d("MonDaChon", "Số lượng sau khi giảm: " + soLuong);
                    notifyItemChanged(position);
                } else {
                    selectedItems.remove(monAnModel);
                    notifyDataSetChanged();
                }
            }
        });
        holder.btn_increaseMonDaChon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int soLuong = monAnModel.getSoLuong();
                if (soLuong >= 1) {
                    soLuong++;
                    monAnModel.setSoLuong(soLuong);
                    selectedItems.set(position, monAnModel);
                    holder.tv_SoLuongMonDaChon.setText(String.valueOf(soLuong));
                    Log.d("MonDaChon", "Số lượng sau khi giảm: " + soLuong);
                    notifyItemChanged(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imv_MonAnDaChon;
        private TextView tv_tenMonDaChon, tv_giaMonDaChon, tv_SoLuongMonDaChon, btn_decreaseMonDaChon, btn_increaseMonDaChon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_MonAnDaChon = itemView.findViewById(R.id.imv_MonAnDaChon);
            tv_tenMonDaChon = itemView.findViewById(R.id.tv_tenMonDaChon);
            tv_giaMonDaChon = itemView.findViewById(R.id.tv_giaMonDaChon);
            tv_SoLuongMonDaChon = itemView.findViewById(R.id.tv_SoLuongMonDaChon);
            btn_decreaseMonDaChon = itemView.findViewById(R.id.btn_decreaseMonDaChon);
            btn_increaseMonDaChon = itemView.findViewById(R.id.btn_increaseMonDaChon);
        }

    }
}
