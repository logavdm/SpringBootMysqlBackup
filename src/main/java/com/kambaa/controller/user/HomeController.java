package com.kambaa.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@Autowired
	ThreadPoolTaskScheduler taskSchduler;
	
//	@Autowired
//	TaskManagement taskManagement;
	
	//private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

//	@RequestMapping("/")
//	public List<TaskWithObject> getAllSceduledTask() {
//		try {
//			return taskManagement.getAllTask();
//		}catch (Exception e) {
//			logger.error("Error occured when getting the list tasks :"+e);
//			return null;
//		}
//	}

	
//	@RequestMapping(value="/start",method=RequestMethod.POST)
//	public String startTask(@RequestParam("id")Long id) {
//		try {
//			if(taskManagement.existTask(id) && !taskManagement.getByID(id).isEnabled()) {
//				TaskWithObject task=taskManagement.getByID(id);
//				ScheduledFuture<?> taskItem=task.getTask();
//				if(taskItem.isCancelled() && taskItem.isDone()) {
//					ScheduledFuture<?> newTask=taskSchduler.schedule(new RunnableTask(task.getTaskName()),new CronTrigger(task.getCronExpression()));
//					task.setTask(newTask);
//					task.setEnabled(true);
//					return "Task Start Success";
//				}else {
//					logger.info("Task already running");
//					return "Error occured when cancel the task";
//				}
//			}else {
//				logger.info("Task not running or not exist");
//				return "Task not running or not exist";
//			}
//		}catch (Exception e) {
//			logger.error("Error occured when getting the list tasks :"+e);
//			return "error occured when stop the task";
//		}
//	}
	
	
//	@RequestMapping(value="/stop",method=RequestMethod.POST)
//	public String stopTask(@RequestParam("id")Long id) {
//		try {
//			if(taskManagement.existTask(id) && taskManagement.getByID(id).isEnabled()) {
//				TaskWithObject task=taskManagement.getByID(id);
//				ScheduledFuture<?> taskItem=task.getTask();
//				if(taskItem.isCancelled() || taskItem.isDone()) {
//					logger.info("Task already cancelled or done");
//					return "Task already cancelled or done";
//				}else {
//					if(taskItem.cancel(true)) {
//						logger.info("Task cancel success");
//						task.setEnabled(false);
//						logger.info("status change success");
//						return "success";
//					}else {
//						logger.info("Error occured when cancel the task");
//						return "Error occured when cancel the task";
//					}
//				}
//			}else {
//				logger.info("Task not running or not exist");
//				return "Task not running or not exist";
//			}
//		}catch (Exception e) {
//			logger.error("Error occured when getting the list tasks :"+e);
//			return "error occured when stop the task";
//		}
//	}
	
	

}
