package com.kambaa.controller.user.starttaskvalidator;

import com.kambaa.services.TaskServices;


public class TaskExistCheck implements StartTaskValidator {
	
	private Long userID;
	private Long taskID;
	private TaskServices taskServices;
	
	public TaskExistCheck(Long userid,Long taskid,TaskServices taskServices) {
		this.userID=userid;
		this.taskID=taskid;
		this.taskServices=taskServices;
	}
	
	
	@Override
	public void Validate() {
		if(!taskServices.existByUserIDAndID(this.userID,this.taskID)) {
			throw new IllegalArgumentException("Task not exist");
		}
	}

	
}
