package com.appulse.uec;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
public final class FragAbout extends Fragment {

    /**
     *
     *  The Wrapper Fragment that host nested child fragments.
     *
     *  First child fragment is added in onActivityCreated() callback
     *
     *  More child fragments can be added at runtime by clicking 'Go Nesty!'
     *  button.
     *
     */

    public static final String TAG = "FragNews";

    /**
     *  Child Fragment Manager
     */
    private FragmentManager fm;

    MenuItem mItem;
    /**
     *  Fragment Tags
     */
    private int fragCount = 1;

    private ListView mListView;

    ArrayAdapter adapter;

    private List mListItems;
    private static final String ENTITY_NAME = "Sponsors";

    EditText inputSearch;

    String[] column;

    private static onAboutListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_about, container, false);


        MySQLHelper db = new MySQLHelper(getActivity());

        Resources res = getResources();
        column = res.getStringArray(R.array.sponsors_entity);

        //  db.deleteAllForEntity(ENTITY_NAME);
        mListItems = db.getAllForEntity(ENTITY_NAME, column);
        updateList(null);
        //ActionBar actionBar = getActivity().getActionBar();

        // actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
        //| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        // inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        String items[];
        if (mListItems == null) {
            items = new String[0];
        } else {
                items = new String[mListItems.size()];
            for (int i = 0; i < mListItems.size(); i++) {
               ManagedEntity result = (ManagedEntity) mListItems.get(i);
                items[i] = (String) result.getValue("name");
            }
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        adapter = new ArrayAdapter(inflater.getContext(), android.R.layout.simple_list_item_1, items);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //gotToDetail(position);
            }
        } );

        Button aboutUEC = (Button) view.findViewById(R.id.AboutUECButton);
        aboutUEC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onAboutAppSelected();
            }
        });

        Button aboutApp = (Button) view.findViewById(R.id.AboutAppButton);
        aboutApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onAboutAppSelected();
            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = manager.getActiveNetworkInfo();
        return n != null && n.isAvailable();
    }

    public void updateList(MenuItem item) {
        if (isNetworkAvailable()) {
            //item.setActionView(R.layout.progressbar);
          //  item.expandActionView();
           // mItem = item;
            get_json();
        }

    }
    public interface onAboutListener {
        public void onAboutUECSelected();
        public void onAboutAppSelected();
    }



    public static void setOnMySignalListener(onAboutListener listener) {
        FragAbout.listener = listener;
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {

            try {

                MySQLHelper db = new MySQLHelper(getActivity());

                for (int i =0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("Events");

                    int id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                      item.setValue(column[j],jsonPosts.getJSONObject(i).getString(column[j]));

                    }
                    item.setId(id);

                    db.addEntity(ENTITY_NAME, item, column);





                }

                List list = db.getAllForEntity(ENTITY_NAME, column);

                String items[];
                if (list == null) {
                    items = new String[0];
                } else {
                    items = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        ManagedEntity result = (ManagedEntity) mListItems.get(i);
                        items[i] = (String) result.getValue("name");
                        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, items);
                        mListView.setAdapter(adapter);
                    }
                }



                if (mItem != null) {
                    mItem.collapseActionView();
                    mItem.setActionView(null);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void get_json() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://www.appulse.com.au/uec/app_scripts.php?script=sponsor", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                JSONArray result = null;
                try {
                    result = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handleResponse(result);
            }
        });
    }
}
