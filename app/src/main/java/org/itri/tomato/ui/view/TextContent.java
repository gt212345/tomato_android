package org.itri.tomato.ui.view;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by austin on 15/8/12.
 */
public class TextContent extends LinearLayout implements ContentRow {

    public TextContent(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){

    }

    @Override
    public LinearLayout getLayout() {
        return this;
    }
}
