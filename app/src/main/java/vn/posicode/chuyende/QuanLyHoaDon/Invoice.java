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
    private String hoa_don_id;
    private String id;
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
        this.items = new ArrayList<>();
        this.total = 0;
        this.amountReceived = 0;
        this.change = 0;
        this.paymentStatus = "Chưa thanh toán";
    }

    public Invoice(String hoa_don_id ,String id, String title, String time, String date, List<InvoiceItem> items,
                   String customerName, String customerPhone, String note, String tableId, String staffId) {
        this.hoa_don_id = hoa_don_id;
        this.id = id;
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
        Log.d("Invoice", "Converting document to Invoice: " + document.getId());
        Invoice invoice = new Invoice();

        // Log toàn bộ dữ liệu document để debug
        Log.d("Invoice", "Document data: " + document.getData());

        // Xử lý hoa_don_id
        Object hoaDonIdObj = document.get("hoa_don_id");
        if (hoaDonIdObj != null) {
            invoice.setHoa_don_id(String.valueOf(hoaDonIdObj));
        }

        // Set ID và title
        invoice.setId(document.getId());
        Log.d("Invoice", "Set invoice ID: " + invoice.getId());
        invoice.setTitle("Hóa đơn #" + document.getId().substring(0, 8));

        // Xử lý ngày tạo
        Object ngayTaoObj = document.get("ngay_tao");
        String ngayTao = "";
        if (ngayTaoObj != null) {
            if (ngayTaoObj instanceof com.google.firebase.Timestamp) {
                com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) ngayTaoObj;
                java.util.Date date = timestamp.toDate();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                ngayTao = sdf.format(date);
            } else {
                ngayTao = String.valueOf(ngayTaoObj);
            }
        }

        // Xử lý giờ tạo
        Object gioTaoObj = document.get("gio_tao");
        String gioTao = "";
        if (gioTaoObj != null) {
            if (gioTaoObj instanceof com.google.firebase.Timestamp) {
                com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) gioTaoObj;
                java.util.Date date = timestamp.toDate();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
                gioTao = sdf.format(date);
            } else {
                gioTao = String.valueOf(gioTaoObj);
            }
        }

        invoice.setDate(ngayTao.isEmpty() ? "N/A" : ngayTao);
        invoice.setTime(gioTao.isEmpty() ? "N/A" : gioTao);

        // Xử lý tổng tiền
        Object tongTienObj = document.get("tong_tien");
        if (tongTienObj != null) {
            if (tongTienObj instanceof Double) {
                invoice.setTotal((Double) tongTienObj);
            } else if (tongTienObj instanceof Long) {
                invoice.setTotal(((Long) tongTienObj).doubleValue());
            } else {
                try {
                    invoice.setTotal(Double.parseDouble(String.valueOf(tongTienObj)));
                } catch (NumberFormatException e) {
                    invoice.setTotal(0.0);
                }
            }
        } else {
            invoice.setTotal(0.0);
        }

        // Xử lý tình trạng
        Object tinhTrangObj = document.get("tinh_trang");
        if (tinhTrangObj != null) {
            invoice.setPaymentStatus(String.valueOf(tinhTrangObj));
        } else {
            invoice.setPaymentStatus("Chưa thanh toán");
        }

        // Xử lý ban_id
        Object banIdObj = document.get("ban_id");
        if (banIdObj != null) {
            invoice.setBanId(String.valueOf(banIdObj));
        }

        // Xử lý tên khách hàng
        Object tenKhachHangObj = document.get("ten_khach_hang");
        if (tenKhachHangObj != null) {
            invoice.setCustomerName(String.valueOf(tenKhachHangObj));
        }

        // Xử lý số điện thoại
        Object soDtObj = document.get("so_dt");
        if (soDtObj != null) {
            invoice.setCustomerPhone(String.valueOf(soDtObj));
        }

        // Xử lý ghi chú
        Object ghiChuObj = document.get("ghi_chu");
        if (ghiChuObj != null) {
            invoice.setNote(String.valueOf(ghiChuObj));
        }

        // Xử lý nv_id
        Object nvIdObj = document.get("nv_id");
        if (nvIdObj != null) {
            invoice.setStaffId(String.valueOf(nvIdObj));
        }

        // Xử lý tiền thu
        Object tienThuObj = document.get("tien_thu");
        if (tienThuObj != null) {
            if (tienThuObj instanceof Double) {
                invoice.setAmountReceived((Double) tienThuObj);
            } else if (tienThuObj instanceof Long) {
                invoice.setAmountReceived(((Long) tienThuObj).doubleValue());
            } else {
                try {
                    invoice.setAmountReceived(Double.parseDouble(String.valueOf(tienThuObj)));
                } catch (NumberFormatException e) {
                    invoice.setAmountReceived(0.0);
                }
            }
        }

        // Xử lý items nếu có
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) document.get("items");
        if (itemsData != null) {
            List<InvoiceItem> items = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                InvoiceItem item = new InvoiceItem();

                if (itemData.containsKey("name")) {
                    item.setName(String.valueOf(itemData.get("name")));
                }

                if (itemData.containsKey("quantity")) {
                    Object quantityObj = itemData.get("quantity");
                    if (quantityObj instanceof Long) {
                        item.setQuantity(((Long) quantityObj).intValue());
                    } else if (quantityObj instanceof Integer) {
                        item.setQuantity((Integer) quantityObj);
                    } else {
                        try {
                            item.setQuantity(Integer.parseInt(String.valueOf(quantityObj)));
                        } catch (NumberFormatException e) {
                            item.setQuantity(0);
                        }
                    }
                }

                if (itemData.containsKey("price")) {
                    Object priceObj = itemData.get("price");
                    if (priceObj instanceof Double) {
                        item.setPrice((Double) priceObj);
                    } else if (priceObj instanceof Long) {
                        item.setPrice(((Long) priceObj).doubleValue());
                    } else {
                        try {
                            item.setPrice(Double.parseDouble(String.valueOf(priceObj)));
                        } catch (NumberFormatException e) {
                            item.setPrice(0.0);
                        }
                    }
                }

                if (itemData.containsKey("note")) {
                    item.setNote(String.valueOf(itemData.get("note")));
                }

                items.add(item);
            }
            invoice.setItems(items);
        }

        Log.d("Invoice", "Converted Invoice: " + invoice.toString());
        return invoice;
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

    // Thêm getter và setter cho hoa_don_id
    public String getHoa_don_id() { return hoa_don_id; }
    public void setHoa_don_id(String hoa_don_id) { this.hoa_don_id = hoa_don_id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
        return Objects.equals(hoa_don_id, invoice.hoa_don_id) &&
                Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoa_don_id, id);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "hoa_don_id='" + hoa_don_id + '\'' +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", total=" + total +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", banId='" + banId + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hoa_don_id", hoa_don_id);  // Thêm vào toMap
        map.put("id", id);
        map.put("ngay_tao", date);
        map.put("gio_tao", time);
        map.put("tong_tien", total);
        map.put("tinh_trang", paymentStatus);
        map.put("ban_id", banId);
        map.put("ten_khach_hang", customerName);
        map.put("so_dt", customerPhone);
        map.put("ghi_chu", note);
        map.put("nv_id", staffId);
        map.put("tien_thu", amountReceived);
        // Thêm xử lý cho items nếu cần
        return map;
    }

    public static class InvoiceItem implements Serializable {
        private String name;
        private int quantity;
        private double price;
        private String note;

        public InvoiceItem() {}

        public InvoiceItem(String name, int quantity, double price) {
            this.name = name;
            this .quantity = quantity;
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
            return Objects.equals(name, item.name) &&
                    quantity == item.quantity &&
                    Double.compare(item.price, price) == 0 &&
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