package aztask.app.com.aztask.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import aztask.app.com.aztask.service.DataLoadingService;
import aztask.app.com.aztask.util.Util;

/**
 * Created by javed.ahmed on 4/26/2017.
 * The purpose of this broad receiver is to remove this key after device reboot,
 * once user opens the application it will again reschedule the service.
 */

public class RebootNotificationRecciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RebootNotiRecciver","JAVED:: reboot has been finished, deleting the key.");
//        sharedPreferences.edit().remove(Util.PREF_KEY_DATA_LOADING_SERVICE).apply();
        scheduleDataLoaderService(context);

/*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(Util.PREF_KEY_DATA_LOADING_SERVICE)) {
        }
*/
    }

    private void scheduleDataLoaderService(Context ctx) {

        //Context ctx = get();
/** this gives us the time for the first trigger.  */
        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 60 * 10; // 5 minutes in milliseconds
        Intent serviceIntent = new Intent(ctx, DataLoadingService.class);
        serviceIntent.setAction("aztask.app.com.aztask.service.load.data");
// make sure you **don't** use *PendingIntent.getBroadcast*, it wouldn't work
        PendingIntent servicePendingIntent =
                PendingIntent.getService(ctx,
                        DataLoadingService.SERVICE_ID, // integer constant used to identify the service
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
// there are other options like setInexactRepeating, check the docs
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
                cal.getTimeInMillis()+interval,
                interval,
                servicePendingIntent
        );

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPreferences.edit().putString(Util.PREF_KEY_DATA_LOADING_SERVICE, "true").apply();
    }
}
