package aztask.app.com.aztask.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.util.Util;
import android.os.AsyncTask;
import android.util.Log;

public class CheckUserRegisterationWorker extends AsyncTask<String, Void, User> {

	private String CLASS_NAME="CheckUserRegisteration";
	
	@Override
	protected User doInBackground(String... params) {
		Log.i(CLASS_NAME, "Checking User Registeration.");
		
		try{
			String deviceId=params[0];
			Log.i(CLASS_NAME, "Device Id:"+deviceId);
			
			String link =Util.SERVER_URL+"/user/is_registered/"+deviceId;
			Log.d(CLASS_NAME, "Link:"+link);

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
				Log.i(CLASS_NAME, "User Registered Response:"+sb.toString());
			}
			
			JSONObject responseObj = new JSONObject(sb.toString());
			int responseCode=(responseObj.getInt("id")>0) ? 200 : 400;
			User registerdUser=null;
			DeviceInfo deviceInfo=null;
			if(responseCode==200){
				registerdUser=new User();
				registerdUser.setUserId(responseObj.getInt("id"));
				registerdUser.setUserName(responseObj.getString("name"));
				registerdUser.setUserMobile(responseObj.getString("contact"));
				registerdUser.setUserEmail(responseObj.getString("email"));
				registerdUser.setUserSkills(responseObj.getString("skills"));
				
				deviceInfo=new DeviceInfo();
				
				JSONObject deviceInfoJson=responseObj.getJSONObject("deviceInfo");
				deviceInfo.setDeviceId(deviceInfoJson.getString("deviceId"));
				deviceInfo.setLatitude(deviceInfoJson.getString("latitude"));
				deviceInfo.setLongitude(deviceInfoJson.getString("longitude"));
				
				registerdUser.setDeviceInfo(deviceInfo);
			}
			return registerdUser;
		}catch(Exception exception){
			exception.printStackTrace();
			Log.e(CLASS_NAME, exception.getMessage());
		}
		return null;
	}
	
}
