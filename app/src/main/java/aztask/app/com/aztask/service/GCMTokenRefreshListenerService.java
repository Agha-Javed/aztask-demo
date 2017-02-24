package aztask.app.com.aztask.service;

import java.io.IOException;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import android.util.Log;

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {
    String PROJECT_NUMBER = "155962838252";

    @Override
    public void onTokenRefresh() {
          InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
          try {
			String token = instanceID.getToken(PROJECT_NUMBER, "GCM", null);
			Log.i("GcmIDListenerService:", "Registeration Token:"+token);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}