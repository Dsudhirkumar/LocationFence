package polohalo.ua.locationfence.adapter;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.model.App;

/**
 * Created by mac on 2/20/16.
 */

public class AppsBlacklistAdapter extends RecyclerView.Adapter<AppsBlacklistAdapter.AppViewHolder> {

    private static final String TAG = "AppBlacklistAdapter";
    private List<App> apps;
    private Context context;
    private Long location;
    private CoordinatorLayout parentLayout;
    private App lastRemoved;
    private int lastRemovedPostion;

    public void updateData() {
        this.apps = App.getBlacklistedApps(location);
        notifyDataSetChanged();
        Log.e(TAG, "data updated " +  apps.size() + "  " + location);
        //todo remove items which are not in main List
    }

    public AppsBlacklistAdapter(Context context, Long location, CoordinatorLayout parentLayout) {//TODO dummy constructor
        this.context = context;
        this.location = location;
        this.parentLayout = parentLayout;
    }


    @Override
    public int getItemCount() {
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
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        AppViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_app_blacklist, viewGroup, false);
        viewHolder = new AppViewHolder(v);
        return viewHolder;
    }

    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        App ci;
        protected ImageView appIcon;
        protected TextView appName;
        protected TextView appPackage;


        public AppViewHolder(View v) {
            super(v);
            appIcon =  (ImageView) v.findViewById(R.id.app_icon);
            appName = (TextView)  v.findViewById(R.id.app_name);
            appPackage = (TextView)  v.findViewById(R.id.app_package);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, " size of blacklist1 " + App.getAllBlacklistedApps().size());
            lastRemoved = apps.get(getAdapterPosition());
            lastRemovedPostion = getAdapterPosition();
            String packageName = apps.get(getAdapterPosition()).getPackageName();
            apps.remove(getAdapterPosition());
            App.deleteItem(packageName, location);
            notifyItemRemoved(getAdapterPosition());
            Log.e(TAG, " size of blacklist2 " + App.getAllBlacklistedApps().size());


            Snackbar snackbar = Snackbar
                    .make(parentLayout, lastRemoved.getLabel() + " " +context.getResources().getString(R.string.removed_from_blacklist), Snackbar.LENGTH_LONG)
                    .setAction(context.getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e(TAG, "onCLickSnackBar" + location);
                            Log.e(TAG, " size of blacklist3 " + App.getAllBlacklistedApps().size());
                            //App.addItemToBlacklist(lastRemoved, location);
                            App app = new App();
                            //todo doesnt work because holds reference to the object, workaround
                            app.setLabel(lastRemoved.getLabel());
                            app.setLocationId(location);
                            app.setSelected(true);
                            app.setPackageName(lastRemoved.getPackageName());
                            App.addItemToBlacklist(app, location);
                            Log.e(TAG, " size of blacklist4 " + App.getAllBlacklistedApps().size());
                            apps.add(lastRemovedPostion, lastRemoved);
                            notifyItemInserted(lastRemovedPostion);
                        }
                    });
            snackbar.show();
        }
    }
}
