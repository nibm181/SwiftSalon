package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tbl_job")
public class Job implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "salon_id")
    @SerializedName("salon_id")
    private int salonId;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    private int duration;

    @ColumnInfo(name = "price")
    @SerializedName("price")
    private float price;

    public Job() {
    }

    public Job(int id, int salonId, String name, int duration, float price) {
        this.id = id;
        this.salonId = salonId;
        this.name = name;
        this.duration = duration;
        this.price = price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Job)) return false;
        if(this.getId() == ((Job) obj).getId()) {
            return true;
        }
        return false;
    }

    protected Job(Parcel in) {
        id = in.readInt();
        salonId = in.readInt();
        name = in.readString();
        duration = in.readInt();
        price = in.readFloat();
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", salon_id=" + salonId +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", price=" + price +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(salonId);
        dest.writeString(name);
        dest.writeInt(duration);
        dest.writeFloat(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
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

    public void setSalonId(int salon_id) {
        this.salonId = salon_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
