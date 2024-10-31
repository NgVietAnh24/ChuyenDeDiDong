package vn.posicode.chuyende.QuanLyHoaDon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.posicode.chuyende.R;

public class InvoiceAdapter extends ArrayAdapter<Invoice> {
    private Context context;
    private int resource;
    private List<Invoice> invoiceList;

    public InvoiceAdapter(@NonNull Context context, int resource, @NonNull List<Invoice> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.invoiceList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Invoice invoice = getItem(position);

        if (invoice != null) {
            TextView titleTextView = convertView.findViewById(R.id.invoiceTitleTextView);
            TextView timeTextView = convertView.findViewById(R.id.invoiceTimeTextView);
            TextView dateTextView = convertView.findViewById(R.id.invoiceDateTextView);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            titleTextView.setText(invoice.getTitle());
            timeTextView.setText(invoice.getTime());
            dateTextView.setText(invoice.getDate());

            deleteButton.setOnClickListener(v -> {
                invoiceList.remove(position);
                notifyDataSetChanged();
            });

            deleteButton.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return true;
            });
        }

        return convertView;
    }
}