package lk.nibm.swiftsalon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Job {

    private int id;
    private int salon_id;
    private String name;
    private  int duration;
    private float price;

    public Job() {
    }

    public Job(int id, int salon_id, String name, int duration, float price) {
        this.id = id;
        this.salon_id = salon_id;
        this.name = name;
        this.duration = duration;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
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
