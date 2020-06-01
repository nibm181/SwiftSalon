package lk.nibm.swiftsalon.service.repository;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lk.nibm.swiftsalon.common.CustomDialog;
import lk.nibm.swiftsalon.service.config.Session;
import lk.nibm.swiftsalon.service.modal.Appointment;

public class AppointmentRepo {

    private LiveData<List<Appointment>> newAppointments, ongoingAppointments;
    Application application;
    CustomDialog dialog;

    public AppointmentRepo(Application application) {
        this.application = application;
        this.newAppointments = fetchAppointmentFromAPI(application);
    }

    public LiveData<List<Appointment>> fetchAppointmentFromAPI(Application application) {

        MutableLiveData<List<Appointment>> liveAppointments = new MutableLiveData<>();
        List<Appointment> appointments = new ArrayList<>();
        dialog = new CustomDialog(application);

        Session session = new Session(application.getApplicationContext());
        String salonNo = session.getSalonNo();

        String url = "http://10.0.2.2/swiftsalon/appTest.php";

        RequestQueue requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley response", response.toString());
                try {
                    if(response.getInt("status") == 1) {

                        JSONObject jsonObject = null;
                        JSONArray newAppArray = response.getJSONArray("newAppointments");
                        JSONArray newAppArray1 = response.getJSONArray("ongoingAppointments");

                        for(int i = 0; i < newAppArray.length(); i++) {

                            jsonObject = newAppArray.getJSONObject(i);

                            Appointment appointment = new Appointment();
                            appointment.setId( jsonObject.getInt("appNo") );
                            appointment.setCustomerFirstName( jsonObject.getString("firstName") );
                            appointment.setStylistName( jsonObject.getString("name") );
                            appointment.setTime( jsonObject.getString("time") );
                            appointment.setDate( jsonObject.getString("date") );
                            appointment.setCustomerImage( jsonObject.getString("image") );
                            appointment.setStylistId( jsonObject.getInt("hsNo") );
                            appointment.setStatus( jsonObject.getString("status") );
                            appointment.setSalonId( jsonObject.getInt("salonNo") );

                            appointments.add(appointment);
                        }

                        liveAppointments.setValue(appointments);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.showToast("We are unable to connect you with us.");
            }
        });

        if(isOnline()) {
            requestQueue.add(request);
        }
        else {
            dialog.showToast("Check your connection and try again.");
        }

        return liveAppointments;
    }

    public LiveData<List<Appointment>> getAllAppointments() {
        return newAppointments;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) application.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
