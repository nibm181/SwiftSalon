package lk.nibm.swiftsalon.service.modal;

import java.sql.Time;

public class Salon {

    private int id;
    private String email;
    private String name;
    private  String type;
    private String mobile_no;
    private String addr1;
    private String addr2;
    private String city;
    private Double longitude;
    private Double latitude;
    private Time open_time;
    private Time close_time;
    private String image;

    public Salon(int id, String email, String name, String type, String mobile_no, String addr1, String addr2, String city, Double longitude, Double latitude, Time open_time, Time close_time, String image) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
        this.mobile_no = mobile_no;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.open_time = open_time;
        this.close_time = close_time;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Time getOpen_time() {
        return open_time;
    }

    public void setOpen_time(Time open_time) {
        this.open_time = open_time;
    }

    public Time getClose_time() {
        return close_time;
    }

    public void setClose_time(Time close_time) {
        this.close_time = close_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
