package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tbl_stylist")
public class Stylist implements Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "salon_id")
    @SerializedName("salon_id")
    private int salonId;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    private String gender;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String image;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    private int status;

    public Stylist() {
    }

    protected Stylist(Parcel in) {
        id = in.readInt();
        salonId = in.readInt();
        name = in.readString();
        gender = in.readString();
        image = in.readString();
        status = in.readInt();
    }

    public static final Creator<Stylist> CREATOR = new Creator<Stylist>() {
        @Override
        public Stylist createFromParcel(Parcel in) {
            return new Stylist(in);
        }

        @Override
        public Stylist[] newArray(int size) {
            return new Stylist[size];
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(salonId);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(image);
        dest.writeInt(status);
    }

    @Override
    public String toString() {
        return "Stylist{" +
                "id=" + id +
                ", salonId=" + salonId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", image='" + image + '\'' +
                ", status=" + status +
                '}';
    }
}
