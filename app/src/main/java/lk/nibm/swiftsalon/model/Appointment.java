package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tbl_appointment")
public class Appointment implements Parcelable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "salon_id")
    @SerializedName("salon_id")
    private int salonId;

    @ColumnInfo(name = "customer_id")
    @SerializedName("customer_id")
    private int customerId;

    @ColumnInfo(name = "stylist_id")
    @SerializedName("stylist_id")
    private int stylistId;

    @ColumnInfo(name = "date")
    @SerializedName("date")
    private String date;

    @ColumnInfo(name = "time")
    @SerializedName("time")
    private String time;

    @ColumnInfo(name = "status")
    @SerializedName("status")
    private String status;

    @ColumnInfo(name = "customer_first_name")
    @SerializedName("customer_first_name")
    private String customerFirstName;

    @ColumnInfo(name = "customer_last_name")
    @SerializedName("customer_last_name")
    private String customerLastName;

    @ColumnInfo(name = "customer_image")
    @SerializedName("customer_image")
    private String customerImage;

    @ColumnInfo(name = "modified_on")
    @SerializedName("modified_on")
    private String modifiedOn; //timestamp

    public Appointment() {
    }

    protected Appointment(Parcel in) {
        id = in.readInt();
        salonId = in.readInt();
        customerId = in.readInt();
        stylistId = in.readInt();
        date = in.readString();
        time = in.readString();
        status = in.readString();
        customerFirstName = in.readString();
        customerLastName = in.readString();
        customerImage = in.readString();
        modifiedOn = in.readString();
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
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

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStylistId() {
        return stylistId;
    }

    public void setStylistId(int stylistId) {
        this.stylistId = stylistId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(salonId);
        dest.writeInt(customerId);
        dest.writeInt(stylistId);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(status);
        dest.writeString(customerFirstName);
        dest.writeString(customerLastName);
        dest.writeString(customerImage);
        dest.writeString(modifiedOn);
    }
}
