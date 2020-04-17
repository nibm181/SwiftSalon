package lk.nibm.swiftsalon;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MyStylistAdapter extends ArrayAdapter<String>{

    private String [] img, name;
    private Activity context;

    public MyStylistAdapter(Activity context, String[] img, String[] name ) {
        super(context, R.layout.stylist_list, name);
        this.context = context;
        this.img = img;
        this.name = name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.stylist_list, null, true);

        TextView txtName = rowView.findViewById(R.id.list_txt_stylist);
        ImageView imgStylist = rowView.findViewById(R.id.list_img_stylist);

        txtName.setText(name[position]);
        //glide image
        Glide.with(rowView).load(img[position]).apply(RequestOptions.circleCropTransform()).into(imgStylist);

        return rowView;
    }

}
