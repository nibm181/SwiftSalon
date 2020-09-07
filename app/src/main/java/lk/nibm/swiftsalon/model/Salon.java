package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.annotations.SerializedName;

import java.sql.Time;

@Entity(tableName = "tbl_salon")
public class Salon implements Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "email")
    @SerializedName("email")
    private String email;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "type")
    @SerializedName("type")
    private  String type;

    @ColumnInfo(name = "mobile_no")
    @SerializedName("mobile_no")
    private String mobileNo;

    @ColumnInfo(name = "addr1")
    @SerializedName("addr1")
    private String addr1;

    @ColumnInfo(name = "addr2")
    @SerializedName("addr2")
    private String addr2;

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    private Double longitude;

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    private Double latitude;

    @ColumnInfo(name = "location")
    @SerializedName("location")
    private String location;

    @ColumnInfo(name = "open_time")
    @SerializedName("open_time")
    private String openTime;

    @ColumnInfo(name = "close_time")
    @SerializedName("close_time")
    private String closeTime;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String image;

    @ColumnInfo(name = "rating")
    @SerializedName("rating")
    private float rating;

    @Ignore
    @SerializedName("fcm_token")
    private String token;

    @Ignore
    private String password;

    public Salon() {
    }

    protected Salon(Parcel in) {
        id = in.readInt();
        email = in.readString();
        name = in.readString();
        type = in.readString();
        mobileNo = in.readString();
        addr1 = in.readString();
        addr2 = in.readString();
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        location = in.readString();
        openTime = in.readString();
        closeTime = in.readString();
        image = in.readString();
        token = in.readString();
        rating = in.readFloat();
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Salon{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", addr1='" + addr1 + '\'' +
                ", addr2='" + addr2 + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", location='" + location + '\'' +
                ", openTime='" + openTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                ", image='" + image + '\'' +
                ", token='" + token + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(mobileNo);
        dest.writeString(addr1);
        dest.writeString(addr2);
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        dest.writeString(location);
        dest.writeString(openTime);
        dest.writeString(closeTime);
        dest.writeString(image);
        dest.writeString(token);
        dest.writeFloat(rating);
    }
}
