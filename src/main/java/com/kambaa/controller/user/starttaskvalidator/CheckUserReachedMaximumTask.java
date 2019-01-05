package com.kambaa.controller.user.starttaskvalidator;

import org.springframework.beans.factory.annotation.Value;

import com.kambaa.helper.TaskManagement;

public class CheckUserReachedMaximumTask implements StartTaskValidator{

	@Value("${user.task.running.max.count}")
	int maximumTaskPerUser;
	
	private Long userID;
	private TaskManagement taskManagement;
	
	public CheckUserReachedMaximumTask(Long userid,TaskManagement taskManagement) {
		this.userID=userid;
		this.taskManagement=taskManagement;
	}
		
	@Override
	public void Validate() {
		if(!(taskManagement.countRunningTaskByUserID(userID)<=maximumTaskPerUser)) {
			throw new IllegalArgumentException("You already reached the maximum number of running task ");
		}else {
			
		}
	}

}
