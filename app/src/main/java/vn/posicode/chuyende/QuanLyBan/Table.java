package vn.posicode.chuyende.QuanLyBan;

public class Table {
    private String name;
    private String description;
    private String status;

    public Table() {
        // Phải có constructor mặc định để Firestore có thể khởi tạo
    }

    public Table(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }


    // Các phương thức getter và setter (nếu cần)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}
