package polohalo.ua.locationfence.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManager {
    private static final String TAG = "LocationManager";
    private static String apiKey = "AIzaSyCaX0pUYOAsO0NQ2oSESLS7HLosSnPAiHU";
    private OnAddressListener listener;
    private Context context;
    private static LocationManager instance;

    public static LocationManager getInstance(){
        if(instance==null)
            instance = new LocationManager();
        return instance;

    }

    public interface OnAddressListener{
        void onSuccess(String result);
        void onError();
    }
    private String getApiKey() {
        return apiKey;
    }

    public void getMyLocationAddress(Context context, LatLng location, OnAddressListener listener) {
        this.listener=listener;
        this.context=context;
        new GeocoderAsyncTask().execute(location.latitude, location.longitude);
    }
    private class GeocoderAsyncTask extends AsyncTask<Double, Void, String>{

        @Override
        protected String doInBackground(Double... location) {
            String address = "";
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);//todo locale
            try {
                //Place your latitude and longitude
                List<Address> addresses = geocoder.getFromLocation(location[0], location[1], 1);
                if (addresses != null)
                    if (addresses.size() != 0) {
                        Address fetchedAddress = addresses.get(0);
                        StringBuilder strAddress = new StringBuilder();
                        for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                            strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                        }
                        Log.e(TAG, "I am at: " + strAddress.toString());
                        address = strAddress.toString();
                    } else
                        Log.e(TAG, "No location found");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(context, "Could not get address..!", Toast.LENGTH_LONG).show();
            }
            return address;
        }
        @Override
        protected void onPostExecute(String result) {
            if(result.equals(""))
                listener.onError();
            else
                listener.onSuccess(result);
        }
    }



}
