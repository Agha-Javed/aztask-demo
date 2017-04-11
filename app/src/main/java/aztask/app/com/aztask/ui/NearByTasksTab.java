package aztask.app.com.aztask.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.data.TaskComparatorByDate;
import aztask.app.com.aztask.net.TasksDownloaderWorker;
import aztask.app.com.aztask.util.Util;

import android.content.Intent;
import android.location.Location;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class NearByTasksTab extends Fragment implements LoaderManager.LoaderCallbacks<Map<String,String>> {

    private static String TAG = "NearByTasksTab";
    private static int NEARBY_TASKS_LOADER_ID = 10;

    private ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    public NearByTasksTab() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle("Tasks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.nearbytasks_fragment, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
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


        recyclerView = (RecyclerView) view.findViewById(R.id.nearbytasks_cardView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mErrorMessageDisplay = (TextView) view.findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle requestData = new Bundle();
        requestData.putString("params", getRequest());
        getLoaderManager().initLoader(NEARBY_TASKS_LOADER_ID, requestData, this).forceLoad();
    }

    private String getRequest() {
        try {
            final JSONObject taskInfo = new JSONObject();
            Location location = Util.getDeviceLocation();
            taskInfo.put("latitude", "" + location.getLatitude());
            taskInfo.put("longitude", "" + location.getLongitude());
            taskInfo.put("userId", MainActivity.getUserId());
            return taskInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public Loader<Map<String, String>> onCreateLoader(int id, final Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);

        final Map<String,String> result=new ConcurrentHashMap<>();

        return new AsyncTaskLoader<Map<String, String>>(getContext()) {

            @Override
            public Map<String, String> loadInBackground() {
                Log.i("TasksDownloaderWorker", "Downloading nearby tasks.");
                String link = "";
                if (MainActivity.isUserRegistered())
                    link = Util.SERVER_URL + "/tasks/list/nearby";
                else
                    link = Util.SERVER_URL + "/tasks/list/new";

                StringBuilder jsonResult = new StringBuilder("");

                try {
                    String downloadNearbyTasksRequest = args.getString("params");// params[0];// prepareDownloadNearByTasksRequest();//taskInfo.toString();

                    if (!(downloadNearbyTasksRequest != null) || !(downloadNearbyTasksRequest.length() > 0)) {
                        Log.i(TAG, "The request is empty,so returning back.");
                    }

                    Log.i(TAG, "Request:" + downloadNearbyTasksRequest);

                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setRequestProperty("Accept", "application/json");

                    if (MainActivity.isUserRegistered()) {
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoOutput(true);

                        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                        wr.write(downloadNearbyTasksRequest);
                        wr.flush();

                    }


                    int HttpResult = con.getResponseCode();
                    result.put("status",""+HttpResult);
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            jsonResult.append(line + "\n");
                        }
                        br.close();

                        result.put("response",jsonResult.toString());

                        return result;
                    } else {
                        Log.i(TAG, con.getResponseMessage());
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
        List<TaskCard> list=new ArrayList<>();
        try {
            rootArray = new JSONArray(result.get("response"));
            int len = rootArray.length();

            for (int i = 0; i < len; i++) {
                JSONObject obj = rootArray.getJSONObject(i);
                TaskCard item = new TaskCard();
                item.setTaskId(obj.getString("task_id"));
                item.setTaskDesc((obj.getString("task_desc")));
//                item.setTaskTime(Util.getFormattedDate(obj.getString("task_time")));
                item.setTaskTime(obj.getString("task_time"));

                item.setTaskLocation(obj.getString("task_location"));
                item.setTaskBudget(obj.getString("task_min_max_budget"));
                item.setTaskOwnerContact(obj.getString("contact"));
                item.setTaskOwnerName(obj.getString("user"));

                Log.i(TAG,"Task Owner Contact:"+item.getTaskOwnerContact());

                item.setImageResourceId(R.drawable.app_logo);
                item.setImageResourceId(R.drawable.app_logo);

                item.setIsfav((obj.getString("liked").equalsIgnoreCase("true")) ? 1 : 0);
                item.setIsturned(0);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(list.size()>0 || "200".equals(result.get("status"))){
            Collections.sort(list,new TaskComparatorByDate());
            Collections.reverse(list);
            TaskAdapter taskAdapter = new TaskAdapter(list);

            recyclerView.setAdapter(taskAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }else{
            recyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Map<String, String>> loader) {

    }

}
