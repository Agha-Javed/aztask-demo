package aztask.app.com.aztask.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.Task;
import aztask.app.com.aztask.data.TaskCard;

import aztask.app.com.aztask.data.TaskComparatorByDate;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.ui.CreateTaskActivity;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.ui.UserRegisterationActivity;
import aztask.app.com.aztask.util.Util;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;
import static android.R.id.list;
import static android.R.id.message;
import static aztask.app.com.aztask.R.id.fab;
import static java.lang.Integer.parseInt;

public class MyTasksTab extends Fragment implements LoaderManager.LoaderCallbacks<Map<String, String>> {

    private final String TAG = "MyTasksTab";

    private ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
    Map<Integer, Integer> positions;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;


    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int MY_TASKS_LOADER_ID = 30;

    public MyTasksTab() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mytasks_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.mytasks_cardView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                String taskId = (String) viewHolder.itemView.getTag();
                Log.i(TAG, "Task " + taskId + " is gonna be deleted.");

                new DeleteTaskWorker().execute(taskId);

                // Build appropriate uri with String row id appended

            }
        }).attachToRecyclerView(recyclerView);


        mErrorMessageDisplay = (TextView) view.findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        fab = (FloatingActionButton) view.findViewById(R.id.mytasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if (MainActivity.isUserRegistered()) {
                    intent = new Intent(getContext(), CreateTaskActivity.class);
                } else {
                    intent = new Intent(getContext(), UserRegisterationActivity.class);
                }
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(MY_TASKS_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<Map<String, String>> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        final Map<String, String> result = new ConcurrentHashMap<>();

        return new AsyncTaskLoader<Map<String, String>>(getContext()) {


            @Override
            public Map<String, String> loadInBackground() {

                Log.i("MyTasksDownloadWorker", "Downloading my tasks.");
                String link = Util.SERVER_URL + "/user/" + MainActivity.getUserId() + "/tasks";
                StringBuilder response = new StringBuilder("");

                try {
                    Log.d(TAG, "Link:" + link);

                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setRequestProperty("Accept", "application/json");

                    int HttpResult = con.getResponseCode();
                    result.put("status", "" + HttpResult);
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            response.append(line + "\n");
                        }
                        br.close();
                        result.put("response", response.toString());
                        return result;
                    }
                } catch (Exception exception) {
                    Log.e("CreateTaskWorker", "Error:" + exception);
                    exception.printStackTrace();
                }

                return result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Map<String, String>> loader, Map<String, String> result) {

        JSONArray rootArray;
        List<TaskCard> list = new ArrayList<>();
        positions = new ConcurrentHashMap<Integer, Integer>();

        try {
            rootArray = new JSONArray(result.get("response"));
            int len = rootArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = rootArray.getJSONObject(i);
                TaskCard item = new TaskCard();
                item.setTaskId(obj.getString("task_id"));
                item.setTaskDesc((obj.getString("task_desc")));
                item.setTaskTime(obj.getString("task_time"));
                item.setTaskLocation(obj.getString("task_location"));
                item.setTaskBudget(obj.getString("task_min_max_budget"));
                item.setImageResourceId(R.drawable.app_logo);
                item.setImageResourceId(R.drawable.app_logo);

                item.setIsfav(0);
                item.setIsturned(0);
                list.add(item);
                positions.put(parseInt(item.getTaskId()), i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (list.size() > 0 && "200".equals(result.get("status"))) {
            Collections.sort(list, new TaskComparatorByDate());
            Collections.reverse(list);
            TaskAdapter taskAdapter = new TaskAdapter(list);
            recyclerView.setAdapter(taskAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.i(TAG, "Bundle Arguments:" + getArguments());
            if (getArguments() != null && "true".equals(getArguments().getString("notification")) && getArguments().getString("task") != null) {
                int taskId = Integer.parseInt(getArguments().getString("task"));
                if (positions.containsKey(taskId)) {
                    recyclerView.setLayoutManager(new CustomLinearLayoutManagerWithSmoothScroller(getContext()));
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(positions.get(taskId), 0);
                }
            }
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);

            if(list.size()==0){
                mErrorMessageDisplay.setText("You have not created any task yet.");
                fab.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Map<String, String>> loader) {

    }

    /* @Override
     public Loader<String> onCreateLoader(int id, Bundle args) {

         mLoadingIndicator.setVisibility(View.VISIBLE);

         return new AsyncTaskLoader<String>(getContext()) {


             @Override
             public String loadInBackground() {

                 Log.i("MyTasksDownloadWorker", "Downloading my tasks.");
                 String link = Util.SERVER_URL + "/user/" + MainActivity.getUserId() + "/tasks";
                 StringBuilder result = new StringBuilder("");

                 try {
                     Log.d(TAG, "Link:" + link);

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
         };
     }

     @Override
     public void onLoadFinished(Loader<String> loader, String data) {

         JSONArray rootArray;
         List<TaskCard> list = new ArrayList<>();
         positions = new ConcurrentHashMap<Integer, Integer>();

         try {
             rootArray = new JSONArray(data);
             int len = rootArray.length();
             for (int i = 0; i < len; i++) {
                 JSONObject obj = rootArray.getJSONObject(i);
                 TaskCard item = new TaskCard();
                 item.setTaskId(obj.getString("task_id"));
                 item.setTaskDesc((obj.getString("task_desc")));
                 item.setTaskTime(obj.getString("task_time"));
                 item.setTaskLocation(obj.getString("task_location"));
                 item.setTaskBudget(obj.getString("task_min_max_budget"));
                 item.setImageResourceId(R.drawable.app_logo);
                 item.setImageResourceId(R.drawable.app_logo);

                 item.setIsfav(0);
                 item.setIsturned(0);
                 list.add(item);
                 positions.put(parseInt(item.getTaskId()), i);
             }
         } catch (JSONException e) {
             e.printStackTrace();
         }


         if (list.size() > 0) {
             Collections.sort(list,new TaskComparatorByDate());
             Collections.reverse(list);
             TaskAdapter taskAdapter = new TaskAdapter(list);
             recyclerView.setAdapter(taskAdapter);

             recyclerView.setVisibility(View.VISIBLE);
             fab.setVisibility(View.VISIBLE);

             mErrorMessageDisplay.setVisibility(View.INVISIBLE);
             mLoadingIndicator.setVisibility(View.INVISIBLE);

             Log.i(TAG, "Bundle Arguments:" + getArguments());
             if (getArguments() != null && "true".equals(getArguments().getString("notification")) && getArguments().getString("task") != null) {
                 int taskId = Integer.parseInt(getArguments().getString("task"));
                 if (positions.containsKey(taskId)) {
                     recyclerView.setLayoutManager(new CustomLinearLayoutManagerWithSmoothScroller(getContext()));
                     ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(positions.get(taskId), 0);
                 }
             }
         } else {
             recyclerView.setVisibility(View.INVISIBLE);
             mErrorMessageDisplay.setVisibility(View.VISIBLE);
             mLoadingIndicator.setVisibility(View.INVISIBLE);
             fab.setVisibility(View.INVISIBLE);
         }

     }

     @Override
     public void onLoaderReset(Loader<String> loader) {

     }
 */
    class DeleteTaskWorker extends AsyncTask<String, Void, String> {

        String TAG = "MyTask.DeleteTaskWorker";

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "Deleting Task.");

            try {
                String taskId = params[0];
                Log.i(TAG, "Task Id:" + taskId);
                if ((taskId == null || taskId.length() <= 0)) {
                    return "{\"code\":\"400\",\"message\":\"Invalid Task Id\"}";
                }
                Log.i(TAG, "Task to delete:" + taskId);

                String link = Util.SERVER_URL + "/user/" + MainActivity.getUserId() + "/task/" + taskId + "/delete";
                Log.d(TAG, "Link:" + link);

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
                    String result = sb.toString();
                    Log.i(TAG, "Task Deletion Response:" + result);
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "{\"code\":\"400\",\"message\":\"Invalid Task Id\"}";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject responseObj = null;
            int responseCode = 0;
            try {
                responseObj = new JSONObject(result);
                responseCode = (responseObj.getInt("code") > 0) ? 200 : 400;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                Toast.makeText(getContext(), "This task has been deleted.", Toast.LENGTH_LONG).show();
                getLoaderManager().restartLoader(MY_TASKS_LOADER_ID, null, MyTasksTab.this).forceLoad();
            }
        }
    }
}
