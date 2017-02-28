package aztask.app.com.aztask.ui;

import org.json.JSONObject;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.Task;
import aztask.app.com.aztask.net.CreateTaskWorker;
import aztask.app.com.aztask.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateTaskActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_task_activity);

		Toolbar toolbar = (Toolbar) findViewById(R.id.createTaskToolbar);
		setSupportActionBar(toolbar);

        setTitle("New Task");

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().setDisplayShowTitleEnabled(false);




        Button submitButton = (Button) findViewById(R.id.submitId);
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText taskDesc = (EditText) findViewById(R.id.taskDescId);
				Spinner  taskCategory =(Spinner) findViewById(R.id.categoryId);
				EditText taskComments = (EditText) findViewById(R.id.commentsId);

				Task task = new Task();
				task.setTaskDesc(taskDesc.getText().toString());

				task.setTaskCategories(taskCategory.getSelectedItem().toString());
				task.setTaskComments(taskComments.getText().toString());

				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setDeviceId(Util.getDeviceId());

				Location location=Util.getDeviceLocation();
				deviceInfo.setLatitude(""+location.getLatitude());
				deviceInfo.setLongitude(""+location.getLongitude());
				deviceInfo.setDeviceId(Util.getDeviceId());

				task.setDeviceInfo(deviceInfo);

				Log.i("MainActivity", "Task Object:" + task);
				try {
					String response=new CreateTaskWorker().execute(task).get();
					JSONObject responseObj = new JSONObject(response);
					int responseCode=(responseObj.getString("code")!=null && responseObj.getString("code").length()>0) ? Integer.parseInt(responseObj.getString("code")) : 400;
					if(responseCode==200){
						Log.i("Create Task Activity", "Task has been created:");
						Toast.makeText(getApplicationContext(),"Task has been created.",
								Toast.LENGTH_SHORT).show();

						Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		                startActivity(intent);

					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});

	}

}