package lk.nibm.swiftsalon.ui.adapter;

import android.view.View;

public interface OnAppointmentListener {

    void onAppointmentClick(int position, String type);
    void onAppointmentAccept(int position);
}
