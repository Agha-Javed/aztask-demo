package aztask.app.com.aztask.data;

public class TaskCard {

	private String taskId;
	private String taskDesc;
    private String taskLocation;
    private String taskBudget;
    private String taskTime;
    private String taskOwnerContact;
    private String taskOwnerName;

    private int imageResourceId;
    private int isfav;
    private int isturned;

    public int getIsturned() {
        return isturned;
    }

    public void setIsturned(int isturned) {
        this.isturned = isturned;
    }

    public int getIsfav() {
        return isfav;
    }

    public void setIsfav(int isfav) {
        this.isfav = isfav;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
    
    public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

    public String getTaskLocation() {
        return taskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        this.taskLocation = taskLocation;
    }

    public String getTaskBudget() {
        return taskBudget;
    }

    public void setTaskBudget(String taskBudget) {
        this.taskBudget = taskBudget;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskOwnerContact() {
        return taskOwnerContact;
    }

    public void setTaskOwnerContact(String taskOwnerContact) {
        this.taskOwnerContact = taskOwnerContact;
    }

    public String getTaskOwnerName() {
        return taskOwnerName;
    }

    public void setTaskOwnerName(String taskOwnerName) {
        this.taskOwnerName = taskOwnerName;
    }
}




