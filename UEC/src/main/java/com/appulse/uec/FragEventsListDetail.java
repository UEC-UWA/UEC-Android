package com.appulse.uec;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;

/**
 * Created by Matt on 27/01/2014.
 */
public class FragEventsListDetail  extends Fragment {
   private int id;

    String[] column;

    private ManagedEntity event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MySQLHelper db = new MySQLHelper(getActivity());

        id = getArguments().getInt("id");


        Resources res = getResources();
        column = res.getStringArray(R.array.events_entity);
        event = db.getEntity("Events",id,column);

        View view = inflater.inflate(R.layout.frag_event_detail, container, false);

        TextView eventName = (TextView) view.findViewById(R.id.eventName);
        eventName.setText((String) event.getValue("name"));
        return view;
    }
}
