package com.appulse.uec;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.appulse.uec.helpers.EventsAdapter;
import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Author: Can Elmas <can.elmas@pozitron.com>
 * Date: 1/14/13 11:50 AM
 */
public final class FragEventList extends Fragment {

    /**
     * The Wrapper Fragment that host nested child fragments.
     * <p/>
     * First child fragment is added in onActivityCreated() callback
     * <p/>
     * More child fragments can be added at runtime by clicking 'Go Nesty!'
     * button.
     */

    public static final String TAG = "FragNews";

    /**
     * Child Fragment Manager
     */
    private FragmentManager fm;

    MenuItem mItem;
    /**
     * Fragment Tags
     */
    private int fragCount = 1;

    private ListView mListView;

    EventsAdapter adapter;

    private List mListItems;
    private static final String ENTITY_NAME = "Events";

    EditText inputSearch;

    String[] column;

    private static onEventsItemSelectedListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_events_list, container, false);


        MySQLHelper db = new MySQLHelper(getActivity());

        Resources res = getResources();
        column = res.getStringArray(R.array.events_entity);

        mListItems = db.getAllForEntityWithSections(ENTITY_NAME, column, "start_date");

        mListView = (ListView) view.findViewById(R.id.eventsListView);
        adapter = new EventsAdapter(inflater.getContext(), mListItems);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotToDetail(position);
            }
        });
        db.close();

        return view;
    }

/*
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                FragNewsList.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        return view;
    }
*/

    public void updateList(MenuItem item) {
        mItem = item;
        get_json();
    }

    public interface onEventsItemSelectedListener {
        public void onEventItemSelected(int id);
    }

    public void gotToDetail(int position) {

        if (listener != null) {
            ManagedEntity item = (ManagedEntity) mListItems.get(position);
            Integer i = item.getId();

            listener.onEventItemSelected(i.intValue());
        }
    }

    public static void setOnMySignalListener(onEventsItemSelectedListener listener) {
        FragEventList.listener = listener;
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {

            try {

                MySQLHelper db = new MySQLHelper(getActivity());

                db.deleteAllForEntity(ENTITY_NAME);
                for (int i = 0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("Events");

                    int id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                        item.setValue(column[j], jsonPosts.getJSONObject(i).getString(column[j]));

                    }
                    item.setId(id);

                    db.addEntity(ENTITY_NAME, item, column);


                }

                List list = db.getAllForEntityWithSections(ENTITY_NAME, column, "start_date");

                adapter = new EventsAdapter(getActivity(), list);
                mListView.setAdapter(adapter);

                if (mItem != null) {
                    cancelMenuLoader();
                }

                db.close();


            } catch (JSONException e) {

            }


        }
        cancelMenuLoader();

    }

    public void get_json() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://www.appulse.com.au/uec/app_scripts.php?script=event", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                JSONArray result = null;
                try {
                    result = new JSONArray(response);
                } catch (JSONException e) {
                    cancelMenuLoader();
                }
                handleResponse(result);
            }
        });
    }
    private void cancelMenuLoader() {
        if (mItem != null) {
            MenuItemCompat.collapseActionView(mItem);
            MenuItemCompat.setActionView(mItem,null);
        }
    }

}
