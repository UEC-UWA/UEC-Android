package com.appulse.uec;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Matt on 19/01/2014.
 */
public class FragNewsList extends Fragment {

    private static onNewsItemSelectedListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };

        View view = inflater.inflate(R.layout.frag_news_list, container, false);


        ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotToDetail(position);
            }
        } );

        return view;
    }

    public interface onNewsItemSelectedListener {
        public void onNewsItemSelected(int value);
    }
    public void gotToDetail(int position) {
       if (listener != null) {
        listener.onNewsItemSelected(position);
       }
    }
    public static void setOnMySignalListener(onNewsItemSelectedListener listener) {
         FragNewsList.listener = listener;
     }

    }

