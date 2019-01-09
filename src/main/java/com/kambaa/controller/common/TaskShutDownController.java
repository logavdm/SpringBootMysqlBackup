package com.kambaa.controller.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.model.Response;

@RestController
public class TaskShutDownController {
	
	@Autowired
	Response response;

	private static final Logger logger = LoggerFactory.getLogger(TaskShutDownController.class);
	
	@RequestMapping(value="/shutdown",method= {RequestMethod.POST})
	public Response shutDownAllTask() {
		try {
			logger.info("-----------------ADMIN SHUT DOWN ALL TASK CONTROLLER START HERE-------------------");
			
		}catch (Exception e) {
			logger.error("Error occured when shutdown all the task :"+e);
			response.setResponsecode("500");
			response.setMessage("Something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-----------------ADMIN SHUT DOWN ALL TASK CONTROLLER END HERE-------------------");
		return response;
	}
	
}
