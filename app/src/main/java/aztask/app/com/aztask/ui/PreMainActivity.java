package aztask.app.com.aztask.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import aztask.app.com.aztask.R;

public class PreMainActivity extends AppCompatActivity {

    private static final int FINE_LOCATION_ACCESS_PERMISSION_CONSTANT = 100;
    private static final int ENABLE_FINE_LOCATION_CONSTANT = 101;
    private CardView locationErrorView;
    TextView errorName;
    TextView errorMsg;
    TextView errorButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_main_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        locationErrorView = (CardView) findViewById(R.id.loctionError);

        errorName = (TextView) findViewById(R.id.errorName);
        errorMsg = (TextView) findViewById(R.id.errorMsg);
        errorButton = (TextView) findViewById(R.id.errorBtn);

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                startActivityForResult(intent, FINE_LOCATION_ACCESS_PERMISSION_CONSTANT);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            if (!isLocationEnabled(this)) {
                errorName.setText("LOCATION NOT FOUND!");
                errorMsg.setText("Please turn on Location Services. For the best experience.");
                errorButton.setText("GOT IT!");
                errorButton.setOnClickListener(new OnClickLocationListener());
                locationErrorView.setVisibility(View.VISIBLE);
            } else if (!isNetworkAvailable()) {
                errorName.setText("SORRY :-(");
                errorMsg.setText("We are not able to connect with our server,please check internet connection.");
                errorButton.setText("TRY AGAIN!");
                errorButton.setOnClickListener(new OnClickNetworkListener());
                locationErrorView.setVisibility(View.VISIBLE);
            } else {
                locationErrorView.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                //Toast.makeText(getBaseContext(), "Location is enabled.", Toast.LENGTH_LONG).show();
            }

        }
    }


    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(PreMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("We need to access your location,please allow us.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            ActivityCompat.requestPermissions(PreMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_PERMISSION_CONSTANT);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            //dialog.cancel();
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                try {
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "User denied for permission, getting defualt tasks", Toast.LENGTH_LONG).show();
            }
        }


    }

    private class OnClickLocationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            startActivityForResult(intent, FINE_LOCATION_ACCESS_PERMISSION_CONSTANT);
        }
    }

    private class OnClickNetworkListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isNetworkAvailable()){
                locationErrorView.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
