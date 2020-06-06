package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;

public class Salon implements Parcelable {

    private int id;
    private String email;
    private String name;
    private  String type;
    private String mobileNo;
    private String addr1;
    private String addr2;
    private Double longitude;
    private Double latitude;
    private Time open_time;
    private Time close_time;
    private String image;

    public Salon() {
    }

    public Salon(int id, String email, String name, String type, String mobileNo, String addr1, String addr2,
                 Double longitude, Double latitude, Time open_time, Time close_time, String image) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
        this.mobileNo = mobileNo;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.longitude = longitude;
        this.latitude = latitude;
        this.open_time = open_time;
        this.close_time = close_time;
        this.image = image;
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
        image = in.readString();
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
                ", open_time=" + open_time +
                ", close_time=" + close_time +
                ", image='" + image + '\'' +
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
        dest.writeString(image);
    }
}
