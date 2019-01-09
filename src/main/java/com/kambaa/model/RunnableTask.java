//package com.kambaa.model;
//
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.kambaa.services.TaskActivityLogServices;
//
//public class RunnableTask implements Runnable {
//	
//	private static final Logger logger = LoggerFactory.getLogger(RunnableTask.class);
//
//	private TaskActivityLogServices logServices;
//	private TaskWithObject task;
//	private String message;
//
//	public RunnableTask(String message,TaskWithObject taskWithObject,TaskActivityLogServices ActivityLogServices) {
//		this.message = message;
//		this.task=taskWithObject;
//		this.logServices=ActivityLogServices;
//	}
//
//	@Override
//	public void run() {
//		logger.info(new Date() + " Runnable Task with " + message + " on thread " + Thread.currentThread().getName());
//		try {
//			logServices.saveActivityLog(task.getUserid(),task.getId(),"LOG WRITE","SUCCESS","LOG WRITE TO CONSOLE SUCCESS");
//		}catch (Exception e) {
//			logger.error("Error occured when write activity log");
//		}
//	}
//
//}
