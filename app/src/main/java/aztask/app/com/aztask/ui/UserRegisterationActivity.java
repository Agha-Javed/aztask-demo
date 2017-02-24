package aztask.app.com.aztask.ui;


import org.json.JSONObject;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.net.UserRegisterationWorker;
import aztask.app.com.aztask.service.GCMRegistrationIntentService;
import aztask.app.com.aztask.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class UserRegisterationActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      requestWindowFeature(Window.FEATURE_NO_TITLE);  
      
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                  WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.user_registeration_activity);
      
      final Button submitButton=(Button)findViewById(R.id.submitId);
	  submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText nameText = (EditText) findViewById(R.id.nameId);
				EditText emailText = (EditText) findViewById(R.id.emailId);
				EditText mobileText = (EditText) findViewById(R.id.mobileId);
				EditText skillsText = (EditText) findViewById(R.id.skillId);

				User user = new User();
				user.setUserName(nameText.getText().toString());
				user.setUserEmail(emailText.getText().toString());
				user.setUserMobile(mobileText.getText().toString());
				user.setUserSkills(skillsText.getText().toString());
				
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setDeviceId(Util.getDeviceId());
				
				Location location=Util.getDeviceLocation();
				Log.i("UserRegisteration", "Location Object Ref:"+location);

				deviceInfo.setLatitude(""+location.getLatitude());
				deviceInfo.setLongitude(""+location.getLongitude());

				user.setDeviceInfo(deviceInfo);

				try {
					String response = new UserRegisterationWorker().execute(user).get();
					JSONObject responseObj = new JSONObject(response);
					int responseCode =(responseObj.getInt("id") > 0)? 200 : 400;
					if (responseCode == 200) {
						Log.i("UserRegisterationActivity", "The user has been created.");
						Intent itent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
						itent.putExtra("userId", responseObj.getInt("id"));
			            startService(itent);
			            
						Toast.makeText(MainActivity.getAppContext(), "Thanks for registeration.", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(intent);
						
						submitButton.setEnabled(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

}