package com.kambaa.controller.user.stoptaskvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kambaa.services.TaskServices;


public class TaskExistCheck implements StopTaskValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskExistCheck.class);
	
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
			logger.debug("task not exist on database");
			throw new IllegalArgumentException("Task not exist");
		}else {
			logger.debug("Task exist");
		}
	}

	
}
