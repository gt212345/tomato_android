package org.itri.tomato.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.itri.tomato.ListItem;
import org.itri.tomato.R;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class MarketListAdapter extends BaseAdapter {
    LayoutInflater li;
    ArrayList<ListItem> items;
    ViewHolder viewHolder;

    private class ViewHolder {
        ImageView image1, image2;
        TextView content;
    }

    public MarketListAdapter(Context context, ArrayList<ListItem> items) {
        this.items = items;
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (items.get(position).isHas2Image()) {
            if (convertView == null) {
                view = li.inflate(R.layout.item_marketlist, null);
                viewHolder = new ViewHolder();
                viewHolder.image1 = (ImageView) view.findViewById(R.id.service1);
                viewHolder.image2 = (ImageView) view.findViewById(R.id.service2);
                viewHolder.content = (TextView) view.findViewById(R.id.content);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            ListItem item = items.get(position);
            viewHolder.image1.setImageBitmap(item.getImage1());
            viewHolder.image2.setImageBitmap(item.getImage2());
            viewHolder.content.setText(item.getContent());
            return view;
        } else if (!items.get(position).isChannels()) {
            if (convertView == null) {
                view = li.inflate(R.layout.item_drawerlist, null);
                viewHolder = new ViewHolder();
                viewHolder.image1 = (ImageView) view.findViewById(R.id.icon);
                viewHolder.content = (TextView) view.findViewById(R.id.drawer_text);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            ListItem item = items.get(position);
            viewHolder.image1.setImageBitmap(item.getImage1());
            viewHolder.content.setText(item.getContent());
            return view;
        } else {
            if (convertView == null) {
                view = li.inflate(R.layout.item_drawerlist, null);
                viewHolder = new ViewHolder();
                viewHolder.image1 = (ImageView) view.findViewById(R.id.icon);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            ListItem item = items.get(position);
            viewHolder.image1.setImageBitmap(item.getImage1());
            return view;
        }
    }
}
