package aztask.app.com.aztask.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import aztask.app.com.aztask.data.AZTaskContract;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

import static android.R.attr.fragment;
import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MyTasksDownloader extends AsyncTask<Void, Void, String> {

    List<TaskCard> tasksList = new ArrayList<TaskCard>();
    RecyclerView MyRecyclerView;
    AppCompatActivity mainActivity;
    Fragment fragmentRef;
    private String CLASS_NAME = "TasksDownloader";
    private Map<Integer, Integer> positions = new HashMap<>();

    private Context context;

    public MyTasksDownloader(Context context) {
        this.context=context;
    }

    public MyTasksDownloader(AppCompatActivity mainActivity, RecyclerView MyRecyclerView) {
        this.MyRecyclerView = MyRecyclerView;
        this.mainActivity = mainActivity;
    }

    public MyTasksDownloader(AppCompatActivity mainActivity, Fragment fragment, RecyclerView MyRecyclerView) {
        this.MyRecyclerView = MyRecyclerView;
        this.mainActivity = mainActivity;
        this.fragmentRef = fragment;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("MyTasksDownloader", "Downloading my tasks.");
        String link = Util.SERVER_URL + "/user/" + Util.getUserId(context) + "/tasks";
        StringBuilder result = new StringBuilder("");

        try {
            Log.d(CLASS_NAME, "Link:" + link);

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestProperty("Accept", "application/json");

            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    result.append(line + "\n");
                }
                br.close();
            }
        } catch (Exception exception) {
            Log.e("CreateTaskWorker", "Error:" + exception);
            exception.printStackTrace();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
      //  Log.i(CLASS_NAME, "User Registered Response:" + result.toString());

        if (result != null && result.trim().length() > 0) {

            JSONArray rootArray = null;
            try {
                rootArray = new JSONArray(result);
                int len = rootArray.length();
                Log.i(CLASS_NAME, "Total Records:" + len);

                if(!(len>0)){
                    Log.i(CLASS_NAME,"Empty Result, returning back.");
                    return;
                }

                context.getContentResolver().delete(AZTaskContract.MY_TASKS_CONTENT_URI, null, null);

                for (int i = 0; i < len; i++) {
                    JSONObject obj = rootArray.getJSONObject(i);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_ID,obj.getString("task_id"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_DESC,obj.getString("task_desc"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_CATEGORY,obj.getString("task_categories"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_TIME,obj.getString("task_time"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LOCATION,obj.getString("task_location"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET,obj.getString("task_min_max_budget"));

/*
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT,obj.getString("contact"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_OWNER_NAME,obj.getString("user"));
                    contentValues.put(AZTaskContract.MyTaskEntry.COLUMN_NAME_TASK_LIKED,obj.getString("liked"));
*/

                    context.getContentResolver().insert(AZTaskContract.MY_TASKS_CONTENT_URI, contentValues);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
