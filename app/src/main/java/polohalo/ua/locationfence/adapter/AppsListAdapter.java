package polohalo.ua.locationfence.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.model.App;
import polohalo.ua.locationfence.model.GeofenceLocation;
import polohalo.ua.locationfence.utils.AppsManager;

/**
 * Created by mac on 2/20/16.
 */

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.AppViewHolder> {

    private static final String TAG = "AppListAdapter";
    private List<App> apps;
    private Context context;
    private HashMap<String, App> selectedAppsMap = new HashMap<>();
    private List<App> selectedAppsList;
    private Long location;

    public void updateData() {
        new AdapterAsyncTask().execute();
    }
    class AdapterAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            apps=AppsManager.getAppsList(context);
            List<App> blacklistedApps = App.getBlacklistedApps(location);
            //Log.e(TAG, apps.size() + " versus " + blacklistedApps.size());

            for(int i = 0; i<apps.size();i++){
                for(int k=0;k<blacklistedApps.size();k++){
                    if(apps.get(i).getPackageName().equals(blacklistedApps.get(k).getPackageName())) {
                        apps.get(i).setSelected(true);

                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }

    }


    public AppsListAdapter(Context context, Long location) {//TODO maybe move updating to another thread to make app more responsive
        this.context = context;
        //selectedAppsMap = App.getBlacklistedApps(location);
        //Log.e(TAG, "location is " + location);
        this.location = location;


    }


    @Override
    public int getItemCount() {//todo downt show com.android apps, they are all sytem resources
        if (apps==null)
            return 0;
        else
            return apps.size();
    }


    @Override
    public void onBindViewHolder(AppViewHolder viewHolder, int i) {
        viewHolder.ci = apps.get(i);
        viewHolder.appIcon.setImageDrawable(viewHolder.ci.getIconDrawable(context));
        viewHolder.appName.setText(viewHolder.ci.getLabel());
        viewHolder.appPackage.setText(viewHolder.ci.getPackageName());
        viewHolder.checkbox.setChecked(viewHolder.ci.isSelected());
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        AppViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_app, viewGroup, false);
        viewHolder = new AppViewHolder(v);
        return viewHolder;
    }


    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        App ci;
        protected ImageView appIcon;
        protected TextView appName;
        protected TextView appPackage;
        public CheckBox checkbox;


        public AppViewHolder(View v) {
            super(v);
            appIcon =  (ImageView) v.findViewById(R.id.app_icon);
            appName = (TextView)  v.findViewById(R.id.app_name);
            appPackage = (TextView)  v.findViewById(R.id.app_package);
            checkbox = (CheckBox)  v.findViewById(R.id.checkbox);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String packageName = apps.get(getAdapterPosition()).getPackageName();//no id, got it from system
            ////Log.e(TAG,"id="+id+"  loc=" + location);
            if(apps.get(getAdapterPosition()).isSelected()) {
                //should be removed
                //todo add a
                apps.get(getAdapterPosition()).setSelected(false);
                GeofenceLocation.decreaseAppCount(location);
                //selectedAppsMap.remove(packageName);
                App.deleteItem(packageName, location);
            }
            else {
                //should be added
                apps.get(getAdapterPosition()).setSelected(true);
                GeofenceLocation.increaseAppCount(location);
                //selectedAppsMap.put(packageName, apps.get(getAdapterPosition()));
                App.addItemToBlacklist(apps.get(getAdapterPosition()), location);


            }
            //Log.e(TAG, "total apps selected " + selectedAppsMap.size() + "  " +  getAdapterPosition()  + "    "  + location);
            notifyDataSetChanged();

        }
    }
}
