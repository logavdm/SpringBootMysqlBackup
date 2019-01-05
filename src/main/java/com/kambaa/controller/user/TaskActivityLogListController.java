package com.kambaa.controller.user;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.entity.TaskActivityLog;
import com.kambaa.model.Pageable;
import com.kambaa.model.Response;
import com.kambaa.model.UserModel;
import com.kambaa.services.TaskActivityLogServices;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET,RequestMethod.POST})
public class TaskActivityLogListController {

	@Autowired
	TaskActivityLogServices taskActivityLogServices;
	
	@Autowired
	TaskServices taskServices;
	
	@Autowired
	Response response;
	
	@Autowired
	Pageable pageable;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskActivityLogListController.class);
	
	@RequestMapping(value="/task/activity",method= {RequestMethod.GET})
	public Response getActivityLogWithPagination(@RequestParam("id")Long taskid,@RequestParam("page")int page,@RequestParam("limit")int count,HttpSession session) {
		try {
			UserModel user=(UserModel) session.getAttribute("USERDETAILS");
			if(!taskServices.existByUserIDAndID(user.getId(),taskid)) {
				logger.error("Task not exist for the user ");
				response.setResponsecode("400");
				response.setMessage("invalid task id");
				response.setResponse(null);
			}else {
				
				List<TaskActivityLog> listActivity;
				listActivity=taskActivityLogServices.FindAllByUserAndTaskWithPagination(user.getId(),taskid,count,(page*count));
				
				if(listActivity!=null && listActivity.size()>0) {
					int totalActivityList=0;
					totalActivityList=taskActivityLogServices.countByUserAndTaskID(user.getId(),taskid);
					pageable.setCurrentPage(page);
					pageable.setResponseCount(listActivity.size());
					pageable.setTotalRecords(totalActivityList);
					pageable.setResult(listActivity);
					
					response.setResponsecode("200");
					response.setMessage("task Activity list get success");
					response.setResponse(pageable);
				}else {
					response.setResponsecode("404");
					response.setMessage("task Activity list get as empty");
					response.setResponse(null);
				}
			}
		}catch (Exception e) {
			logger.error("Error occured when getting the activity log get :"+e);
			response.setResponsecode("500");
			response.setMessage("Something went wrong please try again later");
			response.setResponse(null);
		}
		return response;
	}
	
	
}
