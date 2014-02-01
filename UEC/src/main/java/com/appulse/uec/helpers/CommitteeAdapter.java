package com.appulse.uec.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appulse.uec.R;

import java.util.List;

public class CommitteeAdapter extends BaseAdapter {

    private List listData;

    private List data;

    private LayoutInflater layoutInflater;

    public CommitteeAdapter(Context context, List listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.data = listData;

    }

    @Override
    public int getCount() {
        if (listData != null) {
            return listData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;


        //  ManagedEntity item = (ManagedEntity) listData.get(position);
        Object item_object = listData.get(position);
        if (item_object != null) {
            if (item_object instanceof SectionSeparator) {
                SectionSeparator item = (SectionSeparator) item_object;
                v = layoutInflater.inflate(R.layout.frag_list_section_row, null);

                final TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText((String) item.sectionName);

                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);

            } else {
                ManagedEntity item = (ManagedEntity) item_object;
                v = layoutInflater.inflate(R.layout.frag_committee_list_row, null);

                final TextView memberName = (TextView) v.findViewById(R.id.memberName);

                final TextView title = (TextView) v.findViewById(R.id.memberTitle);

                final ImageView image = (ImageView) v.findViewById(R.id.memberImage);
                memberName.setText((String) item.getValue("first_name") + " " + item.getValue("last_name"));

                title.setText((String) item.getValue("position"));

                image.setTag(item.getValue("photo_path"));
                DownloadImagesTask task = new DownloadImagesTask();
                task.execute(image);
            }
        }
        return v;
    }

}

