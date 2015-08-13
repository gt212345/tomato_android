package org.itri.tomato.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.itri.tomato.R;

/**
 * Created by austin on 15/8/12.
 */
public class TextContent extends LinearLayout implements ContentRow {

    public TextContent(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.content_text, this);
    }

    @Override
    public LinearLayout getLayout() {
        return this;
    }
}
