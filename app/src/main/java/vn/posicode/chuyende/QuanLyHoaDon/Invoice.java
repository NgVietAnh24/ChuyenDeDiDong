package vn.posicode.chuyende.QuanLyHoaDon;

import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Invoice implements Serializable {
    private String id;
    private String hoaDonId;
    private String title;
    private String time;
    private String date;
    private List<InvoiceItem> items;
    private double total;
    private double amountReceived;
    private double change;
    private String paymentStatus;
    private String note;
    private String customerName;
    private String customerPhone;
    private String tableId;
    private String staffId;
    private String banId;
    private String tableName;

    public Invoice() {
        initializeDefaults();
    }

    private void initializeDefaults() {
        this.items = new ArrayList<>();
        this.total = 0;
        this.amountReceived = 0;
        this.change = 0;
        this.paymentStatus = "Chưa thanh toán";
    }

    public Invoice(String id, String title, String time, String date, List<InvoiceItem> items,
                   String customerName, String customerPhone, String note,
                   String tableId, String staffId) {
        this.id = id;
        this.hoaDonId = id;
        this.title = title;
        this.time = time;
        this.date = date;
        this.items = items != null ? items : new ArrayList<>();
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.note = note;
        this.tableId = tableId;
        this.staffId = staffId;
        this.total = calculateTotal();
        this.amountReceived = 0;
        this.change = 0;
        this.paymentStatus = "Chưa thanh toán";
    }

    public static Invoice fromFirestore(DocumentSnapshot document) {
        Invoice invoice = new Invoice();

        // Luôn đặt ID và hoaDonId
        String documentId = document.getId();
        invoice.setId(documentId);

        // Ưu tiên sử dụng hoaDonId từ tài liệu
        String storedHoaDonId = document.getString("hoa_don_id");
        invoice.setHoaDonId(storedHoaDonId != null ? storedHoaDonId : documentId);

        // Tiêu đề hóa đơn
        invoice.setTitle("Hóa đơn #" + documentId.substring(0, 8));

        // Xử lý ngày tạo
        invoice.setDate(parseDate(document.get("ngay_tao")));
        invoice.setTime(parseTime(document.get("gio_tao")));

        // Xử lý tổng tiền
        invoice.setTotal(parseDouble(document.get("tong_tien"), 0.0));

        // Trạng thái thanh toán
        invoice.setPaymentStatus(parseString(document.get("tinh_trang"), "Chưa thanh toán"));

        // Các thông tin khác
        invoice.setBanId(parseString(document.get("ban_id"), null));
        invoice.setCustomerName(parseString(document.get("ten_khach_hang"), null));
        invoice.setCustomerPhone(parseString(document.get("so_dt"), null));
        invoice.setNote(parseString(document.get("ghi_chu"), null));
        invoice.setStaffId(parseString(document.get("nv_id"), null));
        invoice.setAmountReceived(parseDouble(document.get("tien_thu"), 0.0));

        // Xử lý danh sách các mặt hàng
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) document.get("items");
        if (itemsData != null) {
            List<InvoiceItem> items = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                InvoiceItem item = parseInvoiceItem(itemData);
                items.add(item);
            }
            invoice.setItems(items);
        }

        return invoice;
    }

    // Các phương thức hỗ trợ parse
    private static String parseDate(Object dateObj) {
        if (dateObj == null) return "N/A";

        if (dateObj instanceof com.google.firebase.Timestamp) {
            java.util.Date date = ((com.google.firebase.Timestamp) dateObj).toDate();
            return new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);
        }

        return String.valueOf(dateObj);
    }

    private static String parseTime(Object timeObj) {
        if (timeObj == null) return "N/A";

        if (timeObj instanceof com.google.firebase.Timestamp) {
            java.util.Date date = ((com.google.firebase.Timestamp) timeObj).toDate();
            return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
        }

        return String.valueOf(timeObj);
    }

    private static double parseDouble(Object obj, double defaultValue) {
        if (obj == null) return defaultValue;

        try {
            if (obj instanceof Double) return (Double) obj;
            if (obj instanceof Long) return ((Long) obj).doubleValue();
            return Double.parseDouble(String.valueOf(obj));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String parseString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    private static InvoiceItem parseInvoiceItem(Map<String, Object> itemData) {
        InvoiceItem item = new InvoiceItem();

        item.setName(parseString(itemData.get("name"), ""));
        item.setNote(parseString(itemData.get("note"), ""));

        // Parse quantity
        Object quantityObj = itemData.get("quantity");
        item.setQuantity(quantityObj instanceof Number ?
                ((Number) quantityObj).intValue() :
                Integer.parseInt(String.valueOf(quantityObj)));

        // Parse price
        Object priceObj = itemData.get("price");
        item.setPrice(priceObj instanceof Number ?
                ((Number) priceObj).doubleValue() :
                Double.parseDouble(String.valueOf(priceObj)));

        return item;
    }

    // Phương thức chuẩn bị dữ liệu trước khi lưu
    public Map<String, Object> toFirestoreMap() {
        Map<String, Object> map = new HashMap<>();

        // Đảm bảo hoaDonId luôn được set
        if (hoaDonId == null || hoaDonId.isEmpty()) {
            hoaDonId = id;
        }

        // Các trường cơ bản
        map.put("hoa_don_id", hoaDonId);
        map.put("tong_tien", total);
        map.put("tinh_trang", paymentStatus);
        map.put("ban_id", banId);
        map.put("ten_khach_hang", customerName);
        map.put("so_dt", customerPhone);
        map.put("ghi_chu", note);
        map.put("nv_id", staffId);
        map.put("tien_thu", amountReceived);

        // Xử lý danh sách mặt hàng
        List<Map<String, Object>> itemsData = new ArrayList<>();
        for (InvoiceItem item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("name", item.getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("price", item.getPrice());
            itemMap.put("note", item.getNote());
            itemsData.add(itemMap);
        }
        map.put("items", itemsData);

        return map;
    }

    private double calculateTotal() {
        double sum = 0;
        if (items != null) {
            for (InvoiceItem item : items) {
                sum += item.getPrice() * item.getQuantity();
            }
        }
        return sum;
    }

    public void addItem(InvoiceItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        this.total = calculateTotal();
    }

    public void removeItem(InvoiceItem item) {
        if (this.items != null) {
            this.items.remove(item);
            this.total = calculateTotal();
        }
    }

    public void updateItemQuantity(int position, int newQuantity) {
        if (this.items != null && position >= 0 && position < this.items.size()) {
            this.items.get(position).setQuantity(newQuantity);
            this.total = calculateTotal();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHoaDonId() { return hoaDonId; }
    public void setHoaDonId(String hoaDonId) { this.hoaDonId = hoaDonId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) {
        this.items = items;
        this.total = calculateTotal();
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getAmountReceived() { return amountReceived; }
    public void setAmountReceived(double amountReceived) {
        this.amountReceived = amountReceived;
        this.change = amountReceived - total;
    }

    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getBanId() { return banId; }
    public void setBanId(String banId) { this.banId = banId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", hoaDonId='" + hoaDonId + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", banId='" + banId + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    public static class InvoiceItem implements Serializable {
        private String name;
        private int quantity;
        private double price;
        private String note;

        public InvoiceItem() {}

        public InvoiceItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InvoiceItem item = (InvoiceItem) o;
            return quantity == item.quantity &&
                    Double.compare(item.price, price) == 0 &&
                    Objects.equals(name, item.name) &&
                    Objects.equals(note, item.note);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, quantity, price, note);
        }

        @Override
        public String toString() {
            return "InvoiceItem{" +
                    "name='" + name + '\'' +
                    ", quantity=" + quantity +
                    ", price=" + price +
                    ", note='" + note + '\'' +
                    '}';
        }
    }
}