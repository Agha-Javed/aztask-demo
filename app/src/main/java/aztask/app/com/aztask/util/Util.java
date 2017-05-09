package aztask.app.com.aztask.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;


import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.net.CheckUserRegisterationWorker;
import aztask.app.com.aztask.service.DataLoadingService;
import aztask.app.com.aztask.service.GCMRegistrationIntentService;
import aztask.app.com.aztask.ui.MainActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

public class Util {

    //public static String SERVER_URL="http://10.1.19.31:9000";
    //public static String SERVER_URL = "http://172.16.2.68:9000";
//    public static String SERVER_URL = "http://172.16.2.68:9000";


    public static String TAG="Util";
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

    public static final String PREF_KEY_DATA_LOADING_SERVICE="PREF_KEY_DATA_LOADING_SERVICE";

    public static final int NEARBY_TASKS_TAB_POSITION = 0;
    public static final int ASSIGNED_TASKS_TAB_POSITION = 1;
    public static final int MY_TASKS_TAB_POSITION = 2;

    public static final int NEARBY_TASKS_TAB=0;
    public static final int ASSIGNED_TASKS_TAB=1;
    public static final int MY_TASKS_TAB=2;


    public static String getDeviceId(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = "";
        if (sharedPreferences.getString(Util.PREF_KEY_DEVICEID, "") != null && sharedPreferences.getString(Util.PREF_KEY_DEVICEID, "").length()>0) {
            deviceId = sharedPreferences.getString(Util.PREF_KEY_DEVICEID, "");
        } else {
            String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
            deviceId = deviceUuid.toString();
            sharedPreferences.edit().putString(Util.PREF_KEY_DEVICEID, deviceId).apply();
        }
        return deviceId;
    }

    public static String prepareRequestForNearbyTasks(Context context) {
        try {
            final JSONObject request = new JSONObject();
            Location location = Util.getDeviceLocation(context);
            request.put("latitude", "" + location.getLatitude());
            request.put("longitude", "" + location.getLongitude());
            request.put("userId", getUserId(context));
            return request.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getUserId(Context context) {
        User loggedInUser=loadUser(context);
        return (loggedInUser != null) ? loggedInUser.getUserId() : 0;
    }

    public static User getRegisteredUser(Context context) {
        return loadUser(context);
    }

    public static Location getDeviceLocation(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Location location = null;
        if (sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, "") != null && sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, "").length()>0) {
            String deviceLoc=sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, "");

            StringTokenizer tokens = new StringTokenizer(deviceLoc, ":");
            String latitude = tokens.nextToken();// this will contain "Fruit"
            String longitude = tokens.nextToken();
            Log.i("Util","Device Location:"+tokens);

            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
        } else {
            if (isUserRegistered(context)) {
                Log.i("Util", "Location:Didn't get any location in preferences, so getting user's default location ");
                User loggedInUser =getRegisteredUser(context);
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


    public static String getFormattedDate(String taskTimeInString) {

        try {
            final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
            Date date = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH).parse(taskTimeInString);
            long timeInMilis= date.getTime();

            Calendar taskTime = Calendar.getInstance();
            taskTime.setTimeInMillis(timeInMilis);

            Calendar now = Calendar.getInstance();

            final String dateTimeFormatString = "EEE, dd/MM/yyyy, h:mm";
            final String onlyTimeTimeFormatString = "h:mm";

            final long HOURS = 60 * 60 * 60;
            String meridiem = taskTime.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());
            if (now.get(Calendar.DATE) == taskTime.get(Calendar.DATE) ) {
                return "today "+ DateFormat.format(onlyTimeTimeFormatString, taskTime)+meridiem;
            } else if (now.get(Calendar.DATE) - taskTime.get(Calendar.DATE) == 1  ){
                return "yesterday "+ DateFormat.format(onlyTimeTimeFormatString, taskTime)+meridiem;
            } else {
                return DateFormat.format(dateTimeFormatString, taskTime).toString()+meridiem;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";

    }


    public static String shortenText(String text){
        if(text!=null && text.length()>0){
            if(text.length()>40){
                String trimmedText = text.substring(0, Math.min(text.length(), 30));
                return trimmedText+"..";
            }
        }
        return text;
    }


    public static void scheduleDataLoaderService(Context ctx) {

        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 60; // 5 minutes in milliseconds
        Intent serviceIntent = new Intent(ctx, DataLoadingService.class);
        serviceIntent.setAction("aztask.app.com.aztask.service.load.data");

        PendingIntent servicePendingIntent =
                PendingIntent.getService(ctx,
                        DataLoadingService.SERVICE_ID, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
                cal.getTimeInMillis()+interval,
                interval,
                servicePendingIntent
        );

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPreferences.edit().putString(Util.PREF_KEY_DATA_LOADING_SERVICE, "true").apply();
    }

    public static User loadUser(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        Log.i(TAG, "Device ID :" + getDeviceId(context));

        User loggedInUser=null;

        if (preferences.getString(Util.PREF_KEY_DEVICEID, "") != null && preferences.getString(Util.PREF_KEY_DEVICEID, "").length()>0) {
            String userRefInJSONForm = preferences.getString(Util.PREF_KEY_USER, "");
            if (userRefInJSONForm != null && userRefInJSONForm.length()>0) {
                Log.i(TAG, "User exists in preferences, parsing it.");
                loggedInUser = gson.fromJson(userRefInJSONForm, User.class);
            } else {
                Log.i(TAG, "User doesn't exist in preferences, getting it from server and will save into preferences");
                String deviceId = preferences.getString(Util.PREF_KEY_DEVICEID, "");
                loggedInUser =(deviceId!=null && deviceId.length()>0) ? getUserByDeviceId(deviceId) : null;
                if (loggedInUser != null) {
                    String userInJSONForm = gson.toJson(loggedInUser);
                    preferences.edit().putString(Util.PREF_KEY_USER, userInJSONForm).apply();
                }
            }
        } else {
            String deviceId =getDeviceId(context);
            loggedInUser = getUserByDeviceId(deviceId);
            if (loggedInUser != null) {
                String userInJSONForm = gson.toJson(loggedInUser);
                preferences.edit().putString(Util.PREF_KEY_USER, userInJSONForm).apply();
            }
        }

        if (loggedInUser != null && preferences.getString(Util.PREF_GCM_TOKEN, "").length()<=0) {
            Intent itent = new Intent(context, GCMRegistrationIntentService.class);
            itent.putExtra("userId", loggedInUser.getUserId());
            context.startService(itent);
        }

        Log.i(TAG, "Logged In User :" + loggedInUser);

        return loggedInUser;
    }


    public static User getUserByDeviceId(String deviceId) {
        try {
            User user = new CheckUserRegisterationWorker().execute(deviceId).get();
            Log.i(TAG, "User is registered:" + ((user != null) ? user.getDeviceInfo().getDeviceId() : "No"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isUserRegistered(Context context) {
        User loggedInUser=loadUser(context);
        if(loggedInUser!=null){
            return true;
        }
        return false;
    }
}
