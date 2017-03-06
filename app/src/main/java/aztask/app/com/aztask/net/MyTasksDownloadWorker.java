package aztask.app.com.aztask.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import aztask.app.com.aztask.R;
import aztask.app.com.aztask.ui.TaskAdapter;
import aztask.app.com.aztask.data.TaskCard;
import aztask.app.com.aztask.ui.MainActivity;
import aztask.app.com.aztask.util.Util;

import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MyTasksDownloadWorker extends AsyncTask<Void, Void, String>{

	private String CLASS_NAME = "TasksDownloader";
	List<TaskCard> tasksList = new ArrayList<TaskCard>();

	RecyclerView MyRecyclerView;
	AppCompatActivity mainActivity;

    Fragment fragmentRef;

	private Map<Integer,Integer> positions=new HashMap<>();

	public MyTasksDownloadWorker(AppCompatActivity mainActivity, RecyclerView MyRecyclerView) {
		this.MyRecyclerView = MyRecyclerView;
		this.mainActivity = mainActivity;
	}

    public MyTasksDownloadWorker(AppCompatActivity mainActivity, Fragment fragment,RecyclerView MyRecyclerView) {
        this.MyRecyclerView = MyRecyclerView;
        this.mainActivity = mainActivity;
        this.fragmentRef=fragment;
    }

	@Override
	protected String doInBackground(Void... params) {
		Log.i("MyTasksDownloadWorker", "Downloading my tasks.");
		String link = Util.SERVER_URL + "/user/"+MainActivity.getUserId()+"/tasks";
		StringBuilder result = new StringBuilder("");

		try {
			Log.d(CLASS_NAME, "Link:"+link);

			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setRequestProperty("Accept", "application/json");

			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					result.append(line + "\n");
				}
				br.close();
				Log.i(CLASS_NAME, "User Registered Response:"+result.toString());
			}
		} catch (Exception exception) {
			Log.e("CreateTaskWorker", "Error:" + exception);
			exception.printStackTrace();
		}

		return result.toString();
	}

	@Override
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);

		Log.i("Test", "Got The response::" + result);

		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				JSONArray rootArray;
				try {
					rootArray = new JSONArray(result);
					int len = rootArray.length();
					for (int i = 0; i < len; i++) {
						JSONObject obj = rootArray.getJSONObject(i);
						TaskCard item = new TaskCard();
						item.setTaskId(obj.getString("task_id"));
						item.setTaskDesc((obj.getString("task_desc")));
						item.setImageResourceId(R.drawable.great_wall_of_china);
						item.setIsfav(0);
						item.setIsturned(0);
						tasksList.add(item);

						positions.put(i,Integer.parseInt(item.getTaskId()));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}


				if (tasksList.size() > 0 & MyRecyclerView != null) {
					MyRecyclerView.setAdapter(new TaskAdapter(tasksList));
				}

      /*          LinearLayoutManager MyLayoutManager = new LinearLayoutManager(MainActivity.getAppContext()){
					{

						@Override
						public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
						LinearSmoothScroller smoothScroller = new LinearSmoothScroller(this) {

							private static final float SPEED = 300f;// Change this value (default=25f)

							@Override
							protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
								return SPEED / displayMetrics.densityDpi;
							}

						};
						smoothScroller.setTargetPosition(position);
						startSmoothScroll(smoothScroller);
					}

					};
				};
*/



//                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);



  //              MyRecyclerView.setLayoutManager(layoutManager);
             //   MyRecyclerView.getLayoutManager().smoothScrollToPosition(MyRecyclerView,null,5);
    //            layoutManager.smoothScrollToPosition(MyRecyclerView,null,5);

                 MyRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(MainActivity.getAppContext()));
             //    MyRecyclerView.smoothScrollToPosition(5);
                ((LinearLayoutManager) MyRecyclerView.getLayoutManager()).scrollToPositionWithOffset(5, 0);

                //MyRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(MainActivity.getAppContext()));
                //MyRecyclerView.smoothScrollToPosition(5);
             //   MyRecyclerView.smoothScrollToPosition(5);
              /*  if(fragmentRef.getArguments()!=null && "true".equals(fragmentRef.getArguments().getString("notification")) && fragmentRef.getArguments().getInt("task")>0 && positions.containsKey(fragmentRef.getArguments().getInt("task"))){
                    Log.i(TAG,"Scrollogin to particular position, registration");
                    MyRecyclerView.scrollToPosition(positions.get(fragmentRef.getArguments().getInt("task")));
                }*/
				//
			}
		});
	}


    public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

        public LinearLayoutManagerWithSmoothScroller(Context context) {
            super(context, VERTICAL, false);
        }

        public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller {
            public TopSnappedSmoothScroller(Context context) {
                super(context);

            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return LinearLayoutManagerWithSmoothScroller.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        }
    }
}
