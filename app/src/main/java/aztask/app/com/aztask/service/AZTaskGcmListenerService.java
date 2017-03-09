package aztask.app.com.aztask.service;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.ui.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AZTaskGcmListenerService extends GcmListenerService{
	
	//This method will be called on every new message received 
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String action = data.getString("action");
        String message = data.getString("message");
        String task= data.getString("task");
        sendNotification(action,message,task);
    }
    
    //This method is generating a notification and displaying the notification 
    private void sendNotification(String action,String message,String task) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("notification_type", action);
        intent.putExtra("task",task);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
   //     Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

}