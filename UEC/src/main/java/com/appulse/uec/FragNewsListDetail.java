package com.appulse.uec;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * Author: Can Elmas <can.elmas@pozitron.com>
 * Date: 1/14/13 11:17 AM
 */
public final class FragNewsListDetail extends Fragment {

    private String content;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {

            content = String.valueOf(bundle.getString("content"));
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frag_news_list_detail, container, false);

        if (content != null) {

            ((WebView) view.findViewById(R.id.webView)).loadData(content, "text/html", "UTF-8");

        }

        return view;

    }
}
