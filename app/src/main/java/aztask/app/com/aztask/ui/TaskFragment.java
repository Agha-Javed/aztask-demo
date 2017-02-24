package aztask.app.com.aztask.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.net.TasksDownloaderWorker;
import aztask.app.com.aztask.util.Util;

public class TaskFragment extends Fragment {

	ArrayList<TaskCard> tasksList = new ArrayList<TaskCard>();
	RecyclerView MyRecyclerView;
	AppCompatActivity mainActivity;

	public TaskFragment(AppCompatActivity mainActivity){
	 this.mainActivity=mainActivity;	
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().setTitle("Tasks");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_card, container, false);
		MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
		MyRecyclerView.setHasFixedSize(true);
		getTasksList();

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=null;
				if (MainActivity.userRegistered()) {
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
			Location location=Util.getDeviceLocation();
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

			new TasksDownloaderWorker(mainActivity,MyRecyclerView).execute(taskInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
