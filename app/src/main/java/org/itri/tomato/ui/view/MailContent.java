package org.itri.tomato.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.itri.tomato.AutoRunItem;
import org.itri.tomato.R;
import org.itri.tomato.ui.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by austin on 15/8/12.
 */
public class MailContent extends LinearLayout implements ContentRow {

    private TextView mTitleView;
    private EditText mDataView;
    private String mErrMsg;
    private Context mContext;
    private String mContentType = Constants.MAIL_CONTENT;
    private AutoRunItem mItem;
    private boolean mEditMode;

    public MailContent(Context context) {
        super(context);
        init(context);
    }

    public void setItem(AutoRunItem item, boolean edit) {
        mItem = item;
        mEditMode = edit;
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.set_content_mail, this);
        mTitleView = (TextView) view.findViewById(R.id.set_content_mail_title);
        mDataView = (EditText) view.findViewById(R.id.set_content_mail_data);
    }

    private String getData() {
        return mDataView.getText().toString();
    }

    @Override
    public LinearLayout getLayout() {
        return this;
    }

    @Override
    public void buildView() {
        mTitleView.setText(mItem.getDisplay());
        mDataView.setHint(mItem.getCondition());
        if (mEditMode) {
            mDataView.setText(mItem.getValue());
        }
    }

    @Override
    public boolean validate() {
        String data = getData();
        if (TextUtils.isEmpty(data)) {
            mErrMsg = "email require";
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data).matches()) {
            mErrMsg = "email format error";
            return false;
        }
        return true;

    }

    @Override
    public String errMessage() {
        return mErrMsg;
    }

    @Override
    public void getContentData(JSONArray array) {

        JSONObject object = new JSONObject();
        try {
            object.put("agentId", mItem.getAgentId());
            object.put("option", mItem.getOption());
            object.put("conditionType", mContentType);
            object.put("value", getData());
            object.put("agent_parameter", mItem.getConditionType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
