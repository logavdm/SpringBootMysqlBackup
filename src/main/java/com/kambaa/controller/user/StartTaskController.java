package com.kambaa.controller.user;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.controller.user.starttaskvalidator.CheckAlreadyRunning;
import com.kambaa.controller.user.starttaskvalidator.CheckUserReachedMaximumTask;
import com.kambaa.controller.user.starttaskvalidator.StartTaskValidator;
import com.kambaa.controller.user.starttaskvalidator.TaskExistCheck;
import com.kambaa.helper.TaskManagement;
import com.kambaa.model.Response;
import com.kambaa.model.TaskWithObject;
import com.kambaa.model.UserModel;
import com.kambaa.mysql.backup.module.MysqlBackUpTask;
import com.kambaa.mysql.backup.module.MysqlExportServices;
import com.kambaa.services.TaskActionLogServices;
import com.kambaa.services.TaskActivityLogServices;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET,RequestMethod.POST})
public class StartTaskController {

	@Autowired
	TaskServices taskServices;
	
	@Autowired
	TaskActivityLogServices taskActivityLogServices;
	
	@Autowired
	TaskActionLogServices TaskActionLogServices;
	
	@Autowired
	TaskManagement taskManagement;
	
	@Autowired
	MysqlExportServices mysqlExportServices;
	
	@Autowired
	ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	Response response;
		
	
	private static final Logger logger = LoggerFactory.getLogger(StartTaskController.class);
	
	
	@RequestMapping(value="/task/start/{taskid}",method= {RequestMethod.POST})
	public Response startTask(@PathVariable("taskid")Long taskID,HttpSession session) {
		try {
			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
			List<StartTaskValidator> listValidation=new LinkedList<>();
			listValidation.add(new TaskExistCheck(user.getId(),taskID,taskServices));
			listValidation.add(new CheckAlreadyRunning(user.getId(),taskID,taskManagement));
			listValidation.add(new CheckUserReachedMaximumTask(user.getId(),taskManagement));
			
			try {
				//VALIDATION START HERE
				for (StartTaskValidator startTaskValidator : listValidation) {
					startTaskValidator.Validate();
				}
				
				TaskWithObject task=taskServices.findByUserIDAndId(user.getId(),taskID);
				ScheduledFuture<?> taskThread = taskScheduler.schedule(new MysqlBackUpTask(mysqlExportServices,task),new CronTrigger(task.getCronExpression()));
				task.setTask(taskThread);
				
				if(taskManagement.addNewTask(user.getId(), task)) {
					logger.info("Task start success");
					taskServices.updateTaskStatusByID(task.getId(),"RUNNING");
					writeTaskAction(user.getId(),taskID,"START TASK"," User has been start the Task");
					response.setResponsecode("200");
					response.setMessage("Task start Success");
					response.setResponse(null);
				}else {
					logger.info("Error occured when task add to running task list");
					response.setResponsecode("400");
					response.setMessage("Error occured when task add to running task list");
					response.setResponse(null);
				}
				
			}catch (IllegalArgumentException e) {
				logger.error("Task validation failed with the exception of :"+e.getLocalizedMessage());
				response.setResponsecode("400");
				response.setMessage(e.getLocalizedMessage());
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
		
	@Async
	void writeTaskAction(Long userid,Long taskid,String action,String description) {
		try {
			TaskActionLogServices.saveActionLog(userid, taskid, action, description);
			logger.info("Task action write success");
		}catch (Exception e) {
			logger.error("Error occured when write the task action :"+e);
		}
	}
	
}
