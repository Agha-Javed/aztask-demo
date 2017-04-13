package aztask.app.com.aztask.ui;

import org.json.JSONObject;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.Task;
import aztask.app.com.aztask.net.CreateTaskWorker;
import aztask.app.com.aztask.util.Util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static aztask.app.com.aztask.R.id.minBudget;

public class CreateTaskActivity extends AppCompatActivity implements TaskCategoryDialogFragment.NoticeDialogListener {

    private EditText taskDesc;
    private EditText taskCategory;
    private EditText taskLoocation;
    private EditText taskMinBudget;
    private EditText taskMaxBudget;


    private EditText taskComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.createTaskToolbar);
        setSupportActionBar(toolbar);

        setTitle("New Task");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        taskDesc = (EditText) findViewById(R.id.taskDescId);
        taskDesc.getBackground().setColorFilter(Color.rgb(63,81,181),PorterDuff.Mode.SRC_ATOP); // change the drawable color

        View photoHeader =findViewById(R.id.photoHeaderTask);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoHeader.setTranslationZ(100);
            photoHeader.invalidate();
        }

/*
        if(Build.VERSION.SDK_INT > 16) {
            taskDesc.setBackground(drawable); // set the new drawable to EditText
        }else{
            taskDesc.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
*/

        taskCategory = (EditText) findViewById(R.id.categoryId);
        taskCategory.getBackground().setColorFilter(Color.rgb(63,81,181),PorterDuff.Mode.SRC_ATOP); // change the drawable color

        taskCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCategoryDialog();
                }
            }
        });
        taskCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });


        taskLoocation = (EditText) findViewById(R.id.loc);
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        taskLoocation.getBackground().setColorFilter(Color.rgb(63,81,181),PorterDuff.Mode.SRC_ATOP);
        taskLoocation.setOnFocusChangeListener(new View.OnFocusChangeListener()  {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.scrollView1);
                    Snackbar.make(coordinatorLayout,"Clicked.",Snackbar.LENGTH_SHORT).show();
                    List<Address> addresses = null;
                    try {
                        String latLong=sharedPreferences.getString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION,"");//putString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, deviceLocation).apply();
                        if(latLong!=null && latLong.length()>0){
                            String[] latLongArray=latLong.split(":");
                            addresses = geocoder.getFromLocation(Double.parseDouble(latLongArray[0]), Double.parseDouble(latLongArray[1]), 1);
                            if(addresses.size()>0){
                                String address=addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getAddressLine(1);
                                taskLoocation.setText(address);
                            }
                        }
                          // change the drawable color
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        taskMinBudget = (EditText) findViewById(minBudget);
        taskMinBudget.getBackground().setColorFilter(Color.rgb(63,81,181),PorterDuff.Mode.SRC_ATOP); // change the drawable color

        taskMaxBudget = (EditText) findViewById(R.id.maxBudget);
        taskMaxBudget.getBackground().setColorFilter(Color.rgb(63,81,181),PorterDuff.Mode.SRC_ATOP); // change the drawable color


    //    taskComments = (EditText) findViewById(R.id.commentsId);

        Button submitButton = (Button) findViewById(R.id.submitId);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Task task = new Task();

                String taskDescription=taskDesc.getText().toString();
                if(taskDescription!=null && taskDescription.trim().length()>0){
                    task.setTaskDesc(taskDescription);
                }else{
                    taskDesc.setError("task can't be empty.");
                    return;
                }

                String taskCategories=taskCategory.getText().toString();
                if(taskCategories!=null && taskCategories.trim().length()>0){
                    task.setTaskCategories(taskCategories);
                }else{
                    taskCategory.setError("task category can't be empty.");
                    return;
                }

                String taskLocation=taskLoocation.getText().toString();
                if(taskLocation!=null && taskLocation.trim().length()>0){
                    task.setTaskLocation(taskLoocation.getText().toString());
                }else{
                    taskLoocation.setError("task location can't be empty.");
                    return;
                }

                String minBudget=(taskMinBudget.getText()!=null) ? taskMinBudget.getText().toString():"0";

                String maxBudget=(taskMaxBudget.getText()!=null) ? taskMaxBudget.getText().toString():"0";

                String taskBudget=((minBudget!=null && minBudget.length()>0) ? minBudget :"0")+":"+((maxBudget!=null && maxBudget.length()>0) ? maxBudget :"0");
                task.setTask_min_max_budget(taskBudget);


                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceId(Util.getDeviceId());

                Location location = Util.getDeviceLocation();
                deviceInfo.setLatitude("" + location.getLatitude());
                deviceInfo.setLongitude("" + location.getLongitude());
                deviceInfo.setDeviceId(Util.getDeviceId());

                task.setDeviceInfo(deviceInfo);

                Log.i("MainActivity", "Task Object:" + task);
                try {
                    String response = new CreateTaskWorker().execute(task).get();
                    JSONObject responseObj = new JSONObject(response);
                    int responseCode = (responseObj.getString("code") != null && responseObj.getString("code").length() > 0) ? Integer.parseInt(responseObj.getString("code")) : 400;
                    if (responseCode == 200) {
                        Log.i("Create Task Activity", "Task has been created:");
                        Toast.makeText(getApplicationContext(), "Task has been created.",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showCategoryDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TaskCategoryDialogFragment editNameDialogFragment = new TaskCategoryDialogFragment();//.newInstance("Some Title");
        editNameDialogFragment.show(fm, "Choose Category");
    }

    @Override
    public void onDialogPositiveClick(String selectedOptions) {
        if(selectedOptions!=null && selectedOptions.trim().length()>0){
            taskCategory.setText(selectedOptions.substring(0,selectedOptions.length()-1));
        }
    }

    @Override
    public void onDialogNegativeClick() {
        Log.i("CreateTaskActivity", "User pressed cancelled:");
    }
}