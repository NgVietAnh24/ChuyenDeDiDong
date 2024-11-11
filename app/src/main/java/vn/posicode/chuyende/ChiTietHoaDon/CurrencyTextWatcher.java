package vn.posicode.chuyende.ChiTietHoaDon;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyTextWatcher implements TextWatcher {
    private EditText editText;
    private String current = "";
    private Locale vietnameseLocale = new Locale("vi", "VN");
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(vietnameseLocale);

    public CurrencyTextWatcher(EditText editText) {
        this.editText = editText;
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String clean = s.toString().replace(".", "").replace(",", "");

        if (clean.isEmpty()) {
            return;
        }

        try {
            double parsed = Double.parseDouble(clean);
            String formatted = numberFormat.format(parsed);

            if (!formatted.equals(current)) {
                current = formatted;
                editText.removeTextChangedListener(this);
                editText.setText(current);
                editText.setSelection(current.length());
                editText.addTextChangedListener(this);
            }
        } catch (NumberFormatException e) {
            // Xử lý nếu không thể parse
        }
    }

    // Phương thức để lấy giá trị số thực từ text
    public double getNumericValue() {
        try {
            return numberFormat.parse(current.replace(".", "")).doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
