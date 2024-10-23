package vn.posicode.chuyende.QuanLyHoaDon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import vn.posicode.chuyende.R;

public class InvoiceAdapter extends ArrayAdapter<Invoice> {
    private Context context;
    private int resource;
    private ArrayList<Invoice> invoiceList;

    public InvoiceAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Invoice> invoiceList) {
        super(context, resource, invoiceList);
        this.context = context;
        this.resource = resource;
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
        }

        // Lấy các thành phần từ layout
        TextView titleTextView = convertView.findViewById(R.id.invoiceTitleTextView);
        TextView timeTextView = convertView.findViewById(R.id.invoiceTimeTextView);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        // Lấy hóa đơn hiện tại
        Invoice currentInvoice = invoiceList.get(position);

        // Set giá trị cho các thành phần
        titleTextView.setText(currentInvoice.getTitle());
        timeTextView.setText(currentInvoice.getTime());

        // Xử lý sự kiện khi nhấn nút xóa
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa hóa đơn khỏi danh sách
                invoiceList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Đã xóa " + currentInvoice.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
