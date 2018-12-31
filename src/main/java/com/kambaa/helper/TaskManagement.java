package com.kambaa.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kambaa.entity.Task;

@Service
public class TaskManagement {

	@Autowired
	Map<Long,Task> listTask;

	private static final Logger logger = LoggerFactory.getLogger(TaskManagement.class);

	public boolean addTask(Task taskItem) {
		try {
			if (listTask.put(taskItem.getId(),taskItem) != null) {
				logger.info("Task add success");
				return true;
			} else {
				logger.info("Task add failed");
				return true;
			}
		} catch (Exception e) {
			logger.error("Error occured when add task to task list :" + e);
			return false;
		}
	}
	
	
	public Map<Long,Task> stopTask() {
		try {
			return listTask;
		} catch (Exception e) {
			logger.error("Error occured when add task to task list :" + e);
			return null;
		}
	}

	
	
	public List<Task> getAllTask() {
		try {
			return new ArrayList<Task>(listTask.values());
		} catch (Exception e) {
			logger.error("Error occured when add task to task list :" + e);
			return null;
		}
	}
	
	
	public boolean existTask(Long key) {
		try {
			if(listTask.containsKey(key)) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			logger.error("Error occured when check exist :" + e);
			return false;
		}
	}
	
	
	public Task getByID(Long key) {
		try {
			return listTask.get(key);
		} catch (Exception e) {
			logger.error("Error occured when get by id :" + e);
			return null;
		}
	}

}
