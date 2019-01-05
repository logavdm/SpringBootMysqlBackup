package com.kambaa.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.kambaa.entity.TaskActionLog;
import com.kambaa.rowmapper.TaskActionLogRowMapper;

@Service
public class TaskActionLogServices {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedJdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskActionLogServices.class);
	
	public boolean saveActionLog(Long userid,Long taskid,String action,String description) {
		try {
			String sql="INSERT INTO task_action_log(task_action_log_task_id,task_action_log_users_id,task_action_log_action_type,task_action_log_action_description,task_action_log_updated_at,task_action_log_created_at) VALUES(?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql, new Object[] {taskid,userid,action,description});
			return true;
		}catch (Exception e) {
			logger.error("Error occured when save the action log save :"+e);
			return false;
		}
	}

	public List<TaskActionLog> FindAllByUserAndTaskWithPagination(long userid, Long taskid, int count, int offset) {
		try {
			String sql="SELECT * FROM task_action_log WHERE task_action_log_users_id=:userid AND task_action_log_task_id=:taskid LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("taskid", taskid);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql, params,new TaskActionLogRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the action log for particular task :"+e);
			return null;
		}
	}

	public int countByUserAndTaskID(long userid, Long taskid) {
		try {
			String sql="SELECT COUNT(task_action_log_id) FROM task_action_log WHERE task_action_log_users_id=? AND task_action_log_task_id=?";
			return this.jdbcTemplate.queryForObject(sql, new Object[] {userid,taskid},Integer.class);
		}catch (Exception e) {
			logger.error("Error occured when count action log for user with task id :"+e);
			return 0;
		}
	}
	
	
}
