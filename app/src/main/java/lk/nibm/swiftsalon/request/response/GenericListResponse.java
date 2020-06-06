package lk.nibm.swiftsalon.request.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericListResponse<E> {

    @SerializedName("status")
    @Expose()
    private int status;

    @SerializedName("message")
    @Expose()
    private String message;

    @SerializedName("data")
    @Expose()
    private List<E> contents;

    public int getStatus() {
        return status;
    }

    public List<E> getContents() {
        return contents;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "GenericListResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", content=" + contents +
                '}';
    }
}
