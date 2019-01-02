package com.kambaa.controller.user;

import java.util.concurrent.ScheduledFuture;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.helper.TaskManagement;
import com.kambaa.model.Response;
import com.kambaa.model.RunnableTask;
import com.kambaa.model.TaskWithObject;
import com.kambaa.model.UserModel;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET,RequestMethod.POST})
public class TaskManagementController {

	@Autowired
	TaskServices taskServices;
	
	@Autowired
	TaskManagement taskManagement;
	
	@Autowired
	ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	Response response;
	
	@Value("${user.task.running.max.count}")
	int maxRunningTaskForUser;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskManagementController.class);
	
	@RequestMapping(value="/task/add",method= {RequestMethod.POST})
	public Response addTask(HttpSession session) {
		try {
			
			System.err.println("Flag"+maxRunningTaskForUser);
			
		}catch (Exception e) {
			logger.error("Error occured when task add :"+e);
			response.setResponsecode("500");
			response.setMessage("Error occured when new task add controller :"+e);
			response.setResponse(null);
		}
		return response;
	}
		
	@RequestMapping(value="/task/start/{taskid}",method= {RequestMethod.POST})
	public Response addTask(@PathVariable("taskid")Long taskID,HttpSession session) {
		try {
			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
			if(!taskManagement.existByUserIdAndTaskID(user.getId(), taskID)) {
				if(taskServices.existByUserIDAndID(user.getId(),taskID)) {
					if(taskManagement.checkUserReachMaxRunningTask(user.getId())) {
						TaskWithObject task=taskServices.findByUserIDAndId(user.getId(),taskID);
						ScheduledFuture<?> taskThread = taskScheduler.schedule(new RunnableTask(task.getTaskName()),new CronTrigger(task.getCronExpression()));
						task.setTask(taskThread);
						if(taskManagement.addNewTask(user.getId(), task)) {
							logger.info("Task start success");
							taskServices.updateTaskStatusByID(task.getId(),"RUNNING");
							response.setResponsecode("200");
							response.setMessage("Task start Success");
							response.setResponse(null);
						}else {
							logger.info("Error occured when task add to running task list");
							response.setResponsecode("400");
							response.setMessage("Error occured when task add to running task list");
							response.setResponse(null);
						}
					}else {
						logger.info("user already reached the maximum task count so not allow to start the task");
						response.setResponsecode("400");
						response.setMessage("You exceed the maximum number of concurrent running task");
						response.setResponse(null);
					}
				}else {
					logger.info("Task cannot found for the user");
					response.setResponsecode("400");
					response.setMessage("Task not found to start");
					response.setResponse(null);
				}
			}else {
				logger.info("Task running already");
				response.setResponsecode("400");
				response.setMessage("Task not exist");
				response.setResponse(null);
			}
		}catch (Exception e) {
			logger.error("Error occured when task add :"+e);
			response.setResponsecode("500");
			response.setMessage("Error occured when new task add controller :"+e);
			response.setResponse(null);
		}
		return response;
	}
	
	
}
