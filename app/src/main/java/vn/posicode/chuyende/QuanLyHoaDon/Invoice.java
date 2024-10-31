package vn.posicode.chuyende.QuanLyHoaDon;

import java.io.Serializable;
import java.util.List;

public class Invoice implements Serializable {
    private String title;
    private String time;
    private String date;
    private List<InvoiceItem> items;

    public Invoice(String title, String time, String date, List<InvoiceItem> items) {
        this.title = title;
        this.time = time;
        this.date = date;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    public static class InvoiceItem implements Serializable {
        private String name;
        private int quantity;
        private double price;

        public InvoiceItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}