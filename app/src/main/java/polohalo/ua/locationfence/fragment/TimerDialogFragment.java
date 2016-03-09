package polohalo.ua.locationfence.fragment;

/**
 * Created by mac on 3/6/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.utils.AppsManager;


/**
 * Created by mac on 10/5/15.
 */
public class TimerDialogFragment extends DialogFragment {

    private SeekBar seekBar;
    private TextView seekbarValue;
    private Button button;
    private int periodValue;


    public TimerDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }


    public static TimerDialogFragment newInstance() {
        TimerDialogFragment frag = new TimerDialogFragment();
        return frag;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_timer);
        dialog.setTitle(R.string.dialog_timer_title);
        seekBar = (SeekBar)dialog.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                periodValue = (int)(5 + (progress/100.0*115.0));
                seekbarValue.setText("Period = " + periodValue + " sec");//todo not language flexible
                AppsManager.setRepeatingPeriod(getContext(), periodValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AppsManager.setRepeatingPeriod(getContext(), periodValue);

            }
        });
        seekbarValue = (TextView) dialog.findViewById(R.id.seekBar_period);
        button = (Button)dialog.findViewById(R.id.button_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

}