package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NotificationData implements Parcelable {

    @SerializedName("body")
    private String body;

    @SerializedName("title")
    private String title;

    //appointment or anything else
    @SerializedName("type")
    private String type;

    //pending, completed, canceled
    @SerializedName("status")
    private String appointmentStatus;

    //appointment id
    @SerializedName("id")
    private int appointmentId;

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        body = in.readString();
        title = in.readString();
        type = in.readString();
        appointmentStatus = in.readString();
        appointmentId = in.readInt();
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    @Override
    public String toString() {
        return "NotificationData{" +
                "body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", appointmentStatus='" + appointmentStatus + '\'' +
                ", appointmentId=" + appointmentId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(appointmentStatus);
        dest.writeInt(appointmentId);
    }
}
