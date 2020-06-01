package lk.nibm.swiftsalon.service.modal;

public class Stylist {

    private int id;
    private int salon_id;
    private String name;
    private String gender;
    private String image;
    private boolean status;
    private boolean is_delete;

    public Stylist(int id, int salon_id, String name, String gender, String image, boolean status, boolean is_delete) {
        this.id = id;
        this.salon_id = salon_id;
        this.name = name;
        this.gender = gender;
        this.image = image;
        this.status = status;
        this.is_delete = is_delete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
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

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }
}
