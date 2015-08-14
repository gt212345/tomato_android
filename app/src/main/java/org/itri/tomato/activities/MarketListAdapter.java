package org.itri.tomato.activities;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.itri.tomato.fragments.MyAutoRunListFragment;
import org.itri.tomato.ListItem;
import org.itri.tomato.R;

import java.util.ArrayList;

/**
 * Created by hrw on 15/7/9.
 */
public class MarketListAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    LayoutInflater li;
    Context context;
    Fragment fragment;
    ArrayList<ListItem> items;
    ViewHolder viewHolder;
    toggleListener listener;

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        listener = (MyAutoRunListFragment) fragment;
        listener.onToggle((int)compoundButton.getTag(), b);
    }

    public interface toggleListener {
        void onToggle(int position, boolean able);
    }

    private class ViewHolder {
        ImageView image1, image2;
        TextView content;
        Switch aSwitch;
    }

    public MarketListAdapter(Context context, ArrayList<ListItem> items, Fragment fragment) {
        this.context = context;
        this.items = items;
        this.fragment = fragment;
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
        if (items.get(position).isMy()) {
            if (convertView == null) {
                view = li.inflate(R.layout.item_marketlist, null);
                viewHolder = new ViewHolder();
                viewHolder.aSwitch = (Switch) view.findViewById(R.id.aSwitch);
                viewHolder.aSwitch.setTag(position);
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
            viewHolder.aSwitch.setVisibility(View.VISIBLE);
            viewHolder.aSwitch.setChecked(item.getAble());
            viewHolder.aSwitch.setOnCheckedChangeListener(this);
            viewHolder.content.setText(item.getContent());
            return view;
        } else if (items.get(position).isHas2Image()) {
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
        } else {
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
        }
    }

}
