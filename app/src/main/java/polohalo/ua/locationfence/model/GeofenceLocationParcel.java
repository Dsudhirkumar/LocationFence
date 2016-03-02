package polohalo.ua.locationfence.model;

import org.parceler.Parcel;

import java.util.HashMap;

/**
 * Created by mac on 2/27/16.
 */
@Parcel
public class GeofenceLocationParcel {//todo fields are public for annotation processing into parsable
    public double latitude;
    public double longitude;
    public String addressFirst;
    public String addressSecond;
    public double radius;
    public String label;
    private HashMap<App, String> appsList =  new HashMap<>();//todo add methods
    public GeofenceLocationParcel() {
    }


    public int getBlockedAppsCount(){
        return appsList.size();
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
}
