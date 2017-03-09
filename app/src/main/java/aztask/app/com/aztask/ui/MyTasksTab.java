package aztask.app.com.aztask.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;

import aztask.app.com.aztask.ui.CreateTaskActivity;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.ui.UserRegisterationActivity;
import aztask.app.com.aztask.util.Util;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;
import static android.R.id.list;
import static aztask.app.com.aztask.R.id.fab;
import static java.lang.Integer.parseInt;

public class MyTasksTab extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private final String TAG="MyTasksTab";

    private ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
    Map<Integer, Integer> positions;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;


    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int MY_TASKS_LOADER_ID=30;

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


      //  taskAdapter = new TaskAdapter();
        //recyclerView.setAdapter(taskAdapter);

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
                        Log.i(TAG, "My Tasks Response:" + result.toString());
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
        List<TaskCard> list=new ArrayList<>();
        positions=new ConcurrentHashMap<Integer, Integer>();

        try {
            rootArray = new JSONArray(data);
            int len = rootArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = rootArray.getJSONObject(i);
                TaskCard item = new TaskCard();
                item.setTaskId(obj.getString("task_id"));
                item.setTaskDesc((obj.getString("task_desc")));
                item.setImageResourceId(R.drawable.great_wall_of_china);
                item.setIsfav(0);
                item.setIsturned(0);
                list.add(item);
                positions.put(parseInt(item.getTaskId()),i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(list.size()>0){
//            taskAdapter.setData(list);
            TaskAdapter taskAdapter=new TaskAdapter(list);
            recyclerView.setAdapter(taskAdapter);

            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);

            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.i(TAG,"Bundle Arguments:"+getArguments());
            if(getArguments()!=null && "true".equals(getArguments().getString("notification")) && getArguments().getString("task")!=null){
                int taskId=Integer.parseInt(getArguments().getString("task"));
                if(positions.containsKey(taskId)){
                    recyclerView.setLayoutManager(new CustomLinearLayoutManagerWithSmoothScroller(getContext()));
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(positions.get(taskId), 0);
                }
            }
        }else{
            recyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
        }

   }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
