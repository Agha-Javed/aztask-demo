package aztask.app.com.aztask.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.util.Util;

import android.os.AsyncTask;
import android.util.Log;

public class UserRegisterationWorker extends AsyncTask<User, Void, String> {

	private String CLASS_NAME = "RegisterationTask";

	@Override
	protected String doInBackground(User... params) {
		Log.i("RegisterationTask", "Registering User.");

		try {

			User user = params[0];

			String link = Util.SERVER_URL + "/user/register";

			String registerationRequest = prepareRegisterationRequest(user);

			if (!(registerationRequest != null) || !(registerationRequest.length() > 0)) {
				Log.i(CLASS_NAME, "The request is empty,so returning back.");
				return "";
			}

			Log.i(CLASS_NAME, "Request:" + registerationRequest);

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
			Log.e("RegisterationTask", "Error:" + exception);
		}

		return "";
	}

	@Override
	protected void onPostExecute(String result) {
		System.out.println("User Registeration Status:" + result);
		super.onPostExecute(result);
	}

	private String prepareRegisterationRequest(User user) {

		if (user != null) {
			JSONObject personInfo = new JSONObject();
			JSONObject deviceInfo = new JSONObject();

			try {
				personInfo.put("name", user.getUserName());
				personInfo.put("contact", user.getUserMobile());
				personInfo.put("email", user.getUserEmail());
				personInfo.put("skills", user.getUserSkills());
				personInfo.put("gcm_token", "");

				deviceInfo.put("deviceId", user.getDeviceInfo().getDeviceId());
				deviceInfo.put("latitude", user.getDeviceInfo().getLatitude());
				deviceInfo.put("longitude", user.getDeviceInfo().getLongitude());

				personInfo.put("deviceInfo", deviceInfo);

				return personInfo.toString();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

//	private String registerGCM() {
//		String token = null;
//		try {
//			new Thread(new Runnable() {
//			    public void run() {
//					Log.i("UserRegisterationWorker", "Registering GCM Token:");
//					InstanceID instanceID = InstanceID.getInstance(MainActivity.getAppContext());
//					token = instanceID.getToken(Util.PROJECT_NUMBER, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//					Log.i("UserRegisterationWorker", "Token:"+token);
//					return token;
//			}).start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
