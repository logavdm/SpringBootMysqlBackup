package com.kambaa.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kambaa.entity.TaskActivityLog;

public class TaskActivityLogRowMapper implements RowMapper<TaskActivityLog> {

	@Override
	public TaskActivityLog mapRow(ResultSet row, int rowNum) throws SQLException {
		TaskActivityLog taskActivity=new TaskActivityLog();
		taskActivity.setId(row.getLong("task_activity_log_id"));
		taskActivity.setTaskID(row.getLong("task_activity_log_task_id"));
		taskActivity.setUserID(row.getLong("task_activity_log_user_id"));
		taskActivity.setActivity(row.getString("task_activity_log_activity"));
		taskActivity.setStatus(row.getString("task_activity_log_status"));
		taskActivity.setDescription(row.getString("task_activity_log_description"));		
		taskActivity.setUpdatedAt(row.getTimestamp("task_activity_log_updated_at")==null?null:row.getTimestamp("task_activity_log_updated_at").getTime());
		taskActivity.setCreatedAt(row.getTimestamp("task_activity_log_created_at")==null?null:row.getTimestamp("task_activity_log_created_at").getTime());
		return taskActivity;
	}
}
