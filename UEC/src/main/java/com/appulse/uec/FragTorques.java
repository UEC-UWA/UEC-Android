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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.app.*;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.Context;
import android.os.Environment;
import android.net.Uri;
import android.content.Intent;

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
public final class FragTorques extends Fragment {

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

    ArrayAdapter adapter;

    private List mListItems;
    private static final String ENTITY_NAME = "Torques";

    EditText inputSearch;

    String[] column;

    private static onTorquesListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_torques, container, false);


        MySQLHelper db = new MySQLHelper(getActivity());

        Resources res = getResources();
        column = res.getStringArray(R.array.torques_entity);

        //  db.deleteAllForEntity(ENTITY_NAME);
        mListItems = db.getAllForEntity(ENTITY_NAME, column);

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
                items[i] = (String) result.getValue("title");
            }
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        adapter = new ArrayAdapter(inflater.getContext(), android.R.layout.simple_list_item_1, items);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    public interface onTorquesListener {
        public void onTorquesSelected(String value);

    }


    public void gotToDetail(int position) {

        if (listener != null) {
            ManagedEntity item = (ManagedEntity) mListItems.get(position);


            listener.onTorquesSelected((String) item.getValue("file_address"));
        }
    }


    public static void setOnMySignalListener(onTorquesListener listener) {
        FragTorques.listener = listener;
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {

            try {

                MySQLHelper db = new MySQLHelper(getActivity());


                for (int i = 0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity("Torques");

                    int id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                        item.setValue(column[j], jsonPosts.getJSONObject(i).getString(column[j]));

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
                        items[i] = (String) result.getValue("title");
                        Log.e("Update", items[i]);


                    }
                }

                adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, items);
                mListView.setAdapter(adapter);

                if (mItem != null) {
                  //  mItem.collapseActionView();
                    //mItem.setActionView(null);
                }

                db.close();

            } catch (JSONException e) {

            }

        }

        cancelMenuLoader();

    }

    public void get_json() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://uec.org.au/app_scripts/?script=torque", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                JSONArray result = null;
                System.out.println(response);
                try {
                    result = new JSONArray(response);
                    System.out.println(result.toString());
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

//    private void downloadItems() {
//        String url = "url you want to download";
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDescription("Some descrition");
//        request.setTitle("Some title");
//// in order for this if to run, you must use the android 3.2 to compile your app
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            request.allowScanningByMediaScanner();
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        }
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");
//
//// get download service and enqueue file
//        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
//    }
//
//    /**
//     * @param context used to check the device version and DownloadManager information
//     * @return true if the download manager is available
//     */
//    public static boolean isDownloadManagerAvailable(Context context) {
//        try {
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
//                return false;
//            }
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
//            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
//                    PackageManager.MATCH_DEFAULT_ONLY);
//            return list.size() > 0;
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
