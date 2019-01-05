package com.kambaa.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			String sql="SELECT * from task WHERE task_users_id=? AND task_enabled=?";
			return this.jdbcTemplate.query(sql, new Object[] {userID,true},new TaskRowMapper());
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


	public List<Task> FindAllByUserWithStatusWithPagination(long userid,String status, int count, int offset) {
		try {
			String sql="SELECT * FROM task WHERE task_users_id=:userid AND task_status=:status LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("status", status);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql,params,new TaskRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the all user list with pagination :"+e);
			return null;
		}
	}


	public List<Task> FindAllByUserWithStatusWithFilterAndPagination(long userid, String status, String filter, int count, int offset) {
		try {
			String sql="SELECT * FROM task WHERE task_users_id=:userid AND task_status=:status AND task_id LIKE :filter OR task_name LIKE :filter OR task_status LIKE :filter LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("status", status);
			params.put("filter",filter);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql,params,new TaskRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the all task list with filter and status :"+e);
			return null;
		}
	}


	public int countByUser(long userid) {
		String sql="SELECT COUNT(task_id) FROM task WHERE task_users_id=?";
		return this.jdbcTemplate.queryForObject(sql, new Object[] {userid},Integer.class);
	}


	public int countByUserAndStatus(long userid, String status) {
		String sql="SELECT COUNT(task_id) FROM task WHERE task_users_id=? AND task_status=?";
		return this.jdbcTemplate.queryForObject(sql, new Object[] {userid,status},Integer.class);
	}


	public int countByUserAndStatusAndFilter(long userid, String status, String filter) {
		String sql="SELECT COUNT(task_id) FROM task WHERE task_users_id=? AND task_status=? AND task_id LIKE :filter OR task_name LIKE :filter OR task_status LIKE :filter";
		return this.jdbcTemplate.queryForObject(sql, new Object[] {userid,status},Integer.class);
	}


	public List<Task> FindAllByUserWithPagination(long userid, int count, int offset) {
		try {
			String sql="SELECT * FROM task WHERE task_users_id=:userid LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql,params,new TaskRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the all user list with pagination :"+e);
			return null;
		}
	}


	public List<Task> FindAllByUserWithFilterAndPagination(long userid,String filter, int count, int offset) {
		try {
			String sql="SELECT * FROM task WHERE task_users_id=:userid AND task_id LIKE :filter OR task_name LIKE :filter OR task_status LIKE :filter LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("filter",filter);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql,params,new TaskRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the all task list with filter and status :"+e);
			return null;
		}
	}


	public int countByUserAndFilter(long userid, String filter) {
		String sql="SELECT COUNT(task_id) FROM task WHERE task_users_id=:userid AND task_id LIKE :filter OR task_name LIKE :filter OR task_status LIKE :filter";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid",userid);
		params.put("filter",filter);
		return this.namedJdbcTemplate.queryForObject(sql, params,Integer.class);
	}



}
