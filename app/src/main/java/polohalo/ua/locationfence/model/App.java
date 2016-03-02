package polohalo.ua.locationfence.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.R;

/**
 * Created by mac on 2/20/16.
 */
@Table(name = "App")
public class App extends Model {
    private static List<App> blacklistedApps = new ArrayList<>();
    private static List<App> allBlacklistedApps = new ArrayList<>();
    @Column(name = "locationId")
    private Long locationId;
    @Column(name = "selected")
    private boolean selected=false;
    @Column(name = "packgageName")
    private String packageName;
    @Column(name = "label")
    private String label;

    public App(){

    }
    public static List<App> getBlacklistedApps(Long location){
        blacklistedApps = new Select().from(App.class).where("locationId = ?", location).execute();
        return blacklistedApps;
    }
    public static List<App> getAllBlacklistedApps(){
        allBlacklistedApps = new Select().from(App.class).execute();
        return allBlacklistedApps;
    }
    public static void removeAllAppsWithLocation(Long location){
        new Delete().from(App.class).where("locationId = ?", location).execute();
        //new Update(App.class).set("columnName = ?", false);

    }

    public static void addItemToBlacklist(App item, Long locationId){
        item.setLocationId(locationId);
        item.save();
        //getBlacklistedApps();
    }

    public static void deleteItem(String packageName, Long locationId){
         new Delete().from(App.class).where("packgageName = ? AND locationId = ?", packageName, locationId).execute();
        //App.delete(App.class, "where")
        //App.delete(GeofenceLocation.class,id);
        //getBlacklistedApps();
    }

    public App(String packageName, String label){
        this.packageName = packageName;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }
    public Drawable getIconDrawable(Context context){
        Drawable icon;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return ContextCompat.getDrawable(context, R.drawable.ic_default_launcher);//no icon, return default one
        }
        return icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
