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

import com.kambaa.entity.Task;
import com.kambaa.model.Pageable;
import com.kambaa.model.Response;
import com.kambaa.model.UserWithRole;
import com.kambaa.services.TaskServices;

@RestController
@RequestMapping(value="/user",method= {RequestMethod.GET})
public class TaskListController {

	@Autowired
	Response response;
	
	@Autowired
	Pageable pageable;
	
	@Autowired
	TaskServices taskServices;
		
	private static final Logger logger = LoggerFactory.getLogger(TaskListController.class);
	
	
	@RequestMapping("/task/list")
	public Response getAllTaskList(HttpSession session) {
		logger.info("-----------------------USER GET ALL TASK LIST  START HERE---------------------------------");
		try {
			UserWithRole user=(UserWithRole) session.getAttribute("USERDETAILS");
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
	
	
	@RequestMapping(value="/task/list/pagination",method= {RequestMethod.GET})
	public Response getAllTaskListWithPagination(HttpSession session,@RequestParam(value="page")int page,@RequestParam("limit")int count,@RequestParam(value="filter",required=false)String filter) {
		logger.info("-----------------------USER GET ALL TASK LIST WITH PAGINATION START HERE---------------------------------");
		try {
			UserWithRole user=(UserWithRole) session.getAttribute("USERDETAILS");
			List<Task> listTasks;
			if(filter==null || filter.isEmpty() || filter.equalsIgnoreCase("") || filter.equalsIgnoreCase(" ")) {
				logger.info("Filter parameter received as empty or null");
				listTasks=taskServices.FindAllByUserWithPagination(user.getId(),count,(page*count));
			}else {
				logger.info("Filter parameter received as:"+filter);
				listTasks=taskServices.FindAllByUserWithFilterAndPagination(user.getId(),filter,count,(page*count));
			}

			if(listTasks!=null && listTasks.size()>0) {
				int totalTaskList=0;
				if(filter==null || filter.isEmpty() || filter.equalsIgnoreCase("") || filter.equalsIgnoreCase(" ")) {
					totalTaskList=taskServices.countByUser(user.getId());
				}else {
					totalTaskList=taskServices.countByUserAndFilter(user.getId(),filter);
				}
				 
				pageable.setCurrentPage(page);
				pageable.setResponseCount(listTasks.size());
				pageable.setTotalRecords(totalTaskList);
				pageable.setResult(listTasks);
				
				response.setResponsecode("200");
				response.setMessage("task list get success");
				response.setResponse(pageable);
			}else {
				response.setResponsecode("404");
				response.setMessage("task list get as empty");
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
	
	
	@RequestMapping("/task/list/status")
	public Response getAllTaskListByStatus(HttpSession session,@RequestParam(value="status")String status,@RequestParam(value="page")int page,@RequestParam("limit")int count,@RequestParam(value="filter",required=false)String filter) {
		logger.info("-----------------------USER GET ALL TASK LIST WITH STATUS START HERE---------------------------------");
		try {
			UserWithRole user=(UserWithRole) session.getAttribute("USERDETAILS");
			List<Task> listTasks;
			if(filter==null || filter.isEmpty() || filter.equalsIgnoreCase("") || filter.equalsIgnoreCase(" ")) {
				logger.info("Filter parameter received as empty or null");
				listTasks=taskServices.FindAllByUserWithStatusWithPagination(user.getId(),status,count,(page*count));
			}else {
				logger.info("Filter parameter received as:"+filter);
				listTasks=taskServices.FindAllByUserWithStatusWithFilterAndPagination(user.getId(),status,filter,count,(page*count));
			}

			if(listTasks!=null && listTasks.size()>0) {
				int totalTaskList=0;
				if(filter==null || filter.isEmpty() || filter.equalsIgnoreCase("") || filter.equalsIgnoreCase(" ")) {
					totalTaskList=taskServices.countByUser(user.getId());
				}else {
					totalTaskList=taskServices.countByUserAndFilter(user.getId(),filter);
				}
				 
				pageable.setCurrentPage(page);
				pageable.setResponseCount(listTasks.size());
				pageable.setTotalRecords(totalTaskList);
				pageable.setResult(listTasks);
				
				response.setResponsecode("200");
				response.setMessage("task list get success");
				response.setResponse(pageable);
			}else {
				response.setResponsecode("404");
				response.setMessage("task list get as empty");
				response.setResponse(null);
			}
		}catch (Exception e) {
			logger.error("Error occured when getting the all task list :"+e);
			response.setResponsecode("500");
			response.setMessage("something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-----------------------USER GET ALL TASK LIST WITH STATUS END HERE---------------------------------");
		return response;
	}
	
	
}
