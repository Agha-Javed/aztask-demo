package aztask.app.com.aztask.net;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import aztask.app.com.aztask.data.Task;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

import android.os.AsyncTask;
import android.util.Log;

public class CreateTaskWorker extends AsyncTask<Task, Void, String>{
	
	private String CLASS_NAME="CreateTaskWorker";
	
	@Override
	protected String doInBackground(Task... params) {
		Log.i("CreateTaskWorker", "Posting new task.");

		try {
			Task task = params[0];

			String link =Util.SERVER_URL+"/user/"+MainActivity.getUserId()+"/createTask";

			String registerationRequest = prepareCreateTaskRequest(task);

			Log.i("CreateTaskWorker","Create Task Request:"+registerationRequest);
			if (!(registerationRequest != null) || !(registerationRequest.length() > 0)) {
				Log.i(CLASS_NAME, "The request is empty,so returning back.");
				return "";
			}
			
			Log.i(CLASS_NAME, "Request:"+registerationRequest);

			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(registerationRequest);
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			} else {
				Log.i(CLASS_NAME, con.getResponseMessage());
			}
			return sb.toString();
		} catch (Exception exception) {
			Log.e("CreateTaskWorker", "Error:" + exception);
		}

		return "";
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
	
	private String prepareCreateTaskRequest(Task task) {

		if (task != null) {
			JSONObject taskInfo = new JSONObject();

			try {
				taskInfo.put("task_desc", task.getTaskDesc());
				taskInfo.put("task_categories", task.getTaskCategories());
				taskInfo.put("latitude", task.getDeviceInfo().getLatitude());
				taskInfo.put("longitude", task.getDeviceInfo().getLongitude());
				taskInfo.put("device_id", task.getDeviceInfo().getDeviceId());
				taskInfo.put("task_location", task.getTaskLocation());
				taskInfo.put("task_min_max_budget", task.getTask_min_max_budget());

				return taskInfo.toString();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return "";

	}
	
}
