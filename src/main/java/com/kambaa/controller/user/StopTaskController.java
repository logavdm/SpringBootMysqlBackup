package com.kambaa.controller.user;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.controller.user.stoptaskvalidator.CheckAlreadyRunning;
import com.kambaa.controller.user.stoptaskvalidator.StopTaskValidator;
import com.kambaa.helper.TaskManagement;
import com.kambaa.model.Response;
import com.kambaa.model.UserWithRole;
import com.kambaa.services.TaskActionLogServices;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET,RequestMethod.POST})
public class StopTaskController {
	
	@Autowired
	TaskServices taskServices;
	
	@Autowired
	TaskActionLogServices TaskActionLogServices;
	
	@Autowired
	TaskManagement taskManagement;
	
	@Autowired
	ThreadPoolTaskScheduler taskScheduler;
	
	@Autowired
	Response response;
	
	private static final Logger logger = LoggerFactory.getLogger(StopTaskController.class);
	
	@RequestMapping(value="/task/stop/{taskid}",method= {RequestMethod.POST})
	public Response stopTask(@PathVariable("taskid")Long taskID,HttpSession session) {
		try {
			UserWithRole user=(UserWithRole) session.getAttribute("USERDETAILS");
			List<StopTaskValidator> listValidation=new LinkedList<>();
			listValidation.add(new com.kambaa.controller.user.stoptaskvalidator.TaskExistCheck(user.getId(),taskID,taskServices));
			listValidation.add(new CheckAlreadyRunning(user.getId(),taskID,taskManagement));
			try {
				//VALIDATION START HERE
				for (StopTaskValidator startTaskValidator : listValidation) {
					startTaskValidator.Validate();
				}
				if(taskManagement.stopTask(user.getId(),taskID)) {
					logger.info("Task stop success");
					taskServices.updateTaskStatusByID(taskID, "STOPPED");
					writeTaskAction(user.getId(),taskID,"STOP TASK", "user has been stopeed the task");
					response.setResponsecode("200");
					response.setMessage("Task stop success");
					response.setResponse(null);
				}else {
					logger.info("Task stop failed");
					response.setResponsecode("400");
					response.setMessage("Task stop failed");
					response.setResponse(null);
				}
			}catch (IllegalArgumentException e) {
				logger.error("Task validation failed with the exception of :"+e.getLocalizedMessage());
				response.setResponsecode("400");
				response.setMessage(e.getLocalizedMessage());
				response.setResponse(null);
			}
		}catch (Exception e) {
			logger.error("Error occured when stop task :"+e);
			response.setResponsecode("500");
			response.setMessage("something went wrong please try after sometimes");
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
