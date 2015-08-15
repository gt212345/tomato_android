package org.itri.tomato.ui;

import android.app.Activity;
import android.widget.LinearLayout;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.ui.view.ContentRow;
import org.itri.tomato.ui.view.MailContent;
import org.itri.tomato.ui.view.TextContent;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by austin on 15/8/12.
 */
public class ContentRower {

    private Activity mActivity;
    private ArrayList<ContentRow> mRowList = new ArrayList<ContentRow>();
    private LinearLayout mRootView;
    private boolean mValidate;

    public ContentRower(Activity activity) {
        mActivity = activity;

    }

    public boolean createContentViews(LinearLayout rootView, ArrayList<AutoRunItem> data) {
        if (data == null) {
            return false;
        }

        mRootView = rootView;
        mRowList.clear();
        for (AutoRunItem item : data) {
            ContentRow v = createView(item);
            if (v != null) {
                mRowList.add(v);
                mRootView.addView(v.getLayout());
            }
        }

        return true;
    }

    private ContentRow createView(AutoRunItem item) {
        ContentRow view;
        switch (item.getConditionType()) {
            case Constants.TEXT_CONTENT:
                view = createTextContentView(item);
                break;
            case Constants.MAIL_CONTENT:
                view = createMailContentView(item);
                break;
            default:
                view = null;
        }
        return view;
    }

    private TextContent createTextContentView(AutoRunItem item) {
        TextContent textView = new TextContent(mActivity);
        textView.setItem(item, false);
        textView.buildView();
        return textView;
    }

    private MailContent createMailContentView(AutoRunItem item) {
        MailContent mailView = new MailContent(mActivity);
        mailView.setItem(item, false);
        mailView.buildView();
        return mailView;
    }

    public String valSetting() {
        for (ContentRow cr : mRowList) {
            if (!cr.validate()) {
                mValidate = false;
                return cr.errMessage();
            }
        }
        mValidate = true;
        return "Success";
    }

    private JSONArray getParaArray() {
        if (!mValidate) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (ContentRow cr : mRowList) {
            cr.getContentData(array);
        }
        return array;
    }

//    Button test = new Button(getApplicationContext());
//    test.setTextSize(20);
//    test.setText("test");
//    test.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            String tt = cr.valSetting();
//            if (tt.equals("Success")) {
//                Log.d("TAG", "it's work");
//            } else {
//                Log.d("TAG", "wrong" + tt);
//            }
//        }
//    });
//    layout.addView(test);
//    cr = new ContentRower(AddAutoRunActivity.this);
//    cr.createContentViews(layout, autoRunItemsWhen);



}
