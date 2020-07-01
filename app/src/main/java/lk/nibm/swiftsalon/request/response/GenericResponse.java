package lk.nibm.swiftsalon.request.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericResponse<T> {

    @SerializedName("status")
    @Expose()
    private int status;

    @SerializedName("message")
    @Expose()
    private String message;

    @SerializedName("data")
    @Expose()
    private T content;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
