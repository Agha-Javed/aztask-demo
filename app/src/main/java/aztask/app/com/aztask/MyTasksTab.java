package aztask.app.com.aztask;

import java.util.ArrayList;

import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.net.MyTasksDownloadWorker;
import aztask.app.com.aztask.ui.CreateTaskActivity;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.ui.UserRegisterationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyTasksTab extends Fragment{

	ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
	RecyclerView MyRecyclerView;
	AppCompatActivity mainActivity;

    public MyTasksTab(){}

    public MyTasksTab(AppCompatActivity mainActivity){
	 this.mainActivity=mainActivity;	
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.mytasks_fragment, container, false);
		MyRecyclerView = (RecyclerView) view.findViewById(R.id.mytasks_cardView);
		MyRecyclerView.setHasFixedSize(true);
		if(MainActivity.getUserId()>0)
        getTasksList();

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.mytasks_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=null;
				if (MainActivity.isUserRegistered()) {
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
//			final JSONObject taskInfo = new JSONObject();
//			Location location=Util.getDeviceLocation();
//			try {
//				if(location==null){
//					taskInfo.put("latitude",""+ MainActivity.getRegisteredUser().getDeviceInfo().getLatitude());
//					taskInfo.put("longitude",""+ MainActivity.getRegisteredUser().getDeviceInfo().getLongitude());
//				}else{
//					taskInfo.put("latitude",""+ location.getLatitude());
//					taskInfo.put("longitude",""+ location.getLongitude());
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
			new MyTasksDownloadWorker(mainActivity,MyRecyclerView).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
