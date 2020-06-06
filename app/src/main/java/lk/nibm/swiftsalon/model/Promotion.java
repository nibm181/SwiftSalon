package lk.nibm.swiftsalon.model;

public class Promotion {

    private int id;
    private int jobId;
    private String description;
    private float offAmount;
    private int duration;
    private String image;
    private boolean status;

    public Promotion() {
    }

    public Promotion(int id, int jobId, String description, float offAmount, int duration, String image, boolean status) {
        this.id = id;
        this.jobId = jobId;
        this.description = description;
        this.offAmount = offAmount;
        this.duration = duration;
        this.image = image;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getOffAmount() {
        return offAmount;
    }

    public void setOffAmount(float offAmount) {
        this.offAmount = offAmount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", description='" + description + '\'' +
                ", offAmount=" + offAmount +
                ", duration=" + duration +
                ", image='" + image + '\'' +
                ", status=" + status +
                '}';
    }
}
