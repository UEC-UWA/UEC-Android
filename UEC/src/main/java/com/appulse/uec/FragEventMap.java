package com.appulse.uec;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragEventMap extends Fragment {

    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;

    private String address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.frag_event_map, container, false);

        address = getArguments().getString("address");
        if (address != null) {
            address = address.replaceAll(" ","%20");
            address =  address.replace("\n", "");
        }

        try {
            MapsInitializer.initialize(getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO handle this situation
        }

        mMapView = (MapView) inflatedView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(inflatedView);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            getLatLong(address);
            if (mMap != null) {
                getLatLong(address);
            }
        }
    }

    private void setUpMap(double lng, double lat) {
        if (mMap != null)  {
         mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Event Location"));

        }
    }

    public void getLatLong(String address) {
     //  String clean_address = address.replace(' ','%20');
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                JSONObject result = null;
                try {
                    result = new JSONObject(response);
                } catch (JSONException e) {

                }
                Log.e("Main", result.toString());
                try {
                    if (result.get("status").toString().equals("OK")) {

                        JSONArray r = result.getJSONArray("results");

                        JSONObject r1 = r.getJSONObject(0);
                        JSONObject geo = r1.getJSONObject("geometry");

                        double lat = geo.getJSONObject("location").getDouble("lat");
                        double lng = geo.getJSONObject("location").getDouble("lng");
                        setUpMap( lat,lng);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleResponse(JSONArray jsonPosts) {


        if (jsonPosts == null) {
            //updateDisplayForError();
        } else {
           // jsonPosts.getJSONObject(i).getInt("id");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
}