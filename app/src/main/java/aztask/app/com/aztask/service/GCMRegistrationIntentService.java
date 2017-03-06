package aztask.app.com.aztask.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import aztask.app.com.aztask.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import static aztask.app.com.aztask.util.Util.PROJECT_NUMBER;


public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String REGISTRATION_TOKEN_SENT = "RegistrationTokenSent";
    private String CLASS_NAME="GCMRegistrationIntentService";


    public GCMRegistrationIntentService() {
        super("");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
    	Log.i("GCMRegisterationService", "Handling Registeration Event.");
    	int userId=intent.getIntExtra("userId", 0);
        registerGCM(userId);
    }

    private void registerGCM(int userId) {
        String token = null;
        try {
        	Log.i("GCMRegisterationService", "Registering GCM token for user "+userId);

        	InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        	Log.i("GCMRegisterationService", "Got instance "+instanceID);

            token = instanceID.getToken(Util.PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i("GCMRegIntentService", "token:" + token);

            sendRegistrationTokenToServer(userId,token);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


	private void sendRegistrationTokenToServer(int userId, String token) {
		
		Log.i("RegisterationTask", "Registering User.");

		try {

			String link = Util.SERVER_URL + "/user/"+userId+"/registerGCMToken";

			String registerationRequest ="{\"token\":\""+token+"\"}";

			if (!(registerationRequest != null) || !(registerationRequest.length() > 0)) {
				Log.i(CLASS_NAME, "The request is empty,so returning back.");
			}

			Log.i(CLASS_NAME, "Request:" + registerationRequest);

			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(registerationRequest);
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				Log.i(CLASS_NAME, "GCM Token Registration Response:"+sb.toString());
				br.close();
			} else {
				Log.i(CLASS_NAME, con.getResponseMessage());
			}
		} catch (Exception exception) {
			Log.e("RegisterationTask", "Error:" + exception);
		}

	}
    
}