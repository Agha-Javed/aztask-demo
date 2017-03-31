package aztask.app.com.aztask.util;

import java.util.StringTokenizer;
import java.util.UUID;


import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.ui.MainActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class Util {

    //public static String SERVER_URL="http://10.1.19.31:9000";
    //public static String SERVER_URL = "http://172.16.1.203:9000";
//    public static String SERVER_URL = "http://10.1.106.104:9000";


    public static String SERVER_URL="http://aztask-demo.herokuapp.com";
    public static String PROJECT_NUMBER = "155962838252";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    public static final String PREF_KEY_DEVICEID = "PREF_KEY_DEVICEID";
    public static final String PREF_KEY_DEVICE_LOCATION = "PREF_KEY_DEVICE_LOCATION";
    public static final String PREF_KEY_DEVICE_HOME_LOCATION = "PREF_KEY_DEVICE_HOME_LOCATION";
    public static final String PREF_KEY_DEVICE_CURRENT_LOCATION = "PREF_KEY_DEVICE_CURRENT_LOCATION";
    public static final String PREF_GCM_TOKEN = "PREF_GCM_TOKEN";

    public static final String PREF_KEY_USER = "PREF_KEY_USER";

    public static final int NEARBY_TASKS_TAB_POSITION = 0;
    public static final int ASSIGNED_TASKS_TAB_POSITION = 1;
    public static final int MY_TASKS_TAB_POSITION = 2;


    public static String getDeviceId() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        String deviceId = "";
        if (sharedPreferences.getString(Util.PREF_KEY_DEVICEID, null) != null) {
            deviceId = sharedPreferences.getString(Util.PREF_KEY_DEVICEID, "");
        } else {
            String androidId = android.provider.Settings.Secure.getString(MainActivity.getAppContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
            deviceId = deviceUuid.toString();
            sharedPreferences.edit().putString(Util.PREF_KEY_DEVICEID, deviceId).apply();
        }
        return deviceId;
    }

    public static Location getDeviceLocation() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        Location location = null;
        if (sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, null) != null) {
            String deviceLoc=sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, "");

            StringTokenizer tokens = new StringTokenizer(deviceLoc, ":");
            String latitude = tokens.nextToken();// this will contain "Fruit"
            String longitude = tokens.nextToken();
            Log.i("Util","Device Location:"+tokens);

            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
        } else {
            if (MainActivity.isUserRegistered()) {
                Log.i("Util", "Location:Didn't get any location in preferences, so getting user's default location ");
                User loggedInUser = MainActivity.getRegisteredUser();
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(Double.parseDouble(loggedInUser.getDeviceInfo().getLatitude()));
                location.setLongitude(Double.parseDouble(loggedInUser.getDeviceInfo().getLongitude()));
            } else {
                Log.e("Util", "User isn't registered so getting default location.");
                location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(65.9667d);
                location.setLongitude(-18.5333d);

            }

        }

        return location;
    }


}
