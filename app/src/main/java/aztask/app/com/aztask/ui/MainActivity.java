package aztask.app.com.aztask.ui;

import aztask.app.com.aztask.R;
import aztask.app.com.aztask.TabsAdapter;
import aztask.app.com.aztask.data.User;
import aztask.app.com.aztask.net.CheckUserRegisterationWorker;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String CLASS_NAME = "MainActivity";
    private static Context appContext;
    private static int USER_ID;
    private static User loggedInUser;
    private static final int FINE_LOCATION_PERMISSION_CONSTANT = 100;
    private static final int FINE_LOCATION_ENABLED_CONSTANT = 101;
    private SharedPreferences permissionStatus;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static Location currentUserLocation;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = getApplicationContext();

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        boolean hasPermission = checkPermissions();


        // TODO : I have to access device id from device not this harcoded.
        //"abcd11345"

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPreferences.edit().putString(Util.PREF_KEY_DEVICEID,"abcd11345").apply();
/*
        SharedPreferences sharedPreferences = getSharedPreferences(Util.PREF_KEY_DEVICEID, MODE_PRIVATE);
        sharedPreferences.edit().putString(Util.PREF_KEY_DEVICEID,"abcd11345").apply();
*/

        loggedInUser=isUserRegistered();
        if (hasPermission) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            proceedWithPermission();
        }else{
            proceedWithoutPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
            Log.i("MainActivity", "connecting google api client.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient != null) {
            Log.i("MainActivity", "Disconnecting google api client.");
            googleApiClient.disconnect();
        }
    }


    private void proceedWithPermission() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("MainActivity", "Getting User's current location and getting tasks by it.");


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("NearBy Tasks"));
        tabLayout.addTab(tabLayout.newTab().setText("My Tasks"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsAdapter adapter = new TabsAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void proceedWithoutPermission() {
        Log.i("MainActivity", "Getting Default location and getting tasks by it.");
        // I WILL MODIFY IT, TO CALL
        proceedWithPermission();
    }

    //TODO THIS METHOD WILL GO TO REGISTERATION ACTIVITY
    private User isUserRegistered() {
        String deviceId = Util.getDeviceId();

        try {
            User user = new CheckUserRegisterationWorker().execute(deviceId).get();
            Log.i(CLASS_NAME, "User is registered:" + user.getDeviceInfo().getDeviceId());
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
        return loggedInUser.getUserId();
    }

    public static boolean userRegistered() {
        return (USER_ID > 0) ? true : false;
    }

    public static User getRegisteredUser() {
        return loggedInUser;
    }


    public void enableLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Location seems to be disabled, please enable it to get tasks nearby you.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        startActivityForResult(intent, FINE_LOCATION_ENABLED_CONSTANT);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_CONSTANT);
            } else{
                return false;
            }



/*
            if (permissionStatus.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Permissions");
                builder.setMessage("This app needs loocation permission (second check).");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, FINE_LOCATION_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
*/


        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
                editor.commit();

                LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                try {
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        proceedWithPermission();
                    } else {
                        enableLocation();
                        proceedWithoutPermission();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "User denied for permission, getting defualt tasks", Toast.LENGTH_LONG).show();
                proceedWithoutPermission();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.i("onActivityResult()", "JAVED:: Request Code:" + requestCode + " , Result Code:" + resultCode + " and Data:" + data);

        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_CONSTANT: {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    proceedWithPermission();
                }
            }

            case FINE_LOCATION_ENABLED_CONSTANT: {
                if (resultCode == RESULT_OK) {
                    proceedWithPermission();
                }

            }

        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("MainActivity", "OnConnected Method");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(300000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MainActivity","onConnectionSuspended Method");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("MainActivity","onConnectionFailed Method");

    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentUserLocation=location;
        Log.i("MainActivity","JAVED::Location:: Latitude:"+location.getLatitude()+" and Longitude:"+location.getLongitude());

       // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String deviceLocation=location.getLatitude()+"|"+location.getLongitude();
        SharedPreferences sharedPreferences = getSharedPreferences(Util.PREF_KEY_DEVICE_LOCATION, MODE_PRIVATE);
        sharedPreferences.edit().putString(Util.PREF_KEY_DEVICE_LOCATION,deviceLocation).apply();

    }

    public static Location getCurrentUserLocation() {
        return currentUserLocation;
    }

}
