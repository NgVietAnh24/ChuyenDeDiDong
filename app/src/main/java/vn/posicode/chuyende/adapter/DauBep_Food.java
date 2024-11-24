package vn.posicode.chuyende.adapter;

/**
 * Class đại diện cho thông tin món ăn của đầu bếp.
 */
public class DauBep_Food {
    private String monAnId;      // ID của món ăn
    private String tenMonAn;     // Tên món ăn
    private int gia;             // Giá của món ăn
    private String hinhAnh;      // URL hình ảnh của món ăn
    private int soLuong;         // Số lượng món ăn
    private String trangThai;    // Trạng thái của món ăn (ví dụ: "Đang làm", "Hoàn thành")
    private String time;         // Thời gian liên quan đến món ăn (định dạng HH:mm)

    // Constructor mặc định (dùng cho Firebase hoặc khởi tạo không tham số)
    public DauBep_Food() {}

    /**
     * Constructor đầy đủ để khởi tạo đối tượng.
     *
     * @param monAnId    ID của món ăn.
     * @param tenMonAn   Tên món ăn.
     * @param gia        Giá món ăn.
     * @param hinhAnh    URL hình ảnh món ăn.
     * @param soLuong    Số lượng món ăn.
     * @param trangThai  Trạng thái món ăn.
     * @param time       Thời gian liên quan đến món ăn.
     */
    public DauBep_Food(String monAnId, String tenMonAn, int gia, String hinhAnh, int soLuong, String trangThai, String time) {
        this.monAnId = monAnId;
        this.tenMonAn = tenMonAn;
        this.gia = gia;
        this.hinhAnh = hinhAnh;
        this.soLuong = soLuong;
        this.trangThai = trangThai;
        this.time = time;
    }

    // Getter và Setter
    public String getMonAnId() {
        return monAnId;
    }

    public void setMonAnId(String monAnId) {
        this.monAnId = monAnId;
    }

    public String getTenMonAn() {
        return tenMonAn;
    }

    public void setTenMonAn(String tenMonAn) {
        this.tenMonAn = tenMonAn;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DauBep_Food{" +
                "monAnId='" + monAnId + '\'' +
                ", tenMonAn='" + tenMonAn + '\'' +
                ", gia=" + gia +
                ", hinhAnh='" + hinhAnh + '\'' +
                ", soLuong=" + soLuong +
                ", trangThai='" + trangThai + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
