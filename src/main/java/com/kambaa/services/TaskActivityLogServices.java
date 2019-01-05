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

import com.kambaa.entity.TaskActivityLog;
import com.kambaa.rowmapper.TaskActivityLogRowMapper;

@Service
public class TaskActivityLogServices {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedJdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskActivityLogServices.class);
	
	public boolean saveActivityLog(Long userid,Long taskid,String activity,String status,String description) {
		try {
			String sql="INSERT INTO task_activity_log(task_activity_log_task_id,task_activity_log_user_id,task_activity_log_activity,task_activity_log_status,task_activity_log_description,task_activity_log_updated_at,task_activity_log_created_at) VALUES(?,?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql, new Object[] {taskid,userid,activity,status,description});
			return true;
		}catch (Exception e) {
			logger.error("Error occured when save the activity log save :"+e);
			return false;
		}
	}

	public List<TaskActivityLog> FindAllByUserAndTaskWithPagination(long userid, Long taskid, int count, int offset) {
		try {
			String sql="SELECT * FROM task_activity_log WHERE task_activity_log_user_id=:userid AND task_activity_log_task_id=:taskid LIMIT :count OFFSET :offset";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userid",userid);
			params.put("taskid", taskid);
			params.put("count",count);
			params.put("offset",offset);
			return this.namedJdbcTemplate.query(sql, params,new TaskActivityLogRowMapper());
		}catch (Exception e) {
			logger.error("Error occured when getting the activity log for particular task :"+e);
			return null;
		}
	}

	public int countByUserAndTaskID(long userid, Long taskid) {
		try {
			String sql="SELECT COUNT(task_activity_log_id) FROM task_activity_log WHERE task_activity_log_user_id=? AND task_activity_log_task_id=?";
			return this.jdbcTemplate.queryForObject(sql, new Object[] {userid,taskid},Integer.class);
		}catch (Exception e) {
			logger.error("Error occured when count activity log for user with task id :"+e);
			return 0;
		}
	}
	
	
}
