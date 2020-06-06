package lk.nibm.swiftsalon.model;

public class StylistJob {

    private int id;
    private int stylistId;
    private int jobId;

    public StylistJob() {
    }

    public StylistJob(int id, int stylistId, int jobId) {
        this.id = id;
        this.stylistId = stylistId;
        this.jobId = jobId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "StylistJob{" +
                "id=" + id +
                ", stylistId=" + stylistId +
                ", jobId=" + jobId +
                '}';
    }
}
