package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tbl_stylist_job")
public class StylistJob implements Parcelable {

    @PrimaryKey
    private int id;

    @SerializedName("stylist_id")
    @ColumnInfo(name = "stylist_id")
    private int stylistId;

    @SerializedName("job_id")
    @ColumnInfo(name = "job_id")
    private int jobId;

    @SerializedName("name")
    @ColumnInfo(name = "job_name")
    private String jobName;

    public StylistJob() {
    }

    protected StylistJob(Parcel in) {
        id = in.readInt();
        stylistId = in.readInt();
        jobId = in.readInt();
        jobName = in.readString();
    }

    public static final Creator<StylistJob> CREATOR = new Creator<StylistJob>() {
        @Override
        public StylistJob createFromParcel(Parcel in) {
            return new StylistJob(in);
        }

        @Override
        public StylistJob[] newArray(int size) {
            return new StylistJob[size];
        }
    };

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "StylistJob{" +
                "id=" + id +
                ", stylistId=" + stylistId +
                ", jobId=" + jobId +
                ", jobName=" + jobName +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(stylistId);
        dest.writeInt(jobId);
        dest.writeString(jobName);
    }
}
