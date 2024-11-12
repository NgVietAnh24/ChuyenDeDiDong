package vn.posicode.chuyende.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vn.posicode.chuyende.R;
import java.util.List;

public class DauBepAdapter extends RecyclerView.Adapter<DauBepAdapter.DauBepViewHolder> {


    private List<DauBep> dauBepItemList;

    public DauBepAdapter(List<DauBep> dauBepItemList) {
        this.dauBepItemList = dauBepItemList;



}



    @NonNull
    @Override
    public  DauBepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daubep,parent,false);

        return new DauBepViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DauBepViewHolder holder, int position) {
        DauBep item = dauBepItemList.get(position);
        holder.tvTen.setText(item.getTen());
        holder.tvSoLuong.setText(item.getSoLuong());
        holder.tvThoiGian.setText(item.getThoiGian());
        holder.imageAnh.setImageResource(R.drawable.banhmi); // đặt ảnh mẫu
    }

    @Override
    public int getItemCount() {
        return dauBepItemList.size();
    }

    public static class DauBepViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvSoLuong, tvThoiGian;
        ImageView imageAnh;

        public DauBepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            imageAnh = itemView.findViewById(R.id.imageAnh);
        }
    }
}

