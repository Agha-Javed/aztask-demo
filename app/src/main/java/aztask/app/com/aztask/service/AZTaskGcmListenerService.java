package aztask.app.com.aztask.service;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.ui.ProfileActivity;
import aztask.app.com.aztask.ui.UserRegisterationActivity;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.R.attr.action;
import static android.app.PendingIntent.getActivity;

public class AZTaskGcmListenerService extends GcmListenerService {

    public static final int BROADCAST_NO_ACTION_CODE=111;
    public static final int BROADCAST_VIEW_USER_CODE=222;


    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data);
    }


    //This method is generating a notification and displaying the notification 
    private void sendNotification(Bundle data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("notification_type", data.getString("action"));
        intent.putExtra("task", data.getString("task"));

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //     Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_aztask_notification_icon)
                .setContentText(data.getString("message"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if ("assigned".equals(action)) {
            Log.i("GcmListenerService","setting unassigned action");
            noBuilder.addAction(ignoreAction(this));
            notificationManager.notify(BROADCAST_NO_ACTION_CODE, noBuilder.build()); //0 = ID of notification
        } else {
            noBuilder.addAction(viewUserAction(this,data)); // #0
            noBuilder.addAction(ignoreAction(this));
            notificationManager.notify(BROADCAST_VIEW_USER_CODE, noBuilder.build()); //0 = ID of notification
        }

    }

    private static NotificationCompat.Action viewUserAction(Context context,Bundle data) {
        Log.i("GcmListenerService","Setting viewUserAction Intent:"+data.getString("user"));
        Intent viewUserIntent = new Intent();
        viewUserIntent.setAction("aztask.app.com.aztask.broadcast.view_user");

        viewUserIntent.putExtra("userId",data.getString("user"));
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getBroadcast(context, BROADCAST_VIEW_USER_CODE, viewUserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_task_user,
                "View User",
                ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

    private static NotificationCompat.Action ignoreAction(Context context) {
        Log.i("GcmListenerService","Setting Ignore Intent");
        Intent ignoreReminderIntent = new Intent();
        ignoreReminderIntent.setAction("aztask.app.com.aztask.broadcast.ignore");
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getBroadcast(context, BROADCAST_NO_ACTION_CODE, ignoreReminderIntent, 0);
        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_cancel,"No, thanks.",ignoreReminderPendingIntent);
        return ignoreReminderAction;
    }

}