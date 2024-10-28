package vn.posicode.chuyende.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.activities.CategoryModel;

public class CategoryAdapter extends ArrayAdapter<CategoryModel> {
    private final Context context; // Đối tượng Context
    private final List<CategoryModel> categories; // Danh sách các danh mục

    // Constructor
    public CategoryAdapter(Context context, List<CategoryModel> categories) {
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

        // Lấy đối tượng CategoryModel tại vị trí `position`
        CategoryModel category = categories.get(position);

        // Khởi tạo TextView và hiển thị tên danh mục
        TextView textViewCategoryName = convertView.findViewById(R.id.textViewCategoryName);
        textViewCategoryName.setText(category.getName());

        return convertView;
    }
}
