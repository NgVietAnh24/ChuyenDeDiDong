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
    private final Context context;
    private final List<String> categories;


    public CategoryAdapter(Context context, List<String> categories) {
        super(context, R.layout.item_category, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_category, parent, false);
        }

        TextView textViewCategoryName = convertView.findViewById(R.id.textViewCategoryName);
        textViewCategoryName.setText(categories.get(position)); // Hiển thị tên danh mục

        return convertView;
    }



}
