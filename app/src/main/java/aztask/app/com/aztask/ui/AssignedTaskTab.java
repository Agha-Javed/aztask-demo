package aztask.app.com.aztask.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.util.Util;

public class AssignedTaskTab extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private final String TAG = "AssignedTaskTab";
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;
    private final int ASSIGNED_TASKS_LOADER_ID = 20;

    private Map<Integer,Integer> positions=new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "Creating View for Assinged Tasks Tab.");

        View view = inflater.inflate(R.layout.assgined_tasks_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.assigned_tasks_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (MainActivity.isUserRegistered()) {
                    intent = new Intent(getContext(), CreateTaskActivity.class);
                } else {
                    intent = new Intent(getContext(), UserRegisterationActivity.class);
                }
                startActivity(intent);
            }
        });


        mRecyclerView = (RecyclerView) view.findViewById(R.id.assigned_tasks_cardView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);




        mTaskAdapter = new TaskAdapter();
        mRecyclerView.setAdapter(mTaskAdapter);

        getLoaderManager().initLoader(ASSIGNED_TASKS_LOADER_ID, null, this).forceLoad();

        return view;

    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String>(getContext()) {

            @Override
            public String loadInBackground() {
                Log.i(TAG, "Downloading assigned tasks.");
                StringBuilder result = new StringBuilder("");

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
                            result.append(line).append("\n");
                        }
                        br.close();
                        Log.i(TAG, "Assigned Tasks List:" + result.toString());
                    }
                    return result.toString();
                } catch (Exception exception) {
                    Log.e(TAG, "Error:" + exception);
                    exception.printStackTrace();
                }

                return result.toString();
            }

        };

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {

        try {
            JSONArray rootArray = new JSONArray(data);
            List<TaskCard> list = new ArrayList<>();
            int len = rootArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = rootArray.getJSONObject(i);
                TaskCard item = new TaskCard();
                item.setTaskId(obj.getString("task_id"));
                item.setTaskDesc((obj.getString("task_desc")));
                item.setImageResourceId(R.drawable.great_wall_of_china);
                item.setIsfav((obj.getString("liked").equalsIgnoreCase("true")) ? 1 :0);
                item.setIsturned(0);
                list.add(item);
                positions.put(i,Integer.parseInt(item.getTaskId()));
            }
            mTaskAdapter.setData(list);
            mRecyclerView.setVisibility(View.VISIBLE);

            if(getArguments()!=null && "true".equals(getArguments().getString("notification")) && getArguments().getInt("task")>0 && positions.containsKey(getArguments().getInt("task"))){
                Log.i(TAG,"Scrollogin to particular position, registration");
             //   mRecyclerView.scrollToPosition(getArguments().getInt("task"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }

}