package lk.nibm.swiftsalon;

public class Appointment {

    private int appNo;
    private String salonNo;
    private String time;
    private String date;
    private String hsNo;
    private String mobileNo;
    private String status;
    private String firstName;
    private String image;
    private String stylist;

    public Appointment() {
    }

    public Appointment(int appNo, String salonNo, String time, String date, String hsNo, String mobileNo, String status, String firstName, String image, String stylist) {
        this.appNo = appNo;
        this.salonNo = salonNo;
        this.time = time;
        this.date = date;
        this.hsNo = hsNo;
        this.mobileNo = mobileNo;
        this.status = status;
        this.firstName = firstName;
        this.image = image;
        this.stylist = stylist;
    }

    public int getAppNo() {
        return appNo;
    }

    public String getSalonNo() {
        return salonNo;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getHsNo() {
        return hsNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getStatus() {
        return status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getImage() {
        return image;
    }

    public String getStylist() {
        return stylist;
    }


    public void setAppNo(int appNo) {
        this.appNo = appNo;
    }

    public void setSalonNo(String salonNo) {
        this.salonNo = salonNo;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHsNo(String hsNo) {
        this.hsNo = hsNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStylist(String stylist) {
        this.stylist = stylist;
    }
}
