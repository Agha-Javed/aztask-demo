package aztask.app.com.aztask.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import aztask.app.com.aztask.util.Util;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TaskNotificationService extends IntentService {

    private String CLASS_NAME = "TaskNotificationService";

    public TaskNotificationService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.i(CLASS_NAME, "notifying task owner about notification.");

        try {
            String action = intent.getStringExtra("action");
            int userId = intent.getIntExtra("userId", 0);
            int taskId = intent.getIntExtra("taskId", 0);


            String link = Util.SERVER_URL + "/user/" + userId + "/"+action+"/" + taskId;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestProperty("Accept", "application/json");

            StringBuilder sb = new StringBuilder("");
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.i(CLASS_NAME, "User Registered Response:" + sb.toString());
            } else {
                Log.i(CLASS_NAME, con.getResponseMessage());
            }
        } catch (Exception exception) {
            Log.e("RegistrationTask", "Error:" + exception);
        }

    }

}
