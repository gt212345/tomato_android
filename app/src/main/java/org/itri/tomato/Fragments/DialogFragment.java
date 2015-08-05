package org.itri.tomato.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.itri.tomato.Activities.AddAutoRunActivity;
import org.itri.tomato.R;

import java.util.ArrayList;


/**
 * Created by heiruwu on 7/31/15.
 */
public class DialogFragment extends android.app.DialogFragment implements CompoundButton.OnCheckedChangeListener {

    ArrayList<String> checks;
    String string;

    private static final int CHECK_BOX = 1;
    private static final int RADIO_BUTTON = 2;

    public interface CheckBoxListener {
        void onCheckFinished(ArrayList<String> Strings);
    }

    public interface RadioButtonListener {
        void onRadioFinished(String String);
    }


    public static DialogFragment newInstance(String[] parts, int type) {
        DialogFragment dialogFragment = new DialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("parts", parts);
        args.putInt("type", type);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int counts = getArguments().getStringArray("parts").length;
        int type = getArguments().getInt("type");
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
                        listener.onCheckFinished(checks);
                    }
                }).setNegativeButton("Cancel", null);
                break;
            case RADIO_BUTTON:
                RadioGroup radioGroup = new RadioGroup(getActivity());
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
                        listener.onRadioFinished(string);
                    }
                }).setNegativeButton("Cancel", null);
                break;
        }
        return builder.create();
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
