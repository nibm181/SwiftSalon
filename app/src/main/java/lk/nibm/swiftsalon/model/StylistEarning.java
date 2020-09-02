package lk.nibm.swiftsalon.model;

import com.google.gson.annotations.SerializedName;

public class StylistEarning {

    @SerializedName("salon_id")
    private int salonId;

    @SerializedName("stylist_id")
    private int stylistId;

    @SerializedName("stylist_name")
    private String stylistName;

    @SerializedName("year")
    private int year;

    @SerializedName("month")
    private int month;

    @SerializedName("total")
    private float stylistEarning;

    @SerializedName("count")
    private int count;

    public int getSalonId() {
        return salonId;
    }

    public void setSalonId(int salonId) {
        this.salonId = salonId;
    }

    public int getStylistId() {
        return stylistId;
    }

    public void setStylistId(int stylistId) {
        this.stylistId = stylistId;
    }

    public String getStylistName() {
        return stylistName;
    }

    public void setStylistName(String stylistName) {
        this.stylistName = stylistName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public float getStylistEarning() {
        return stylistEarning;
    }

    public void setStylistEarning(float stylistEarning) {
        this.stylistEarning = stylistEarning;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "StylistEarning{" +
                "salonId=" + salonId +
                ", stylistId=" + stylistId +
                ", stylistName='" + stylistName + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", stylistEarning=" + stylistEarning +
                ", count=" + count +
                '}';
    }
}
