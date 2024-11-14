package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.DauBepFoodActivity;

public class DauBepFoodAdapter extends RecyclerView.Adapter<DauBepFoodAdapter.ViewHolder> {
    private List<DauBep_Food> selectedFoodList;
    private DauBepFoodActivity activity;
  //  private Context context; // Thêm biến Context

    public DauBepFoodAdapter(List<DauBep_Food> selectedFoodList, DauBepFoodActivity activity) {
        this.selectedFoodList = selectedFoodList;
        this.activity = activity;
       // this.context = activity; // Gán context từ activity
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daubep, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DauBep_Food food = selectedFoodList.get(position);

        // Set data cho các TextView và Button
        holder.tvTen.setText(food.getTen_mon_an());
        holder.tvSoLuong.setText("Số lượng: " + food.getSo_luong());
        holder.tvThoiGian.setText(food.getTime());

        // Set hình ảnh nếu có URL
        if (food.getHinh_anh() != null && !food.getHinh_anh().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(food.getHinh_anh()) // Đảm bảo URL là hợp lệ
                    .into(holder.imageAnh);
        }

        // Xử lý sự kiện cho các nút bấm nếu cần
        holder.btnDaxong.setOnClickListener(v -> {
           // activity.setSelectedFoodId(food.getMon_an_id());  // Lưu foodId vào selectedFoodId
            activity.showQuantityDialog(food.getMon_an_id());
        });

        holder.btnDangLam.setOnClickListener(v -> {
            // Xử lý khi nhấn nút "Đang làm"
            activity.updateFoodStatus(food.getMon_an_id(), "Đang làm");
        });
    }


    @Override
    public int getItemCount() {
        return selectedFoodList.size();
    }
    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvSoLuong, tvThoiGian;
        Button btnDaxong, btnDangLam;
        ImageView imageAnh;

        public ViewHolder(View itemView) {
            super(itemView);

            // Ánh xạ các thành phần từ item_daubep layout
            tvTen = itemView.findViewById(R.id.tvTen);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            btnDaxong = itemView.findViewById(R.id.btnDaxong);
            btnDangLam = itemView.findViewById(R.id.btnDangLam);
            imageAnh = itemView.findViewById(R.id.imageAnh);
        }
    }
}
