package com.appulse.uec;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appulse.uec.helpers.CommitteeAdapter;
import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Matt on 2/01/2014.
 */
public class FragCommitteeList extends Fragment {

    private ListView listView;

    private CommitteeAdapter adapter;

    private List mListItems;

    private MenuItem mItem;

    private String[] column;

    private static final String ENTITY_NAME = "Committee";

    private static onCommitteeItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_committee_list, container, false);

        listView = (ListView) view.findViewById(R.id.committeeListView);

        mListItems = new LinkedList<ManagedEntity>();

        Resources res = getResources();
        column = res.getStringArray(R.array.committee_entity);

        MySQLHelper db = new MySQLHelper(getActivity());
        mListItems = db.getAllForEntityWithSections(ENTITY_NAME, column, "subcommittee");
        adapter = new CommitteeAdapter(inflater.getContext(), mListItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotToDetail(position);
            }
        });

        db.close();

        return view;
    }

    public void updateList(MenuItem item) {
        mItem = item;
        get_json();
    }

    public interface onCommitteeItemSelectedListener {
        public void onCommitteeItemSelected(int id);
    }

    public void gotToDetail(int position) {
        //Log.e("FragNewsList", "Position: " + position);
        if (listener != null) {
            ManagedEntity item = (ManagedEntity) mListItems.get(position);

            listener.onCommitteeItemSelected(item.getId());
        }
    }

    public static void setOnMySignalListener(onCommitteeItemSelectedListener listener) {
        FragCommitteeList.listener = listener;
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {

            try {

                MySQLHelper db = new MySQLHelper(getActivity());

                db.deleteAllForEntity(ENTITY_NAME);

                for (int i = 0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("Committee");

                    int news_id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                        if (column[j].equals("date")) {
                            String clean_value = jsonPosts.getJSONObject(i).getString(column[j]);
                            clean_value = clean_value.substring(5, clean_value.length());
                            item.setValue(column[j], Integer.valueOf(clean_value));
                        } else {
                            item.setValue(column[j], jsonPosts.getJSONObject(i).getString(column[j]));
                        }
                    }
                    item.setId(news_id);

                    db.addEntity(ENTITY_NAME, item, column);


                }

                List list = db.getAllForEntityWithSections(ENTITY_NAME, column, "subcommittee");
                adapter = new CommitteeAdapter(getActivity(), list);
                //  adapter = new ArrayAdapter<String>(getActivity(),
                //  android.R.layout.simple_list_item_1, mListItems);
                listView.setAdapter(adapter);

                if (mItem != null) {
                    //cancelMenuLoader();
                }

                db.close();

            } catch (JSONException e) {
                //e.printStackTrace();
            }

        }
        cancelMenuLoader();
    }

    public void get_json() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://uec.org.au/app_scripts/?script=person", new AsyncHttpResponseHandler() {
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

