package aztask.app.com.aztask.ui;


import org.json.JSONException;
import org.json.JSONObject;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.service.GCMRegistrationIntentService;
import aztask.app.com.aztask.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class UserRegisterationActivity extends AppCompatActivity implements TaskCategoryDialogFragment.NoticeDialogListener {

    private EditText nameText;
   // private EditText emailText;
    private EditText mobileText;
    private EditText skillsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registeration_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registerUser);
        setSupportActionBar(toolbar);

        setTitle("Register");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        nameText = (EditText) findViewById(R.id.nameId);
        //emailText = (EditText) findViewById(R.id.emailId);
        mobileText = (EditText) findViewById(R.id.mobileId);
        skillsText = (EditText) findViewById(R.id.skillId);

        skillsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCategoryDialog();
                }
            }
        });
        skillsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });


        final Button submitButton = (Button) findViewById(R.id.submitId);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                User user = new User();

                String userName=nameText.getText().toString();
                if(userName!=null && userName.trim().toString().length()>0){
                    user.setUserName(userName);
                    user.setUserEmail("dummy_email@aztask.com");
                }else{
                    nameText.setError("username can't be empty.");
                    return;
                }

                String phoneNum=mobileText.getText().toString();
                if(phoneNum!=null && phoneNum.length()>0){
                    user.setUserMobile(phoneNum);
                }else{
                    mobileText.setError("mobile number can't be empty.");
                    return;
                }

                String skills=skillsText.getText().toString();
                if(skills!=null && skills.length()>0){
                    user.setUserSkills(skills);
                }else{
                    skillsText.setError("skills can't be empty.");
                    return;
                }

                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceId(Util.getDeviceId());

                Location location = Util.getDeviceLocation();

                deviceInfo.setLatitude("" + location.getLatitude());
                deviceInfo.setLongitude("" + location.getLongitude());

                user.setDeviceInfo(deviceInfo);
                new UserRegisterationWorker().execute(user);
            }
        });

    }

    private void showCategoryDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TaskCategoryDialogFragment editNameDialogFragment = new TaskCategoryDialogFragment();
        editNameDialogFragment.show(fm, "Choose Skills Category");
    }

    @Override
    public void onDialogPositiveClick(String selectedOptions) {
        if(selectedOptions!=null && selectedOptions.trim().length()>0){
            skillsText.setText(selectedOptions.substring(0,selectedOptions.length()-1));
        }
    }

    @Override
    public void onDialogNegativeClick() {
        Log.i("RegisterationActivity","User has cancelled the dialog.");
    }

    class UserRegisterationWorker extends AsyncTask<User, Void, String> {

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
            System.out.println("Registeration result:" + result);
            try {
                JSONObject responseObj = new JSONObject(result);
                int responseCode = (responseObj.getInt("id") > 0) ? 200 : 400;
                if (responseCode == 200) {
                    Log.i("RegisterationActivity", "The user has been created.");
                    Intent itent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
                    itent.putExtra("userId", responseObj.getInt("id"));
                    startService(itent);

                    Toast.makeText(MainActivity.getAppContext(), "Yay!!,You are registered.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.getAppContext(), responseObj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
    }
}