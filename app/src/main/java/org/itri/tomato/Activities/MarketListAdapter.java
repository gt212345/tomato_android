package org.itri.tomato.Activities;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.itri.tomato.MarketListItem;
import org.itri.tomato.R;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class MarketListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<MarketListItem> items;
    ViewHolder viewHolder;

    private class ViewHolder {
        ImageView image1,image2;
        TextView content;
    }

    public MarketListAdapter(Context context, ArrayList<MarketListItem> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
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
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_marketlist,null);
            viewHolder = new ViewHolder();
            viewHolder.image1 = (ImageView) view.findViewById(R.id.service1);
            viewHolder.image2 = (ImageView) view.findViewById(R.id.service2);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            MarketListItem item = items.get(position);
            viewHolder.image1.setImageBitmap(item.getImage1());
            viewHolder.image2.setImageBitmap(item.getImage2());
            viewHolder.content.setText(item.getContent());
        }
        return view;
    }
}
