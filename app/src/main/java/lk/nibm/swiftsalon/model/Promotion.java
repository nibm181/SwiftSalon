package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "tbl_promotion")
public class Promotion implements Parcelable {

    @PrimaryKey
    @NonNull
    private int id;

    @ColumnInfo(name = "salon_id")
    @SerializedName("salon_id")
    private int salonId;

    @ColumnInfo(name = "job_id")
    @SerializedName("job_id")
    private int jobId;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String description;

    @ColumnInfo(name = "off_amount")
    @SerializedName("off_amount")
    private float offAmount;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String image;

    @ColumnInfo(name = "start_date")
    @SerializedName("start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    @SerializedName("end_date")
    private Date endDate;

    public Promotion() {
    }

    protected Promotion(Parcel in) {
        id = in.readInt();
        salonId = in.readInt();
        jobId = in.readInt();
        description = in.readString();
        offAmount = in.readFloat();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(salonId);
        dest.writeInt(jobId);
        dest.writeString(description);
        dest.writeFloat(offAmount);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", salonId=" + salonId +
                ", jobId=" + jobId +
                ", description='" + description + '\'' +
                ", offAmount=" + offAmount +
                ", image='" + image + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }


}
