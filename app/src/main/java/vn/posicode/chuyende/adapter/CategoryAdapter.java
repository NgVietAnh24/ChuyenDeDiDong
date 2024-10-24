package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import vn.posicode.chuyende.R;

public class CategoryAdapter extends ArrayAdapter<String> {
    private final Context context; // Đối tượng Context
    private final List<String> categories; // Danh sách các danh mục

    // Constructor
    public CategoryAdapter(Context context, List<String> categories) {
        super(context, R.layout.item_category, categories); // Gọi đến constructor của ArrayAdapter
        this.context = context;  // Lưu context
        this.categories = categories;   // Lưu danh sách danh mục
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Kiểm tra nếu convertView chưa được khởi tạo
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // Lấy LayoutInflater từ context
            convertView = inflater.inflate(R.layout.item_category, parent, false);
        }

        TextView textViewCategoryName = convertView.findViewById(R.id.textViewCategoryName); // Khởi tạo TextView
        textViewCategoryName.setText(categories.get(position)); // Hiển thị tên danh mục

        return convertView;
    }



}
