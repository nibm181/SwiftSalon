package lk.nibm.swiftsalon.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import lk.nibm.swiftsalon.R;
import lk.nibm.swiftsalon.config.Session;
import lk.nibm.swiftsalon.adapter.MyAppointmentAdapter;
import lk.nibm.swiftsalon.modal.Appointment;

public class PreviousFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    private List<Appointment> appList = new ArrayList<>();
    SweetAlertDialog pDialog;
    String salonNo;
    Session session;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_previous, container, false);

        recyclerView = view.findViewById(R.id.rv_previous);
        MyAppointmentAdapter adapter = new MyAppointmentAdapter(getActivity(), appList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getContext());
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        salonNo = session.getsalonNo();

        getData();
    }



    void getData() {
        pDialog.show();
        appList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "https://newswiftsalon.000webhostapp.com/appointment.php?previous=1&salonNo="+salonNo;

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                pDialog.dismiss();
                JSONObject jsonObject = null;

                for(int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Appointment appointment = new Appointment();
                        appointment.setAppNo(jsonObject.getInt("appNo"));
                        appointment.setFirstName(jsonObject.getString("firstName"));
                        appointment.setStylist(jsonObject.getString("name"));
                        appointment.setTime(jsonObject.getString("time"));
                        appointment.setDate(jsonObject.getString("date"));
                        appointment.setMobileNo(jsonObject.getString("mobileNo"));
                        appointment.setImage(jsonObject.getString("image"));
                        appointment.setHsNo(jsonObject.getString("hsNo"));
                        appointment.setStatus(jsonObject.getString("status"));
                        appointment.setSalonNo(jsonObject.getString("salonNo"));

                        appList.add(appointment);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MyAppointmentAdapter)recyclerView.getAdapter()).appData = appList;
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), "There is no appointments yet.", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);

    }
}
