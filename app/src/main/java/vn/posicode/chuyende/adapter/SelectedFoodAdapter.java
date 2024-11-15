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

public class SelectedFoodAdapter extends RecyclerView.Adapter<SelectedFoodAdapter.SelectedFoodViewHolder> {
    private Context context;
    private List<SelectedFood> foodList;
    private OnItemClickListener listener;
    private FirebaseFirestore db; // Khai báo FirebaseFirestore

    // Interface để thông báo sự kiện khi nút được nhấn
    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onChooseClick(int position);
        void onTakeClick(int position);
        void onIncreaseClick(int position);
        void onDecreaseClick(int position);
    }

    public SelectedFoodAdapter(Context context, List<SelectedFood> foodList, OnItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
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

        // Load image using Glide
        Glide.with(context).load(food.getHinh_anh()).into(holder.ivFoodImage);



//        holder.btnDelete.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onDeleteClick(position);
//            }
//
//            // Xóa tài liệu từ Firestore
//            db.collection("selected_foods")
//                    .document(food.getMon_an_id()) // Lấy ID của món ăn để xóa
//                    .delete()
//                    .addOnSuccessListener(aVoid -> {
//                        // Xóa item khỏi danh sách và cập nhật RecyclerView
//                        foodList.remove(position);
//                        notifyItemRemoved(position);
//                        Toast.makeText(context, "Đã xóa món ăn khỏi danh sách", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(context, "Lỗi khi xóa món ăn", Toast.LENGTH_SHORT).show();
//                    });
//        });


        // Xử lý sự kiện khi nút tvStatus được nhấn
        holder.tvStatus.setOnClickListener(v -> {
            // Xử lý trạng thái
            String currentStatus = food.getTrang_thai();
            if ("Đã đặt".equals(currentStatus)) {
                food.setTrang_thai("Đang làm");
            } else if ("Đang làm".equals(currentStatus)) {
                food.setTrang_thai("Đã lấy");
            } else if ("Đã lấy".equals(currentStatus)) {
                food.setTrang_thai("Chưa xử lý");
            }

            // Cập nhật lại Firebase với trạng thái mới
            db.collection("selected_foods")
                    .document(food.getMon_an_id())
                    .set(food)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật lại giao diện RecyclerView
                        notifyItemChanged(position);
                        Toast.makeText(context, "Trạng thái đã được cập nhật", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                    });
        });

        // Xử lý sự kiện khi nút được nhấn
//        holder.btnDelete.setOnClickListener(v -> {
//            if (listener != null) listener.onDeleteClick(position);
//        });
        holder.btnChoose.setOnClickListener(v -> {
            if (listener != null) listener.onChooseClick(position);

        });
        holder.btnTake.setOnClickListener(v -> {
            if (listener != null) listener.onTakeClick(position);
        });
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseClick(position);
        });
        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null) listener.onDecreaseClick(position);
        });








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
          //  btnDelete = itemView.findViewById(R.id.btnDelete);
            btnChoose = itemView.findViewById(R.id.btnChoose);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnTake = itemView.findViewById(R.id.btnTake);
        }
    }
}
