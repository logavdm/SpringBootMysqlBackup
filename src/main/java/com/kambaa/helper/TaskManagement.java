package com.kambaa.helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.kambaa.model.TaskWithObject;
import com.kambaa.services.TaskServices;

@Service
public class TaskManagement {

	@Autowired
	Map<Long,Map<Long,TaskWithObject>> listTask;
	
	@Autowired
	ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	TaskServices taskServices;
	
	@Value("${user.task.running.max.count}")
	int maxRunningTaskForUser;

	private static final Logger logger = LoggerFactory.getLogger(TaskManagement.class);

	
	public boolean existTask(Long key) {
		try {
			if(listTask.containsKey(key)) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error occured when check exist :" + e);
			return false;
		}
	}
	
	public boolean existByUserIdAndTaskID(Long userid,Long taskID) {
		try {
			if(listTask.containsKey(userid)) {
				logger.info("user already have running task");
				if(listTask.get(userid).containsKey(taskID)) {
					logger.info("user id and task id already exist");
					return true;
				}else {
					logger.info("task id not found");
					return false;
				}
			}else {
				logger.info("User don't have any running task");
				return false;
			}
		}catch (Exception e) {
			logger.error("Error occured when checking the task exist by user id :"+e);
			return true;
		}
	}
	
	public boolean checkUserReachMaxRunningTask(Long userID) {
		try {
			if(listTask.get(userID)==null || listTask.get(userID).size()<=maxRunningTaskForUser) {
				logger.info("User not reach the maximum running task");
				return true;
			}else {
				logger.info("user already reached the maximum running task");
				return false;
			}
		}catch (Exception e) {
			logger.error("Error occured when user max running task count check :"+e);
			return false;
		}
	}
	
	public boolean addNewTask(Long userid,TaskWithObject task) {
		try {
			if(listTask.containsKey(userid)) {
				Map<Long,TaskWithObject> mapTask=listTask.get(userid);
				mapTask.put(task.getId(),task);
			}else {
				Map<Long,TaskWithObject> mapTask=new HashMap<Long,TaskWithObject>();
				mapTask.put(task.getId(),task);
				listTask.put(userid, mapTask);
			}
			return true;
		}catch (Exception e) {
			logger.error("Error occured when task add to running task list :"+e);
			return false;
		}
	}
	
	public boolean stopTask(long userid,long taskId) {
		try {
			if(listTask.containsKey(userid) && listTask.get(userid).containsKey(taskId)) {
				if(listTask.get(userid).get(taskId).getTask().cancel(false)) {
					listTask.get(userid).remove(taskId);
					
					if(listTask.get(userid).size()<=0) {
						listTask.remove(userid);
					}
					
					logger.debug("task stopped and remove successfully");
					return true;
				}else{
					logger.debug("error when stop the task");
					return false;
				}
			}else {
				logger.debug("Task not found to stop");
				return false;
			}
		}catch (Exception e) {
			logger.error("Error occured when stop the task :"+e);
			return false;
		}
	}
	
	@Async
	public void updateTaskStatusByID(Long taskID,String status) {
		try {
			taskServices.updateTaskStatusByID(taskID, status);
		}catch (Exception e) {
			logger.error("Error occured when task status update method :"+e);
		}
	}
	
	public int countRunningTaskByUserID(Long userID) {
		try {
			if(listTask.containsKey(userID)) {
				return listTask.get(userID).size();
			}else {
				return 0;
			}
		}catch (Exception e) {
			logger.error("Error when count running task list by user id :"+e);
			return Integer.MAX_VALUE;
		}
	}
	
}
