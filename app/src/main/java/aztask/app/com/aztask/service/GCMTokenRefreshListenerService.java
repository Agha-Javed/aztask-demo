package aztask.app.com.aztask.service;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;
import android.util.Log;

import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        intent.putExtra("userId", Util.getUserId(getApplicationContext()));
        startService(intent);
    }
}