package vn.posicode.chuyende.QuanLyBan;

public class Table {
    private String name;
    private String description;

    public Table() {
        // Firebase yêu cầu constructor trống
    }

    public Table(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
