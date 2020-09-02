package lk.nibm.swiftsalon.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Earnings {

    @SerializedName("salon_id")
    private int salonId;

    @SerializedName("year")
    private int year;

    @SerializedName("month")
    private int month;

    @SerializedName("total")
    private float earning;

    @SerializedName("count")
    private int count;

    public int getSalonId() {
        return salonId;
    }

    public void setSalonId(int salonId) {
        this.salonId = salonId;
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

    public float getEarning() {
        return earning;
    }

    public void setEarning(float earning) {
        this.earning = earning;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Earnings{" +
                "salonId=" + salonId +
                ", year=" + year +
                ", month=" + month +
                ", earning=" + earning +
                ", count=" + count +
                '}';
    }
}
