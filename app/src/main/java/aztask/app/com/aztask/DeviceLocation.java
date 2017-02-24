package aztask.app.com.aztask;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class DeviceLocation implements LocationListener{

	private double longitude;// = location.getLongitude();
	private double latitude;// = location.getLatitude();

    public void onLocationChanged(Location location) {
	      longitude = location.getLongitude();
	      latitude = location.getLatitude();
	    }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		public double getLatitude(){
			return latitude;
		}
		
		public double getLongitude(){
			return longitude;
		}

}
