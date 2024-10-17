package vn.posicode.chuyende.models;

public class ListTable {
    int id;
    String nameTable;
    String capacity;
    String Status;

    public ListTable() {
    }

    public ListTable(int id, String nameTable, String capacity, String status) {
        this.id = id;
        this.nameTable = nameTable;
        this.capacity = capacity;
        Status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
