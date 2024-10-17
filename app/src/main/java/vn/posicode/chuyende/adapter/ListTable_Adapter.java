package vn.posicode.chuyende.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.posicode.chuyende.R;
import vn.posicode.chuyende.models.ListTable;

public class ListTable_Adapter extends RecyclerView.Adapter<ListTable_Adapter.ViewHolder> {
    private ArrayList<ListTable> list_table;

    public ListTable_Adapter(ArrayList<ListTable> list_table) {
        this.list_table = list_table;
    }


    @NonNull
    @Override
    public ListTable_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_table, parent, false);
        return new ListTable_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListTable_Adapter.ViewHolder holder, int position) {
        ListTable tables = list_table.get(position);
        holder.tv_nameTable.setText(tables.getNameTable());
        holder.tv_capacity.setText(tables.getCapacity());
        holder.rad_status.setChecked(Boolean.parseBoolean(tables.getStatus()));

        //Khoi tao mau cho trang thai
        holder.rad_status.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.rad_status.getContext(), R.color.grey)));
        // Dem so lan nhap trang thai
        holder.clickStatus = 0;

        holder.rad_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.clickStatus++;

                switch (holder.clickStatus % 3) {
                    case 1:
                        holder.rad_status.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.rad_status.getContext(), R.color.yellow)));
                        break;
                    case 2:
                        holder.rad_status.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.rad_status.getContext(), R.color.red)));
                        break;
                    case 0:
                        holder.rad_status.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.rad_status.getContext(), R.color.grey)));
                        break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_table.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_nameTable, tv_capacity;
        public RadioButton rad_status;
        public int clickStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_nameTable = itemView.findViewById(R.id.tv_nameTable);
            tv_capacity = itemView.findViewById(R.id.tv_capacity);
            rad_status = itemView.findViewById(R.id.rad_status);
        }

    }
}
