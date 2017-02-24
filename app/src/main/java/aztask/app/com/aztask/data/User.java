package aztask.app.com.aztask.data;

public class User {
	private int userId;
	private String userName;
	private String userEmail;
	private String userMobile;
	private String userSkills;
	private String gcmToken;

	private DeviceInfo deviceInfo;
	
	public User(){}
	
	public User(int userId,String userName, String userEmail, String userMobile, String userSkills,String gcmToken,DeviceInfo deviceInfo) {
		super();
		this.userId=userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userMobile = userMobile;
		this.userSkills = userSkills;
		this.gcmToken=gcmToken;
		this.deviceInfo=deviceInfo;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserSkills() {
		return userSkills;
	}

	public void setUserSkills(String userSkills) {
		this.userSkills = userSkills;
	}
	
	
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public String getGcmToken() {
		return gcmToken;
	}

	public void setGcmToken(String gcmToken) {
		this.gcmToken = gcmToken;
	}


	@Override
	public String toString() {
		return "User [userName=" + userName + "]";
	}

}
