package vn.posicode.chuyende.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.CategoryModel;

public class Category_Adapter extends RecyclerView.Adapter<Category_Adapter.ViewHolder> {
    private ArrayList<CategoryModel> list_cate;

    public Category_Adapter(ArrayList<CategoryModel> list_cate) {
        this.list_cate = list_cate;
    }

    @NonNull
    @Override
    public Category_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_danhmuc, parent, false);
        return new Category_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Category_Adapter.ViewHolder holder, int position) {
        CategoryModel cate = list_cate.get(position);
        holder.btnChonDanhMuc.setText(cate.getName());
    }

    @Override
    public int getItemCount() {
        return list_cate.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      private Button btnChonDanhMuc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnChonDanhMuc = itemView.findViewById(R.id.btnChonDanhMuc);
        }

    }
}
