package aztask.app.com.aztask.ui;

import org.json.JSONObject;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.Task;
import aztask.app.com.aztask.net.CreateTaskWorker;
import aztask.app.com.aztask.util.Util;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements TaskCategoryDialogFragment.NoticeDialogListener {

    private EditText taskDesc;
    private EditText taskCategory;
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
        taskCategory = (EditText) findViewById(R.id.categoryId);

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

        taskComments = (EditText) findViewById(R.id.commentsId);

        Button submitButton = (Button) findViewById(R.id.submitId);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Task task = new Task();

                task.setTaskDesc(taskDesc.getText().toString());
                task.setTaskCategories(taskCategory.getText().toString());
                task.setTaskComments(taskComments.getText().toString());


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
        taskCategory.setText(selectedOptions);
    }

    @Override
    public void onDialogNegativeClick() {
        Log.i("CreateTaskActivity", "User pressed cancelled:");
    }
}