package org.itri.tomato.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.itri.tomato.R;

/**
 * Created by heiruwu on 7/24/15.
 */
public class AutoRunFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_autorun,container,false);
        return rootView;
    }


}
