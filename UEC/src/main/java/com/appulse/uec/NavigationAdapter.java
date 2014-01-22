package com.appulse.uec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class NavigationAdapter extends BaseAdapter {

    private List listData;

    private List data;

    private LayoutInflater layoutInflater;

    public NavigationAdapter(Context context, List listData) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.frag_navigation_drawer_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavMenuItem m = (NavMenuItem) listData.get(position);

        holder.title.setText(m.title);


        holder.imageView.setImageResource(m.image);
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        ImageView imageView;
    }


}

