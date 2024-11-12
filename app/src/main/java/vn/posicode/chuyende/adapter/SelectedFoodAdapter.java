package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.posicode.chuyende.R;

public class SelectedFoodAdapter extends RecyclerView.Adapter<SelectedFoodAdapter.SelectedFoodViewHolder> {
    private Context context;
    private List<SelectedFood> foodList;

    public SelectedFoodAdapter(Context context, List<SelectedFood> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @Override
    public SelectedFoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danhsachmondachon, parent, false);
        return new SelectedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedFoodViewHolder holder, int position) {
        SelectedFood food = foodList.get(position);

        holder.tvFoodName.setText(food.getTen_mon_an());
        holder.tvFoodPrice.setText(String.format("$%d", food.getGia()));
        holder.tvStatus.setText(food.getTrang_thai());
        holder.tvQuantity.setText(String.valueOf(food.getSo_luong()));

        // Load image using Glide or Picasso
        Glide.with(context).load(food.getHinh_anh()).into(holder.ivFoodImage);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class SelectedFoodViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodName, tvFoodPrice, tvStatus, tvQuantity;
        ImageView ivFoodImage;
        Button btnDelete, btnChoose, btnDecrease, btnIncrease, btnTake;

        public SelectedFoodViewHolder(View itemView) {
            super(itemView);

            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnChoose = itemView.findViewById(R.id.btnChoose);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnTake = itemView.findViewById(R.id.btnTake);
        }
    }
}
