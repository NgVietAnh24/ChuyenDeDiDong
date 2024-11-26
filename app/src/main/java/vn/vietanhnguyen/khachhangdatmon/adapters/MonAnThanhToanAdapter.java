package vn.vietanhnguyen.khachhangdatmon.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import vn.vietanhnguyen.khachhangdatmon.R;
import vn.vietanhnguyen.khachhangdatmon.models.MonAn;

public class MonAnThanhToanAdapter extends RecyclerView.Adapter<MonAnThanhToanAdapter.MonAnViewHolder> {
    private List<MonAn> monAnList;

    public MonAnThanhToanAdapter(List<MonAn> monAnList) {
        this.monAnList = monAnList;
    }

    // Tạo ViewHolder
    @Override
    public MonAnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mon_an_thanh_toan, parent, false);
        return new MonAnViewHolder(view);
    }

    // Liên kết dữ liệu với ViewHolder
    @Override
    public void onBindViewHolder(MonAnViewHolder holder, int position) {
        MonAn monAn = monAnList.get(position);

        long price = Long.parseLong(monAn.getPrice());
        String formattedPrice = NumberFormat.getInstance(Locale.getDefault()).format(price);

        holder.name.setText(monAn.getName());
        holder.price.setText(formattedPrice + " VND");
        holder.quantity.setText("Số lượng: " + monAn.getSoLuong());
        Log.d("K","kkkkkkkkkkkkkkkkkk"+monAn.getSoLuong());
    }


    @Override
    public int getItemCount() {
        return monAnList.size();
    }

    // ViewHolder chứa các views cho mỗi món ăn
    public static class MonAnViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;

        public MonAnViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTenMonAnTT);
            price = itemView.findViewById(R.id.tvGiaMonAnTT);
            quantity = itemView.findViewById(R.id.tvSLM);
        }
    }
}
