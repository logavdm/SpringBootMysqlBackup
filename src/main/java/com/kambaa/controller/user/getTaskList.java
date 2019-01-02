package com.kambaa.controller.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.entity.Task;
import com.kambaa.model.Response;
import com.kambaa.model.UserModel;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET})
public class getTaskList {

	@Autowired
	Response response;
	
	@Autowired
	TaskServices taskServices;
		
	private static final Logger logger = LoggerFactory.getLogger(getTaskList.class);
	
	@RequestMapping("/task/list")
	public Response getAllTaskList(HttpSession session) {
		logger.info("-----------------------USER GET ALL TASK LIST  START HERE---------------------------------");
		try {
			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
			List<Task> listTask=taskServices.findAllByUserID(user.getId());
			if(listTask!=null && listTask.size()>0) {
				logger.info("Task list get success");
				response.setResponsecode("200");
				response.setMessage("Task list get success");
				response.setResponse(listTask);
			}else {
				logger.info("Task list get as Empty");
				response.setResponsecode("404");
				response.setMessage("Task list get as Empty");
				response.setResponse(null);
			}
		}catch (Exception e) {
			logger.error("Error occured when getting the all task list :"+e);
			response.setResponsecode("500");
			response.setMessage("something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-----------------------USER GET ALL TASK LIST END HERE---------------------------------");
		return response;
	}
	
}
