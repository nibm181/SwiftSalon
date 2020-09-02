package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NotificationData implements Parcelable {

    //appointment or anything else
    private String type;

    //pending, completed, canceled
    @SerializedName("appointment_status")
    private String appointmentStatus;

    //appointment id
    @SerializedName("appointment_id")
    private int appointmentId;

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        type = in.readString();
        appointmentStatus = in.readString();
        appointmentId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(appointmentStatus);
        dest.writeInt(appointmentId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
