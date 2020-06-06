package lk.nibm.swiftsalon.model;

public class AppointmentDetail {

    private int id;
    private int appointmentId;
    private int jobId;
    private float price;

    public AppointmentDetail() {
    }

    public AppointmentDetail(int id, int appointmentId, int jobId, float price) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.jobId = jobId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "AppointmentDetail{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", jobId=" + jobId +
                ", price=" + price +
                '}';
    }
}
