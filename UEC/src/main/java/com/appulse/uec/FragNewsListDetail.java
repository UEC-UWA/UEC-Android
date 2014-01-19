package com.appulse.uec;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Matt on 19/01/2014.
 */
public class FragNewsListDetail extends Fragment{

    private static onNewsDetailSelectedListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = inflater.inflate(R.layout.frag_news_list_detail, container, false);

        Button b = (Button) view.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToDetail(8);
            }
        });

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        TextView  t = (TextView) view.findViewById(R.id.editText);
        t.setText(sb.toString());
        return view;
    }

    public interface onNewsDetailSelectedListener {
        public void onNewsDetailSelected(int value);
    }
    public void gotToDetail(int position) {
        if (listener != null) {
            listener.onNewsDetailSelected(position);
        }
    }
    public static void setOnMySignalListener(onNewsDetailSelectedListener listener) {
        FragNewsListDetail.listener = listener;
    }

}
