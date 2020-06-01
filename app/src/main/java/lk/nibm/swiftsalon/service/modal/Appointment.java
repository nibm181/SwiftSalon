package lk.nibm.swiftsalon.service.modal;

import java.util.Observable;

public class Appointment extends Observable {

    private int id;
    private int salonId;
    private int customerId;
    private int stylistId;
    private int jobId;
    private String date;
    private String time;
    private String status;
    private String customerFirstName;
    private String customerImage;
    private String stylistName;

    public Appointment() {
    }

    public Appointment(int id, int salonId, int customerId, int stylistId, int jobId, String date, String time, String status) {
        this.id = id;
        this.salonId = salonId;
        this.customerId = customerId;
        this.stylistId = stylistId;
        this.jobId = jobId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Appointment(int id, int salonId, int customerId, int stylistId, int jobId, String date, String time, String status, String customerFirstName, String customerImage, String stylistName) {
        this.id = id;
        this.salonId = salonId;
        this.customerId = customerId;
        this.stylistId = stylistId;
        this.jobId = jobId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.customerFirstName = customerFirstName;
        this.customerImage = customerImage;
        this.stylistName = stylistName;
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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStylistId() {
        return stylistId;
    }

    public void setStylistId(int stylistId) {
        this.stylistId = stylistId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getStylistName() {
        return stylistName;
    }

    public void setStylistName(String stylistName) {
        this.stylistName = stylistName;
    }

}
