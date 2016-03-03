package polohalo.ua.locationfence.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mac on 2/27/16.
 */
@Table(name = "Locations")
public class GeofenceLocation extends Model {
    private static final String TAG = "GeofenceLocation";//todo fields are public for annotation processing into parsable

    @Column(name = "Latitude")
    public double latitude;
    @Column(name = "blockedAppsCount")
    public int blockedAppsCount;
    @Column(name = "Longitude")
    public double longitude;
    @Column(name = "addressFirst")
    public String addressFirst;
    @Column(name = "addressSecond")
    public String addressSecond;
    @Column(name = "radius")
    public double radius;
    @Column(name = "label")
    public String label;
    private HashMap<App, String> appsList =  new HashMap<>();//todo add methods
    private static List<GeofenceLocation> items;
    public GeofenceLocation() {
    }

    public static GeofenceLocation getById(Long id){
         List<GeofenceLocation> list = new Select()
                .from(GeofenceLocation.class)
                .execute();
        Log.e(TAG, "getByid " + list.size());
        for(int i = 0 ; i<list.size();i++){
            Log.e(TAG,  id +   "    " + list.get(i).getId()  +  " "+list.get(i).getLabel());
            if(id.equals(list.get(i).getId()))
                return list.get(i);
        }
        return new GeofenceLocation();
        //return list.get(0);//stupid workaround
    }

    public static void addItem(GeofenceLocation item){
        item.save();
        //getAll();

    }
    public static void updateItem(GeofenceLocation newData, Long id){
        new Update(GeofenceLocation.class).set("Latitude = ? and Longitude = ? and addressFirst = ? " +
                "and addressSecond = ? and radius = ? and label = ?",
                newData.getLatitude(), newData.getLongitude(), newData.getAddressFirst(), newData.getAddressSecond(),
                newData.getRadius(), newData.getLabel()).where("id = ?", id).execute();
    }

    public static List<GeofenceLocation> getAll() {
        items = new Select().from(GeofenceLocation.class).execute();
        //deleta a quesry
        //new Delete().from(Item.class).where("remote_id = ?", 1).execute();
        return items;
    }
    public static void deleteItem(Long id){
        GeofenceLocation.delete(GeofenceLocation.class,id);
        //getAll();
    }


    public int getBlockedAppsCount(){
        return blockedAppsCount;
    }
    public void setBlockedAppsCount(int count){
        blockedAppsCount= count;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getAddressFirst(){
        if(addressFirst!=null)
            return addressFirst;
        return "";
    }
    public String getAddressSecond(){
        if(addressSecond!=null)
            return addressSecond;
        return "";
    }

    public void setAddress(String[] address) {
        if(address.length>1)
            this.addressSecond = address[1];
        this.addressFirst = address[0];
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
//todo remove duplicate code
    public static void decreaseAppCount(Long location) {
        List<GeofenceLocation> list = new Select()
                .from(GeofenceLocation.class)
                .where("id = ?", location)
                .execute();
        int newCount = list.get(0).getBlockedAppsCount();
        newCount--;
        new Update(GeofenceLocation.class).set("blockedAppsCount = ?",newCount).where("id = ?", location).execute();
    }
    public static void increaseAppCount(Long location) {
        List<GeofenceLocation> list = new Select()
                .from(GeofenceLocation.class)
                .where("id = ?", location)
                .execute();
        int newCount = list.get(0).getBlockedAppsCount();
        newCount++;
        new Update(GeofenceLocation.class).set("blockedAppsCount = ?",newCount).where("id = ?", location).execute();
    }
}
