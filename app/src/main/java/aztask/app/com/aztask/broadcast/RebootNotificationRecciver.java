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
        Log.d("RebootNotiRecciver","JAVED:: reboot has been finished, sheduling synch service.");
        Util.loadUser(context);
        Util.scheduleDataLoaderService(context);
    }
}
