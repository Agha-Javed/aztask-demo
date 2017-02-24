package aztask.app.com.aztask.data;

public class Task {

	private String taskDesc;
	private String taskCategories;
	private String taskComments;
	private DeviceInfo deviceInfo;
	
	public Task(){}

	public Task(String taskDesc, String taskCategories, String taskComments,DeviceInfo deviceInfo) {
		super();
		this.taskDesc = taskDesc;
		this.taskCategories = taskCategories;
		this.taskComments=taskComments;
		this.deviceInfo = deviceInfo;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getTaskCategories() {
		return taskCategories;
	}

	public void setTaskCategories(String taskCategories) {
		this.taskCategories = taskCategories;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public String getTaskComments() {
		return taskComments;
	}

	public void setTaskComments(String taskComments) {
		this.taskComments = taskComments;
	}

	@Override
	public String toString() {
		return "Task [taskDesc=" + taskDesc + "]";
	}
	
}
