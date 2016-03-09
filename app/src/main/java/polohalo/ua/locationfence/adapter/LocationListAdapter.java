package polohalo.ua.locationfence.adapter;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.activity.MainActivity;
import polohalo.ua.locationfence.model.App;
import polohalo.ua.locationfence.model.GeofenceLocation;

/**
 * Created by mac on 2/20/16.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationViewHolder> {

    private static final String TAG = "LocationListAdapter";
    private List<GeofenceLocation> locations;
    private Context context;
    private View highlightedView;
    private Long highlightedId;
    private int highlightedPosition;


    public void updateData() {
        Log.e(TAG, "updating data");
        this.locations = GeofenceLocation.getAll();
        notifyDataSetChanged();
    }

    public LocationListAdapter(Context context) {//TODO dummy constructor
        this.context = context;

    }


    @Override
    public int getItemCount() {
        if (locations==null)
            return 0;
        else
            return locations.size();
    }

    @Override
    public void onBindViewHolder(LocationViewHolder viewHolder, int i) {//todo something with those null checks

        viewHolder.ci = locations.get(i);
        Log.e(TAG, "binding with radius = " + viewHolder.ci.getRadius());

        viewHolder.locationAddress.setText(viewHolder.ci.getAddressFirst());
        if(!viewHolder.ci.getLabel().equals(""))
            viewHolder.locationLabel.setText(viewHolder.ci.getLabel() + ", " + (int)viewHolder.ci.getRadius() + " m");
        else
            viewHolder.locationLabel.setText( (int)viewHolder.ci.getRadius() + " m");
        viewHolder.locationCity.setText(viewHolder.ci.getAddressSecond());
        viewHolder.appsCount.setText(viewHolder.ci.getBlockedAppsCount() + " "
                + (viewHolder.ci.getBlockedAppsCount()==1 ?
                context.getResources().getString(R.string.blocked_app) : context.getResources().getString(R.string.blocked_apps)));//todo handle singularity and grammar rules
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LocationViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_location, viewGroup, false);
        viewHolder = new LocationViewHolder(v);
        return viewHolder;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        GeofenceLocation ci;
        protected TextView locationAddress;
        protected TextView locationLabel;
        protected TextView locationCity;
        protected TextView appsCount;


        public LocationViewHolder(View v) {
            super(v);
            locationAddress =  (TextView) v.findViewById(R.id.address);
            locationCity = (TextView)  v.findViewById(R.id.city);
            locationLabel = (TextView)  v.findViewById(R.id.label);
            appsCount = (TextView) v.findViewById(R.id.blocked_apps_count);
            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {//todo show bottomSheet with edit and delete option
            Log.e(TAG, "id " + ci.getId());
            if(highlightedView!=null) {
                if (highlightedView == view) {//todo it would be nice to change elevation, BUT setElevation() accepts float which is not dp pixels but simple pixels without scaling to density
                    view.setSelected(false);
                    highlightedView = null;
                    Log.e(TAG, "here");
                    highlightedId = null;
                    highlightedPosition = 0;
                    ((MainActivity)context).setBottomViewState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else{
                    highlightedView.setSelected(false);
                    highlightedView = view;
                    highlightedView.setSelected(true);
                    highlightedId = ci.getId();//todo why we need id?
                    highlightedPosition = getAdapterPosition();
                    ((MainActivity)context).setBottomViewState(BottomSheetBehavior.STATE_EXPANDED);

                }
            }
            else {
                view.setSelected(true);//todo wont work because of viewholder pattern
                highlightedView = view;

                Log.e(TAG,"or here");

                highlightedId = ci.getId();//todo why we need id?
                highlightedPosition = getAdapterPosition();
                ((MainActivity)context).setBottomViewState(BottomSheetBehavior.STATE_EXPANDED);

            }
            Log.e(TAG, "item pressed");
            //Intent intent = new Intent(context, EditActivity.class);
            //intent.putExtra("id", ci.getId());
            //context.startActivity(intent);
        }
    }
    public void uncheckLastChecked(){
        highlightedView.setSelected(false);
        highlightedId=null;
        highlightedView=null;
    }
    public Long getHighlightedId(){
        Log.e(TAG, "highlighted id is" + highlightedId);//candidate to break everythinh
        return highlightedId;
    }
    public void removeHighlightedItem(){
        App.removeAllAppsWithLocation(locations.get(highlightedPosition).getId());
        GeofenceLocation.deleteItem(highlightedId);
        locations.remove(highlightedPosition);
        highlightedView=null;
        //todo delete items from second database that correspond to location
        //todo make call to database
        Log.e(TAG, "removing item " +highlightedPosition);
        notifyItemRemoved(highlightedPosition);

    }
}
