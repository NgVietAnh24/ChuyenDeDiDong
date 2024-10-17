package vn.posicode.chuyende.models;

public class ListTable {
    String nameTable;
    String capacity;
    String Status;

    public ListTable(String nameTable, String capacity, String status) {
        this.nameTable = nameTable;
        this.capacity = capacity;
        Status = status;
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
