package com.kambaa.controller.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.helper.TaskManagement;
import com.kambaa.model.Response;
import com.kambaa.model.UserModel;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET,RequestMethod.POST})
public class ViewTask {
	
	@Autowired
	Response response;
	
	@Autowired
	TaskManagement taskManagement;
	
	@Autowired
	TaskServices taskServices;
	
	private static final Logger logger = LoggerFactory.getLogger(ViewTask.class);
	
	@RequestMapping(value="/task/view/statistics/{id}",method= {RequestMethod.GET})
	public Response getTaskStatisticsDetails(@PathVariable("id") Long taskid,HttpSession session) {
		try {
			logger.info("-------------------USER TASK DETAIL VIEW CONTROLLER START HERE-------------------------");
			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
			if(!taskServices.existByUserIDAndID(user.getId(),taskid)) {
				logger.info("Task id not found");
				response.setResponsecode("400");
				response.setMessage("Invalid task to view");
				response.setResponse(null);
			}else {
				if(!taskManagement.existByUserIdAndTaskID(user.getId(),taskid)) {
					logger.info("Task not found on running task list");
					response.setResponsecode("400");
					response.setMessage("Task not running to view task live details");
					response.setResponse(null);
				}else {
					Map<String,Object> mapDetails=new HashMap<>();
					mapDetails.put("Key1","value");
					
					response.setResponsecode("200");
					response.setMessage("hii");
					response.setResponse(mapDetails);
					//ScheduledFuture<?> taskItem = threadPoolTaskScheduler().schedule(new RunnableTask(taskList.get(taskid).getTaskName()),new CronTrigger(taskList.get(taskid).getCronExpression()));
				}
			}
		}catch (Exception e) {
			logger.info("Error occured when get the task details :"+e);
			response.setResponsecode("500");
			response.setMessage("Something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-------------------USER TASK DETAIL VIEW CONTROLLER END HERE-------------------------");
		return response;
	}
	
	
//	@RequestMapping(value="/task/view/history/{id}",method= {RequestMethod.GET})
//	public Response getTaskHistoryDetails(@PathVariable("id") Long taskid,HttpSession session) {
//		try {
//			logger.info("-------------------USER TASK DETAIL VIEW CONTROLLER START HERE-------------------------");
//			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
//			if(!taskServices.existByUserIDAndID(user.getId(),taskid)) {
//				logger.info("Task id not found");
//				response.setResponsecode("400");
//				response.setMessage("Invalid task to view");
//				response.setResponse(null);
//			}else {
//				if(!taskManagement.existByUserIdAndTaskID(user.getId(),taskid)) {
//					logger.info("Task not found on running task list");
//					response.setResponsecode("400");
//					response.setMessage("Task not running to view task live details");
//					response.setResponse(null);
//				}else {
//					Map<String,Object> mapDetails=new HashMap<>();
//					
//					//ScheduledFuture<?> taskItem = threadPoolTaskScheduler().schedule(new RunnableTask(taskList.get(taskid).getTaskName()),new CronTrigger(taskList.get(taskid).getCronExpression()));
//				
//				}
//			}
//		}catch (Exception e) {
//			logger.info("Error occured when get the task details :"+e);
//			response.setResponsecode("500");
//			response.setMessage("Something went wrong please try again later");
//			response.setResponse(null);
//		}
//		logger.info("-------------------USER TASK DETAIL VIEW CONTROLLER END HERE-------------------------");
//		return response;
//	}
	

}
