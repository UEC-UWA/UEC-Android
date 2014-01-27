package com.appulse.uec.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appulse.uec.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;




public class EventsAdapter extends BaseAdapter {

    private List listData;

    private List data;

    private LayoutInflater layoutInflater;

    public EventsAdapter(Context context, List listData) {
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
        Object listItem = listData.get(position);


        if(listItem instanceof SectionSeparator){
            SectionSeparator item = (SectionSeparator) listItem;
            convertView = layoutInflater.inflate(R.layout.frag_list_section_row, null);

            final TextView sectionView = (TextView) convertView.findViewById(R.id.list_item_section_text);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            try {
                long unix_time = dateFormat.parse(item.sectionName).getTime();
                DateFormat df= new SimpleDateFormat("d MMMM yyyy K:mm a");
                String date_formatted = df.format(unix_time);
                sectionView.setText(date_formatted);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            convertView.setOnClickListener(null);
            convertView.setOnLongClickListener(null);
            convertView.setLongClickable(false);

        } else {
            ManagedEntity item = (ManagedEntity) listItem;
            ViewHolder holder;
            convertView = layoutInflater.inflate(R.layout.frag_events_list_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.eventName);
            holder.location = (TextView) convertView.findViewById(R.id.eventLocation);
            holder.typeImage = (ImageView) convertView.findViewById(R.id.eventTypeImage);
            holder.image = (ImageView) convertView.findViewById(R.id.eventImage);

            holder.name.setText((String) item.getValue("name"));

            holder.location.setText((String) item.getValue("location"));

            if (item.getValue("type").equals("Social")) {
                holder.typeImage.setImageResource(R.drawable.redsolocup);
            } else if (item.getValue("type").equals("Educational")) {
                holder.typeImage.setImageResource(R.drawable.hardhat);

            } else {
                holder.typeImage.setImageResource(R.drawable.gradhat);
            }
            String image = (String) item.getValue("photo_path");
            if (image != null && !image.equals("")) {
                holder.image.setTag(image);
                DownloadImagesTask t = new DownloadImagesTask();
                t.execute(holder.image);
            }
        }
            return convertView;

    }

    static class ViewHolder {
        TextView name;
        TextView location;
        ImageView image;
        ImageView typeImage;
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

