package com.appulse.uec;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
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

        mListItems = db.getAllForEntity(ENTITY_NAME, column, "date", false);

        mListView = (ListView) view.findViewById(R.id.listView);
        adapter = new NewsAdapter(inflater.getContext(), mListItems);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotToDetail(position);
            }
        });

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
        db.close();
        return view;
    }

    public void updateList(MenuItem item) {
        mItem = item;
        get_json();
    }

    public interface onNewsItemSelectedListener {
        public void onNewsItemSelected(String value);
    }

    public void gotToDetail(int position) {
        Log.e("FragNewsList", "Position: " + position);
        if (listener != null) {
            ManagedEntity item = (ManagedEntity) mListItems.get(position);

            listener.onNewsItemSelected((String) item.getValue("content"));
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
                db.deleteAllForEntity(ENTITY_NAME);
                for (int i = 0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("News");

                    int news_id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                        item.setValue(column[j], jsonPosts.getJSONObject(i).getString(column[j]));
                    }
                    item.setId(news_id);

                    db.addEntity(ENTITY_NAME, item, column);
                }

                List list = db.getAllForEntity(ENTITY_NAME, column);
              //  list.add(0, new SearchRow());
                adapter = new NewsAdapter(getActivity(), list);
                //  adapter = new ArrayAdapter<String>(getActivity(),
                //  android.R.layout.simple_list_item_1, mListItems);
                mListView.setAdapter(adapter);
                db.close();


            } catch (JSONException e) {
                //e.printStackTrace();
                //cancelMenuLoader();
            }

        }
        cancelMenuLoader();
    }

    private void cancelMenuLoader() {
        if (mItem != null) {

           // MenuItem menuItemRefresh = mItem;

           // MenuItemCompat.collapseActionView(mItem);
           // menuItemRefresh = MenuItemCompat.setActionView(menuItemRefresh, R.layout.progressbar);
           // MenuItemCompat.setA
            // MenuItemCompat.setActionView(mItem, R.layout.progressbar);

            MenuItemCompat.collapseActionView(mItem);
            MenuItemCompat.setActionView(mItem,null);

            //MenuItemCompat.collapseActionView(mItem);
           // MenuItemCompat.collapseActionView(mItem);
           // MenuItemCompat.setActionView(null);
        }
    }
    public void get_json() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://uec.org.au/app_scripts/?script=newsarticle", new AsyncHttpResponseHandler() {
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
}
