package aztask.app.com.aztask.util;

import java.util.UUID;

import aztask.app.com.aztask.DeviceLocation;
import aztask.app.com.aztask.ui.MainActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Util {

    //public static String SERVER_URL="http://10.1.19.31:9000";
    public static String SERVER_URL = "http://172.16.0.36:9000";
//    public static String SERVER_URL = "http://10.1.106.104:9000";


    //	public static String SERVER_URL="http://aztask-demo.herokuapp.com";
    public static String PROJECT_NUMBER = "155962838252";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    public static final String PREF_KEY_DEVICEID = "PREF_KEY_DEVICEID";
    public static final String PREF_KEY_DEVICE_LOCATION = "PREF_KEY_DEVICE_LOCATION";
    public static final String PREF_KEY_USER = "PREF_KEY_USER";

    public static final int NEARBY_TASKS_TAB_POSITION=0;
    public static final int ASSIGNED_TASKS_TAB_POSITION=1;
    public static final int MY_TASKS_TAB_POSITION=2;



    public static String getDeviceId() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());//getSharedPreferences(Util.PREF_KEY_DEVICEID, Context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString(Util.PREF_KEY_DEVICEID, null);

        Log.i("Util", "Got Device Id:" + deviceId);


/*
        final TelephonyManager tm = (TelephonyManager) MainActivity.getAppContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		Log.i("MainActivity", "Original Device Id:" + tm.getDeviceId());

		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(MainActivity.getAppContext().getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
*/

        return deviceId;
    }

    public static Location getDeviceLocation() {


        LocationManager lm = (LocationManager) MainActivity.getAppContext().getSystemService(Context.LOCATION_SERVICE);

        DeviceLocation locationListener = new DeviceLocation();
        if (ActivityCompat.checkSelfPermission(MainActivity.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 15, locationListener);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location==null){
			Log.e("Util", "The application couldn't get real coordinates, so getting home location.");
			location=new Location(LocationManager.GPS_PROVIDER);
			location.setLatitude(65.9667d);
			location.setLongitude(-18.5333d);
		}
		return location;
	}
	



}
