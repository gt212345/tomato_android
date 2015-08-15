package org.itri.tomato.ui.view;

import android.widget.LinearLayout;

import org.json.JSONArray;

/**
 * Created by austin on 15/8/12.
 */
public interface ContentRow {

    public LinearLayout getLayout();
    public void buildView();
    public boolean validate();
    public String errMessage();
    public void getContentData(JSONArray array);
}
