package aztask.app.com.aztask.net;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import aztask.app.com.aztask.data.AZTaskContract;
import aztask.app.com.aztask.data.AZTaskDbHelper;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

/**
 * Created by javed.ahmed on 4/18/2017.
 */

public class AssignedTasksDownloader extends AsyncTask<Void, Void, String> {
    private final String TAG = "AssignedTasksDownloader";
    private Context context;

    public AssignedTasksDownloader(Context context){
        this.context=context;
    }

    @Override
    protected String doInBackground(Void... params) {

        Log.i(TAG, "Downloading assigned tasks.");
        StringBuilder response = new StringBuilder("");

        String link = Util.SERVER_URL + "/user/" + MainActivity.getUserId() + "/assigned_tasks";
        Log.d(TAG, "Link:" + link);

        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestProperty("Accept", "application/json");

            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line).append("\n");
                }
                br.close();
            }

        } catch (Exception exception) {
            Log.e(TAG, "Error:" + exception);
            exception.printStackTrace();
        }

        return response.toString();
    }


    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        if(result!=null && result.trim().length()>0){

            JSONArray rootArray = null;
            Map<String,Integer> taskListMap=null;
            Integer POSITIVE_VALUE=new Integer(1);

            try {
                rootArray = new JSONArray(result);
                int len = rootArray.length();

                if(!(len>0)){
                    Log.i(TAG,"Empty Result, returning back.");
                    return;
                }

                taskListMap=new ConcurrentHashMap<>();
                context.getContentResolver().delete(AZTaskContract.ASSIGNED_TASKS_CONTENT_URI, null, null);

                for (int i = 0; i < len; i++) {
                    JSONObject obj = rootArray.getJSONObject(i);

                    String taskId=obj.getString("task_id");

                    //this logic is to stop inserting duplicate values in db.
                    if(taskListMap.get(taskId)!=null) {
                        continue;
                    }else{
                        taskListMap.put(taskId,POSITIVE_VALUE);
                    }

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_ID,taskId);
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_DESC,obj.getString("task_desc"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_CATEGORY,obj.getString("task_categories"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_TIME,obj.getString("task_time"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LOCATION,obj.getString("task_location"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET,obj.getString("task_min_max_budget"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT,obj.getString("contact"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_NAME,obj.getString("user"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LIKED,obj.getString("liked"));


                    context.getContentResolver().insert(AZTaskContract.ASSIGNED_TASKS_CONTENT_URI, contentValues);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
