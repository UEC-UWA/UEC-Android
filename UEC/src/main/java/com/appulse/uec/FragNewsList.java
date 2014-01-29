package com.appulse.uec;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;
import com.appulse.uec.helpers.NewsAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


/**
 * Author: Can Elmas <can.elmas@pozitron.com>
 * Date: 1/14/13 11:50 AM
 */
public final class FragNewsList extends Fragment {

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

    NewsAdapter adapter;

    private List mListItems;
    private static final String ENTITY_NAME = "News";

    EditText inputSearch;

    String[] column;

    private static onNewsItemSelectedListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_news_list, container, false);


        MySQLHelper db = new MySQLHelper(getActivity());

        Resources res = getResources();
        column = res.getStringArray(R.array.news_entity);

        //  db.deleteAllForEntity(ENTITY_NAME);
        mListItems = db.getAllForEntity(ENTITY_NAME,column);
        updateList(null);
       // ActionBar actionBar = getActivity().getActionBar();

        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
              //  | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

        //inputSearch = (EditText) view.findViewById(R.id.inputSearch);

        mListView = (ListView) view.findViewById(R.id.listView);
        adapter = new NewsAdapter(inflater.getContext(), mListItems);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotToDetail(position);
            }
        } );

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
*/
        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = manager.getActiveNetworkInfo();
        return n != null && n.isAvailable();
    }

    public void updateList(MenuItem item) {
        if (isNetworkAvailable()) {
           // item.setActionView(R.layout.progressbar);
            //item.expandActionView();
            //mItem = item;
            get_json();
        }

    }
    public interface onNewsItemSelectedListener {
        public void onNewsItemSelected(String value);
    }

    public void gotToDetail(int position) {
        Log.e("FragNewsList", "Position: " + position);
        if (listener != null) {
            ManagedEntity item = (ManagedEntity) mListItems.get(position);

            listener.onNewsItemSelected((String)item.getValue("content"));
        }
    }

    public static void setOnMySignalListener(onNewsItemSelectedListener listener) {
        FragNewsList.listener = listener;
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {

            try {

                MySQLHelper db = new MySQLHelper(getActivity());

                for (int i =0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("News");

                    int news_id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                       item.setValue(column[j],jsonPosts.getJSONObject(i).getString(column[j]));
                    }
                    item.setId(news_id);

                    db.addEntity(ENTITY_NAME, item, column);
                }

                List<ManagedEntity> list = db.getAllForEntity(ENTITY_NAME, column);
                adapter = new NewsAdapter(getActivity(), list);
                //  adapter = new ArrayAdapter<String>(getActivity(),
                //  android.R.layout.simple_list_item_1, mListItems);
                mListView.setAdapter(adapter);

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

        client.get("http://www.appulse.com.au/uec/app_scripts.php?script=newsarticle", new AsyncHttpResponseHandler() {
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
