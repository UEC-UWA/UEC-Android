package com.appulse.uec.helpers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appulse.uec.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class NewsAdapter extends BaseAdapter {

    private List listData;

    private List data;

    private LayoutInflater layoutInflater;

    public NewsAdapter(Context context, List listData) {
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
            convertView = layoutInflater.inflate(R.layout.frag_news_list_row, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ManagedEntity item = (ManagedEntity) listData.get(position);

        holder.title.setText((String) item.getValue("title"));

        holder.category.setText((String) item.getValue("category"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        try {
            long unix_time = dateFormat.parse((String)item.getValue("date")).getTime();
            DateFormat df= new SimpleDateFormat("d MMMM yyyy K:mm a");
            String date_formatted = df.format(unix_time);
            holder.date.setText(date_formatted);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.summary.setText((String) item.getValue("summary"));
        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView category;
        TextView summary;
    }
/*
    protected List<ManagedEntity> getFilteredResults(CharSequence constraint) {
        List<ManagedEntity> filteredResults = new ArrayList<ManagedEntity>();

        for (Object c: data) {
            ManagedEntity item = (ManagedEntity) c;
            String searchField = item.getSearchableField();
            if (((String) item.getValue(searchField)).trim().toLowerCase().startsWith(constraint.toString().trim().toLowerCase())) { // you can also use contains instead of startsWith...whatever you need
                filteredResults.add(item);
            }
        }
        return filteredResults;
    }


    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                listData = (List<ManagedEntity>) results.values;
                NewsAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<ManagedEntity> filteredResults = getFilteredResults(constraint);

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }
    */
}

