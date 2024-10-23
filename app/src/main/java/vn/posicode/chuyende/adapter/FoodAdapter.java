package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

import vn.posicode.chuyende.R;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private OnItemClickListener onItemClickListener; // Thêm interface listener
    Context context;
    // Tạo interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Thêm setter cho listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Constructor
    public FoodAdapter( List<Food> foodList) {
        this.foodList = foodList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.foodPrice.setText("$" + food.getPrice());  // Có thể định dạng lại giá trị tiền tệ nếu cần
//        holder.foodImage.setImageResource(food.getImage());
//        Glide.with(context)
//                .load(food.getImage()) // Đảm bảo imagePath là đường dẫn ảnh dạng chuỗi
//                .into(holder.foodImage);
        String imageUrl = food.getImage(); // Lấy URL hình ảnh từ getImage()

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng AsyncTask để tải ảnh từ URL
            new DownloadImageTask(holder.foodImage).execute(imageUrl);
        } else {
            // Đặt hình ảnh mặc định nếu không có URL
            holder.foodImage.setImageResource(R.drawable.banhmi);
        }
//         Thêm sự kiện click cho từng item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodPrice;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imageViewFood);
            foodName = itemView.findViewById(R.id.textViewName);
            foodPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
