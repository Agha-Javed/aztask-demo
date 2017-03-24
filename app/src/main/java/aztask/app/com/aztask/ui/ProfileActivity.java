package aztask.app.com.aztask.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.DeviceInfo;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.util.Util;

import static android.R.attr.name;
import static java.security.AccessController.getContext;


public class ProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private static final String TAG = "ProfileActivity";
    private static final int USER_PROFILE_LOADER_ID=444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        View photoHeader =findViewById(R.id.photoHeader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Button btnCall = (Button) findViewById(R.id.btnCall);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }
        });

        Button messageButton = (Button) findViewById(R.id.btnMessage);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView phoneNumber =(TextView)findViewById(R.id.tvNumber);
                TextView userName  =(TextView) findViewById(R.id.tvName);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi "+userName.getText()+",");
                sendIntent.putExtra("address",phoneNumber.getText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoHeader.setTranslationZ(100);
            photoHeader.invalidate();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Bundle requestData = new Bundle();
        String userId = getIntent().getExtras().getString("userId");

        requestData.putString("userId", userId);
        getSupportLoaderManager().initLoader(USER_PROFILE_LOADER_ID, requestData, this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(getApplicationContext()) {

            @Override
            public String loadInBackground() {
                Log.i("TasksDownloaderWorker", "Requesting User Profile.");

                StringBuilder result = new StringBuilder("");
                try {
                    String userId = args.getString("userId");
                    Log.i(TAG, "User Id:" + userId);

                    String link = Util.SERVER_URL + "/user/"+userId;
                    Log.d(TAG, "Link:" + link);

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
                        Log.i(TAG, "User Profile in JSON:" + sb.toString());
                        return sb.toString();
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.e(TAG, exception.getMessage());
                }

                return result.toString();
            }


        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        JSONObject responseObj = null;
        try {
            responseObj = new JSONObject(data.toString());
            int responseCode = (responseObj.getInt("id") > 0) ? 200 : 400;
            if (responseCode == 200) {
                TextView userName  =(TextView)findViewById(R.id.tvName);
                TextView phoneNumber =(TextView)findViewById(R.id.tvNumber);
                TextView skills =(TextView) findViewById(R.id.skills);

                userName.setText(responseObj.getString("name"));
                phoneNumber.setText(responseObj.getString("contact"));
                skills.setText(responseObj.getString("skills"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}

