package com.appulse.uec;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Matt on 27/01/2014.
 */
public class FragAboutApp extends Fragment {

    private static onAboutAppVersionListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_about_app_detail, container, false);

        final Button button = (Button) view.findViewById(R.id.feedbackButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"john.clema@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });


        final Button buttonVersion = (Button) view.findViewById(R.id.versionButton);
        buttonVersion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if (listener != null) {
                   listener.onAboutAppVersion();
               }
            }
        });

        return view;
    }

    public interface onAboutAppVersionListener {
        public void onAboutAppVersion();
    }

    public static void setOnMySignalListener(onAboutAppVersionListener listener) {
        FragAboutApp.listener = listener;
    }



}
