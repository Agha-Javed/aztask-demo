package aztask.app.com.aztask.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import aztask.app.com.aztask.net.AssignedTasksDownloader;
import aztask.app.com.aztask.net.MyTasksDownloader;
import aztask.app.com.aztask.net.NearbyTasksDownloader;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;


public class DataLoadingService extends IntentService {
    public static final int SERVICE_ID=500;
    private static final String TAG="DataLoadingService";

    public DataLoadingService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"JAVED:: Yay!!! Service Called.");
        if(isNetworkAvailable()){
            downloadNearByTasks();
            downloadAssignedTasks();
            downloadMyTasks();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void downloadNearByTasks(){
        new NearbyTasksDownloader(getApplicationContext(),false).execute();
    }

    private void downloadAssignedTasks(){
        new AssignedTasksDownloader(getApplicationContext()).execute();
    }


    private void downloadMyTasks(){
        new MyTasksDownloader(getApplicationContext()).execute();
    }

}
