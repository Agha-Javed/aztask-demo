package aztask.app.com.aztask.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import aztask.app.com.aztask.R;
import aztask.app.com.aztask.ui.TaskAdapter;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class TasksDownloaderWorker extends AsyncTask<String, Void, String> {

	private String CLASS_NAME = "TasksDownloader";
	List<TaskCard> tasksList = new ArrayList<TaskCard>();

	RecyclerView MyRecyclerView;
	AppCompatActivity mainActivity;

	public TasksDownloaderWorker(AppCompatActivity mainActivity, RecyclerView MyRecyclerView) {
		this.MyRecyclerView = MyRecyclerView;
		this.mainActivity = mainActivity;
	}

	@Override
	protected String doInBackground(String... params) {
		Log.i("TasksDownloaderWorker", "Downloading nearby tasks.");
		String link ="";
        if(MainActivity.isUserRegistered())
            link=Util.SERVER_URL + "/tasks/list/nearby";
        else
            link=Util.SERVER_URL + "/tasks/list/new";

		StringBuilder result = new StringBuilder("");

		try {
			String downloadNearbyTasksRequest = params[0];// prepareDownloadNearByTasksRequest();//taskInfo.toString();

			if (!(downloadNearbyTasksRequest != null) || !(downloadNearbyTasksRequest.length() > 0)) {
				Log.i(CLASS_NAME, "The request is empty,so returning back.");
			}

			Log.i(CLASS_NAME, "Request:" + downloadNearbyTasksRequest);

			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setRequestProperty("Accept", "application/json");

            if (MainActivity.isUserRegistered()){
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(downloadNearbyTasksRequest);
                wr.flush();

            }


			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					result.append(line + "\n");
				}
				br.close();

				return result.toString();
			} else {
				Log.i(CLASS_NAME, con.getResponseMessage());
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
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				JSONArray rootArray;
				try {
					rootArray = new JSONArray(result);
					int len = rootArray.length();
					for (int i = 0; i < len; i++) {
						JSONObject obj = rootArray.getJSONObject(i);
						TaskCard item = new TaskCard();
						item.setTaskId(obj.getString("task_id"));
						item.setTaskDesc((obj.getString("task_desc")));
						item.setImageResourceId(R.drawable.great_wall_of_china);
						item.setIsfav((obj.getString("liked").equalsIgnoreCase("true")) ? 1 :0);
						item.setIsturned(0);
						tasksList.add(item);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				LinearLayoutManager MyLayoutManager = new LinearLayoutManager(MainActivity.getAppContext());
				MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

				if (tasksList.size() > 0 & MyRecyclerView != null) {
					MyRecyclerView.setAdapter(new TaskAdapter(tasksList));
				}
				MyRecyclerView.setLayoutManager(MyLayoutManager);
			}
		});
	}
}
