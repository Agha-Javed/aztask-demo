package aztask.app.com.aztask.ui;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.net.CheckUserRegisterationWorker;
import aztask.app.com.aztask.service.GCMRegistrationIntentService;
import aztask.app.com.aztask.util.Util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // FINAL STATIC VARIABLES
    private static final int FINE_LOCATION_PERMISSION_CONSTANT = 100;
    private static final int FINE_LOCATION_ENABLED_CONSTANT = 101;
    private static final int READ_PHONE_STATE_PERMISSION_CONSTANT = 200;

    // STATIC VARIABLES
    private static String TAG = "MainActivity";
    //TODO I HAVE TO REMOVE THIS STATIC CONTEXT
    private static Context appContext;
    private static User loggedInUser;
    private static Location currentUserLocation;

    //INSTANCE VARIABLES
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Bundle bundle;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);


        tabLayout.addTab(tabLayout.newTab().setText("NearBy Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("Assigned Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("My Tasks"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i(TAG, "Setting Default Nearby Tasks.");

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final TabsAdapter adapter = new TabsAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(Util.NEARBY_TASKS_TAB_POSITION);

        Log.i(TAG, "Bundle:" + bundle);
        Log.i(TAG, "Extras from Pending Intent:" + getIntent().getExtras());

        if (getIntent().getExtras() != null) {
            //  Bundle b = getIntent().getExtras();
            this.bundle = new Bundle();
            Log.i(TAG, "notification from Pending Intent:" + getIntent().getExtras().getBoolean("notification"));
            Log.i(TAG, "notification_type from Pending Intent:" + getIntent().getExtras().getString("notification_type"));
            if (getIntent().getExtras().getBoolean("notification")) {
                bundle.putString("notification", "true");
                if ("assigned".equals(getIntent().getExtras().getString("notification_type"))) {
                    bundle.putString("notification_type", "assigned");
                    Log.i(TAG, "Setting Assigned Tasks Tab");
                    viewPager.setCurrentItem(Util.ASSIGNED_TASKS_TAB_POSITION);
                } else if ("liked".equals(getIntent().getExtras().getString("notification_type"))) {
                    bundle.putString("notification_type", "liked");
                    Log.i(TAG, "Setting My Tasks tab");
                    viewPager.setCurrentItem(Util.MY_TASKS_TAB_POSITION);
                }
                bundle.putString("task", getIntent().getExtras().getString("task"));
                adapter.setArgumentsDataForFragments(bundle);
            }

        }

/*
        if (bundle != null && "true".equals(bundle.getString("notification"))) {
            Log.i(TAG, "Bundle:" + bundle + " and Notification: " + bundle.getString("notification"));
            if (bundle != null && "true".equals(bundle.getString("notification"))) {
                Log.i(TAG, "Bundle:" + bundle + " and Notification Type: " + bundle.getString("notification_type"));
                if ("assigned".equals(bundle.getString("notification_type"))) {
                    Log.i(TAG, "Setting Assigned Tasks Tab");
                    viewPager.setCurrentItem(Util.ASSIGNED_TASKS_TAB_POSITION);
                } else if (bundle != null && "liked".equals(bundle.getString("notification_type"))) {
                    viewPager.setCurrentItem(Util.MY_TASKS_TAB_POSITION);
                    Log.i(TAG, "Setting My Tasks tab");
                }
                adapter.setArgumentsDataForFragments(bundle);
            } else {
                viewPager.setCurrentItem(Util.NEARBY_TASKS_TAB_POSITION);
                Log.i(TAG, "Setting Default Nearby Tasks.");

            }
        }
*/

        loadUser();



        if (checkLocationPermission()) {
            Log.i(TAG, "Phone has location permission");
            setupGoogleApiClient();
            //    proceedWithPermission();
        } else {
            Log.i(TAG, "Phone doesn't has location permission,so will return general tasks.");
            //  proceedWithoutPermission();
        }

    }

    private void setupGoogleApiClient() {
        Log.i(TAG, "Setting up google api client.");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
            Log.i(TAG, "connecting google api client.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient != null) {
            Log.i(TAG, "Disconnecting google api client.");
            googleApiClient.disconnect();
        }
    }


    private void proceedWithPermission() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("NearBy Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("Assigned Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("My Tasks"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsAdapter adapter = new TabsAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (bundle != null && "true".equals(bundle.getString("notification"))) {
            Log.i(TAG, "Bundle:" + bundle + " and Notification: " + bundle.getString("notification"));
            if (bundle != null && "true".equals(bundle.getString("notification"))) {
                Log.i(TAG, "Bundle:" + bundle + " and Notification Type: " + bundle.getString("notification_type"));
                if ("assigned".equals(bundle.getString("notification_type"))) {
                    Log.i(TAG, "Setting Assigned Tasks Tab");
                    viewPager.setCurrentItem(Util.ASSIGNED_TASKS_TAB_POSITION);
                } else if (bundle != null && "liked".equals(bundle.getString("notification_type"))) {
                    viewPager.setCurrentItem(Util.MY_TASKS_TAB_POSITION);
                    Log.i(TAG, "Setting My Tasks tab");
                }
                adapter.setArgumentsDataForFragments(bundle);
            } else {
                viewPager.setCurrentItem(Util.NEARBY_TASKS_TAB_POSITION);
                Log.i(TAG, "Setting Default Nearby Tasks.");

            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i(TAG, "Setting Default Nearby Tasks.");

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private User getUserByDeviceId(String deviceId) {
        try {
            User user = new CheckUserRegisterationWorker().execute(deviceId).get();
            Log.i(TAG, "User is registered:" + ((user != null) ? user.getDeviceInfo().getDeviceId() : "No"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static int getUserId() {
        return (loggedInUser != null) ? loggedInUser.getUserId() : 0;
    }

    public static boolean isUserRegistered() {
        Log.i(TAG, "isUserRegistered::" + ((loggedInUser != null) ? true : false));
        return (loggedInUser != null) ? true : false;
    }

    public static User getRegisteredUser() {
        return loggedInUser;
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "OnConnected Method");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended Method");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed Method");

    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentUserLocation = location;
        Log.i(TAG, "Location Changed::Latitude:" + location.getLatitude() + " and Longitude:" + location.getLongitude());

        String deviceLocation = location.getLatitude() + ":" + location.getLongitude();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(Util.PREF_KEY_DEVICE_CURRENT_LOCATION, deviceLocation).apply();
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CONSTANT);
            return false;
        }
        return true;
    }

    private void loadUser() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        Log.i(TAG, "Device ID :" +  Util.getDeviceId());

        if (preferences.getString(Util.PREF_KEY_DEVICEID, "") != null && preferences.getString(Util.PREF_KEY_DEVICEID, "").length()>0) {
            String userRefInJSONForm = preferences.getString(Util.PREF_KEY_USER, "");
            if (userRefInJSONForm != null && userRefInJSONForm.length()>0) {
                Log.i(TAG, "User exists in preferences, parsing it.");
                loggedInUser = gson.fromJson(userRefInJSONForm, User.class);
            } else {
                Log.i(TAG, "User doesn't exist in preferences, getting it from server and will save into preferences");
                String deviceId = preferences.getString(Util.PREF_KEY_DEVICEID, "");
                loggedInUser =(deviceId!=null && deviceId.length()>0) ? getUserByDeviceId(deviceId) : null;
                if (loggedInUser != null) {
                    String userInJSONForm = gson.toJson(loggedInUser);
                    preferences.edit().putString(Util.PREF_KEY_USER, userInJSONForm).apply();
                }
            }
        } else {
            String deviceId = Util.getDeviceId();
            loggedInUser = getUserByDeviceId(deviceId);
            if (loggedInUser != null) {
                String userInJSONForm = gson.toJson(loggedInUser);
                preferences.edit().putString(Util.PREF_KEY_USER, userInJSONForm).apply();
            }
        }

        Log.i(TAG, "GCM Token:" + preferences.getString(Util.PREF_GCM_TOKEN, ""));
        if (loggedInUser != null && preferences.getString(Util.PREF_GCM_TOKEN, "").length()<=0) {
            Intent itent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
            itent.putExtra("userId", loggedInUser.getUserId());
            startService(itent);
        }

        Log.i(TAG, "Logged In User :" + loggedInUser);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
