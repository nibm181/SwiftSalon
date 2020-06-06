package lk.nibm.swiftsalon.model;

public class Stylist {

    private int id;
    private int salonId;
    private String name;
    private String gender;
    private String image;
    private boolean status;
    private boolean isDelete;

    public Stylist() {
    }

    public Stylist(int id, int salonId, String name, String gender, String image, boolean status, boolean isDelete) {
        this.id = id;
        this.salonId = salonId;
        this.name = name;
        this.gender = gender;
        this.image = image;
        this.status = status;
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalonId() {
        return salonId;
    }

    public void setSalonId(int salonId) {
        this.salonId = salonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public String toString() {
        return "Stylist{" +
                "id=" + id +
                ", salonId=" + salonId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", image='" + image + '\'' +
                ", status=" + status +
                ", isDelete=" + isDelete +
                '}';
    }
}
