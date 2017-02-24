package aztask.app.com.aztask.data;

public class DeviceInfo {

	private String latitude;
	private String longitude;
	private String deviceId;
	
	public DeviceInfo(){}

	public DeviceInfo(String latitude, String longitude, String deviceId) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.deviceId = deviceId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	
}
