package org.itri.tomato.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.itri.tomato.activities.AddAutoRunActivity;
import org.itri.tomato.activities.EditAutoRunActivity;
import org.itri.tomato.R;
import org.itri.tomato.Utilities;

import java.util.ArrayList;


/**
 * Created by heiruwu on 7/31/15.
 */
public class DialogFragment extends android.app.DialogFragment implements CompoundButton.OnCheckedChangeListener {

    ArrayList<String> checks;
    int num;
    String string;
    RadioGroup radioGroup;
    static Fragment fragment;
    private static final int CHECK_BOX = 1;
    private static final int RADIO_BUTTON = 2;
    private static final int SEARCH_DIALOG = 3;
    private static final int CHECK_BOX_EDIT = 4;
    private static final int RADIO_BUTTON_EDIT = 5;


    public interface CheckBoxListener {
        void onCheckFinished(ArrayList<String> Strings, int num);
    }

    public interface RadioButtonListener {
        void onRadioFinished(String String, int num, boolean isMap);
    }


    public static DialogFragment newInstance(String[] parts, int type, Fragment f, int num, boolean isMap) {
        DialogFragment dialogFragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("parts", parts);
        args.putInt("type", type);
        args.putInt("num", num);
        args.putBoolean("isMap", isMap);
        fragment = f;
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int counts = getArguments().getStringArray("parts").length;
        int type = getArguments().getInt("type");
        final boolean isMap = getArguments().getBoolean("isMap");
        num = getArguments().getInt("num");
        ArrayList<String> names = new ArrayList<>();
        checks = new ArrayList<>();
        for (String tmp : getArguments().getStringArray("parts")) {
            names.add(tmp);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        switch (type) {
            case CHECK_BOX:
                for (int i = 0; i < counts; i++) {
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(names.get(i));
                    checkBox.setOnCheckedChangeListener(this);
                    layout.addView(checkBox);
                }

                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBoxListener listener = (AddAutoRunActivity) getActivity();
                        listener.onCheckFinished(checks, num);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case RADIO_BUTTON:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (AddAutoRunActivity) getActivity();
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case SEARCH_DIALOG:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (AutoRunListFragment) fragment;
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioButtonListener listener = (AutoRunListFragment) fragment;
                        listener.onRadioFinished("all", num, isMap);
                    }
                });
                break;
            case CHECK_BOX_EDIT:
                for (int i = 0; i < counts; i++) {
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(names.get(i));
                    checkBox.setOnCheckedChangeListener(this);
                    layout.addView(checkBox);
                }

                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBoxListener listener = (EditAutoRunActivity) getActivity();
                        listener.onCheckFinished(checks, num);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case RADIO_BUTTON_EDIT:
                radioGroup = new RadioGroup(getActivity());
                radioGroup.setOrientation(LinearLayout.VERTICAL);
                layout.addView(radioGroup);
                for (int i = 0; i < counts; i++) {
                    RadioButton radioButton = new RadioButton(getActivity());
                    radioButton.setText(names.get(i));
                    radioButton.setOnCheckedChangeListener(this);
                    radioGroup.addView(radioButton);
                }
                builder.setView(view).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButtonListener listener = (EditAutoRunActivity) getActivity();
                        listener.onRadioFinished(string, num, isMap);
                    }
                }).setNegativeButton("Cancel", null);
                break;
        }
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int type = getArguments().getInt("type");
        if (type == Utilities.SEARCH_DIALOG) {
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.TOP | Gravity.RIGHT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = -100;
            params.y = 100;
            window.setAttributes(params);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            checks.add(compoundButton.getText().toString());
            string = compoundButton.getText().toString();
        } else {
            checks.remove(compoundButton.getText().toString());
        }
    }

}
