package aztask.app.com.aztask.net;

import android.app.ProgressDialog;
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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import aztask.app.com.aztask.data.AZTaskContract;
import aztask.app.com.aztask.data.AZTaskDbHelper;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

public class NearbyTasksDownloader extends AsyncTask<String, Void, String> {

	List<TaskCard> tasksList = new ArrayList<TaskCard>();
	private String CLASS_NAME = "TasksDownloader";
	private Context context;
	private ProgressDialog dialog;

	public NearbyTasksDownloader(Context context,boolean showDialog) {
		this.context=context;
		if(showDialog){
			dialog = new ProgressDialog(context);
		}
	}

	protected void onPreExecute() {
		if(dialog!=null){
			this.dialog.setMessage("please wait..");
			this.dialog.show();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Log.i("NearbyTasksDownloader", "Downloading nearby tasks.");
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

		Log.i(CLASS_NAME,"NearByTasks Downloader Result::"+result);
		if(result!=null && result.trim().length()>0){


			JSONArray rootArray = null;
			Map<String,Integer> taskListMap=null;
			Integer POSITIVE_VALUE=new Integer(1);
			try {
				rootArray = new JSONArray(result);
				int len = rootArray.length();

				if(!(len>0)){
					Log.i(CLASS_NAME,"Empty Result, returning back.");
					return;
				}

				taskListMap=new ConcurrentHashMap<>();
				context.getContentResolver().delete(AZTaskContract.NEARBY_TASKS_CONTENT_URI, null, null);

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
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_ID,taskId);
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_DESC,obj.getString("task_desc"));
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_CATEGORY,obj.getString("task_categories"));
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_TIME,obj.getString("task_time"));
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_LOCATION,obj.getString("task_location"));
					contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_MIN_MAX_BUDGET,obj.getString("task_min_max_budget"));
                    contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_OWNER_CONTACT,obj.getString("contact"));
                    contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_OWNER_NAME,obj.getString("user"));
                    contentValues.put(AZTaskContract.NearByTaskEntry.COLUMN_NAME_TASK_LIKED,obj.getString("liked"));
					context.getContentResolver().insert(AZTaskContract.NEARBY_TASKS_CONTENT_URI, contentValues);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (dialog!=null && dialog.isShowing()) {
			dialog.dismiss();
		}

	}

}
