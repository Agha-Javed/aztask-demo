package aztask.app.com.aztask.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import aztask.app.com.aztask.data.AZTaskContract;
import aztask.app.com.aztask.util.Util;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static android.R.attr.action;
import static android.R.attr.id;

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

            int attachedTab=intent.getIntExtra("attachedTab", 0);

            ContentValues cv = new ContentValues();
            if("likeTask".equals(action)){
                cv.put("is_task_liked","true");
            }else{
                cv.put("is_task_liked","false");
            }
            switch (attachedTab){
                case Util.NEARBY_TASKS_TAB:{
                    Uri taskURI = ContentUris.withAppendedId(AZTaskContract.NEARBY_TASKS_CONTENT_URI, taskId);
                    getContentResolver().update(taskURI,cv,null,null);
                }break;
                case Util.ASSIGNED_TASKS_TAB:{
                    Uri taskURI = ContentUris.withAppendedId(AZTaskContract.ASSIGNED_TASKS_CONTENT_URI, taskId);
                    getContentResolver().update(taskURI,cv,null,null);
                }break;
                case Util.MY_TASKS_TAB:{
                    Uri taskURI = ContentUris.withAppendedId(AZTaskContract.MY_TASKS_CONTENT_URI, taskId);
                    getContentResolver().update(taskURI,cv,null,null);
                }break;
            }



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
