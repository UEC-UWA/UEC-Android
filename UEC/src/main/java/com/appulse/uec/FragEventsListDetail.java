package com.appulse.uec;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appulse.uec.helpers.DownloadImagesTask;
import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Matt on 27/01/2014.
 */
public class FragEventsListDetail extends Fragment {
    private int id;

    String[] column;

    private ManagedEntity event;

    private static onEventMapListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MySQLHelper db = new MySQLHelper(getActivity());

        id = getArguments().getInt("id");

        Resources res = getResources();
        column = res.getStringArray(R.array.events_entity);
        event = db.getEntity("Events", id, column);

        View view = inflater.inflate(R.layout.frag_event_detail, container, false);

        TextView eventName = (TextView) view.findViewById(R.id.eventName);
        eventName.setText((String) event.getValue("name"));

        TextView eventLocation = (TextView) view.findViewById(R.id.eventSub);

        eventLocation.setText((String) event.getValue("location"));

        TextView eventStart = (TextView) view.findViewById(R.id.eventStartDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            long unix_time = dateFormat.parse((String)event.getValue("start_date")).getTime();
            DateFormat df = new SimpleDateFormat("d MMMM yyyy K:mm a");
            String date_formatted = df.format(unix_time);
            eventStart.setText(date_formatted);

        } catch (ParseException e) {
            e.printStackTrace();
        }
TextView eventEnd = (TextView) view.findViewById(R.id.eventEndDate);

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            long unix_time = dateFormat2.parse((String)event.getValue("end_date")).getTime();
            DateFormat df = new SimpleDateFormat("d MMMM yyyy K:mm a");
            String date_formatted = df.format(unix_time);
            eventEnd.setText(date_formatted);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView eventAddress = (TextView) view.findViewById(R.id.eventAddress);
        eventAddress.setText((String) event.getValue("address"));

        TextView eventContent = (TextView) view.findViewById(R.id.eventContent);
        eventContent.setText((String) event.getValue("event_description"));

        ImageView image = (ImageView) view.findViewById(R.id.eventImage);

        image.setTag(event.getValue("photo_path"));
        DownloadImagesTask task = new DownloadImagesTask();
        task.execute(image);


        Button eventMap = (Button) view.findViewById(R.id.buttonMap);
        eventMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onMapButtonSelected();
            }
        });
        return view;
    }

    public interface onEventMapListener {
        public void onMapButtonSelected();

    }


    public static void setOnMySignalListener(onEventMapListener listener) {
        FragEventsListDetail.listener = listener;
    }

}
