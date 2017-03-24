package aztask.app.com.aztask.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import aztask.app.com.aztask.service.AZTaskGcmListenerService;
import aztask.app.com.aztask.ui.ProfileActivity;

/**
 * Created by javed.ahmed on 3/15/2017.
 */

public class NotificationReceiver extends BroadcastReceiver{

    private NotificationManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {

        manager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("NotificationReceiver" ,"Action Type:"+intent.getAction());
        if(intent.getAction().equals("aztask.app.com.aztask.broadcast.ignore")){
            Log.d("NotificationReceiver","Cancelling notification");
            manager.cancel(AZTaskGcmListenerService.BROADCAST_NO_ACTION_CODE);
            manager.cancel(AZTaskGcmListenerService.BROADCAST_VIEW_USER_CODE);
        }else if(intent.getAction().equals("aztask.app.com.aztask.broadcast.view_user")){
            Log.d("LOG", "Starting profiling activity.");
            Intent profileActivityIntent=new Intent(context, ProfileActivity.class);

            //intent.getEx
            //Log.i("NotificationReceiver","User Id:"+intent.getExtras().getString("userId"));
            String userId=intent.getStringExtra("userId");
            Log.i("NotificationReceiver","User Id:"+userId);
            profileActivityIntent.putExtra("userId",userId);

            profileActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(profileActivityIntent);
            manager.cancel(AZTaskGcmListenerService.BROADCAST_VIEW_USER_CODE);
            context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }else {
            Log.d("LOG","Fail ABC");
        }

    }
}
