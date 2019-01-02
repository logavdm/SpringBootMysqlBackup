package com.kambaa.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.kambaa.entity.Task;
import com.kambaa.model.TaskWithObject;
import com.kambaa.rowmapper.TaskRowMapper;
import com.kambaa.rowmapper.TaskWithObjectRowmapper;

@Service
public class TaskServices {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Autowired
	TransactionTemplate transactionTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskServices.class);
	
	@Async
	public void updateTaskStatusByID(Long taskID,String status) {
		try {
			this.jdbcTemplate.update("UPDATE task SET task_status=? WHERE task_id=?",new Object[] {status,taskID});
		}catch (Exception e) {
			logger.error("Error occured when update the task status :"+e);
		}
	}
	
	
	public List<Task> findAllByUserID(Long userID){
		try {
			String sql="SELECT * from task WHERE task_users_id=?";
			return this.jdbcTemplate.query(sql, new Object[] {userID},new TaskRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the task list by user :"+e);
			return null;
		}
	}
	
	public boolean existByUserIDAndID(Long userID,Long taskID) {
		try {
			String sql="SELECT COUNT(*) from task WHERE task_id=? AND task_users_id=? AND task_enabled=?";
			return this.jdbcTemplate.queryForObject(sql, new Object[] {taskID,userID,true},Integer.class)>0?true:false;
		}catch (Exception e) {
			logger.error("Error occured when getting the task for user :"+e);
			return false;
		}
	}
	
	public TaskWithObject findByUserIDAndId(Long userID,Long taskID) {
		try {
			String sql="SELECT * from task WHERE task_id=? AND task_users_id=? AND task_enabled=? LIMIT 1";
			return this.jdbcTemplate.query(sql, new Object[] {taskID,userID,true},new TaskWithObjectRowmapper()).get(0);
		}catch (Exception e) {
			logger.error("Error occured when getting the task for user :"+e);
			return null;
		}
	}
	
}
