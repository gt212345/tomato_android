package org.itri.tomato.ui;

import android.app.Activity;

import org.itri.tomato.ui.view.ContentRow;

import java.util.ArrayList;

/**
 * Created by austin on 15/8/12.
 */
public class ContentRower {

    private Activity mActivity;
    private ArrayList<ContentRow> mRowList = new ArrayList<ContentRow>();


    public ContentRower(Activity activity) {
        mActivity = activity;

    }


}
