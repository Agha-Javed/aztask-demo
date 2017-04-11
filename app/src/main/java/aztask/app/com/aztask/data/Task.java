package aztask.app.com.aztask.data;

public class Task {

	private String taskDesc;
	private String taskCategories;
	private String taskLocation;
	private String task_min_max_budget;
	private DeviceInfo deviceInfo;
	
	public Task(){}

	public Task(String taskDesc, String taskCategories, String task_min_max_budget,String taskLocation,DeviceInfo deviceInfo) {
		super();
		this.taskDesc = taskDesc;
		this.taskCategories = taskCategories;
		this.taskLocation=taskLocation;
		this.task_min_max_budget =task_min_max_budget;
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
	
	public String getTask_min_max_budget() {
		return task_min_max_budget;
	}

	public void setTask_min_max_budget(String task_min_max_budget) {
		this.task_min_max_budget = task_min_max_budget;
	}

	public String getTaskLocation() {
		return taskLocation;
	}

	public void setTaskLocation(String taskLocation) {
		this.taskLocation = taskLocation;
	}

	@Override
	public String toString() {
		return "Task [taskDesc=" + taskDesc + "]";
	}

}
