package aztask.app.com.aztask;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.net.TasksDownloaderWorker;
import aztask.app.com.aztask.ui.CreateTaskActivity;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.ui.UserRegisterationActivity;
import aztask.app.com.aztask.util.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NearByTasksTab extends Fragment{

	ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
	RecyclerView MyRecyclerView;
	AppCompatActivity mainActivity;

	public NearByTasksTab(){}
	public NearByTasksTab(AppCompatActivity mainActivity){
	 this.mainActivity=mainActivity;	
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().setTitle("Tasks");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.nearbytasks_fragment, container, false);
		MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
		MyRecyclerView.setHasFixedSize(true);
		getTasksList();

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(Util.PREF_KEY_DEVICEID, Context.MODE_PRIVATE);
				String deviceId=sharedPreferences.getString(Util.PREF_KEY_DEVICEID,null);

				Log.i("NearByTasksTab","Device Id:"+deviceId);

				Intent intent=null;
				if (deviceId!=null && deviceId.length()>0) {
					intent = new Intent(getContext(), CreateTaskActivity.class);
				} else {
					intent = new Intent(getContext(), UserRegisterationActivity.class);
				}
				startActivity(intent);
			}
		});
		return view;
	}
	
	private void getTasksList(){
		try {
			final JSONObject taskInfo = new JSONObject();
			Location location=MainActivity.getCurrentUserLocation();
			try {
				if(location==null){
					taskInfo.put("latitude",""+ MainActivity.getRegisteredUser().getDeviceInfo().getLatitude());
					taskInfo.put("longitude",""+ MainActivity.getRegisteredUser().getDeviceInfo().getLongitude());
					
				}else{
					taskInfo.put("latitude",""+ location.getLatitude());
					taskInfo.put("longitude",""+ location.getLongitude());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			taskInfo.put("userId", MainActivity.getUserId());
			new TasksDownloaderWorker(mainActivity,MyRecyclerView).execute(taskInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
