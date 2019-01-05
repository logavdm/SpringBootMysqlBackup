package com.kambaa.controller.user.starttaskvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kambaa.helper.TaskManagement;

public class CheckAlreadyRunning implements StartTaskValidator{

	private static final Logger logger = LoggerFactory.getLogger(CheckAlreadyRunning.class);
	
	private Long userID;
	private Long taskID;
	private TaskManagement taskManagement;
	
	public CheckAlreadyRunning(Long userid,Long taskid,TaskManagement taskManagement) {
		this.userID=userid;
		this.taskID=taskid;
		this.taskManagement=taskManagement;
	}
	
	@Override
	public void Validate() {
		if(taskManagement.existByUserIdAndTaskID(userID, taskID)) {
			logger.debug("task running already");
			throw new IllegalArgumentException("Task running Already");
		}else {
			logger.debug("Task not running already");
		}
	}

}
