package org.itri.tomato.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.itri.tomato.ListItem;
import org.itri.tomato.R;

import java.util.ArrayList;

/**
 * Created by heiruwu on 7/16/15.
 */
public class MyAutoRunListFragment extends Fragment {
    ArrayList<ListItem> listItems;
//    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myautorunlist, container, false);
//        this.rootView = rootView;
        ListView autoRunList = (ListView) rootView.findViewById(R.id.autoRunList);
        getActivity().setTitle("My AutoRun List");
        return rootView;
    }
}
