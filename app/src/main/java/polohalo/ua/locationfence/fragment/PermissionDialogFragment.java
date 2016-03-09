package polohalo.ua.locationfence.fragment;

/**
 * Created by mac on 3/6/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import polohalo.ua.locationfence.R;


/**
 * Created by mac on 10/5/15.
 */
public class PermissionDialogFragment extends DialogFragment {


    private static final int REQUEST_WRITE_SETTINGS = 4;



    public PermissionDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }


    public static PermissionDialogFragment newInstance() {
        PermissionDialogFragment frag = new PermissionDialogFragment();
        //Bundle args = new Bundle();
        //args.putString("title", title);
        //frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_title));
        alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_content));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openSettings();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });

        return alertDialogBuilder.create();
    }
    private void openSettings(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        getActivity().startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
    }

}