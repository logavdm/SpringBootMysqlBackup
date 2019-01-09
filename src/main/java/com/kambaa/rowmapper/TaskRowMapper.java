package com.kambaa.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kambaa.entity.Task;

public class TaskRowMapper implements RowMapper<Task>  {

	@Override
	public Task mapRow(ResultSet row, int rowNum) throws SQLException {
		Task task=new Task();
		task.setId(row.getLong("task_id"));
		task.setTaskName(row.getString("task_name"));
		task.setCronExpression(row.getString("task_cron"));
		task.setStatus(row.getString("task_status"));
		task.setUserid(row.getLong("task_users_id"));
		task.setConnectionString(row.getString("task_connection_string"));
		task.setDatabase(row.getString("task_database"));
		task.setUsername(row.getString("task_database_username"));
		task.setPassword(row.getString("task_database_password"));
		task.setEnabled(row.getBoolean("task_enabled"));
		task.setUpdatedAt(row.getTimestamp("task_updated_at")==null?null:row.getTimestamp("task_updated_at").getTime());
		task.setCreatedAt(row.getTimestamp("task_created_at")==null?null:row.getTimestamp("task_created_at").getTime());
		return task;
	}
}
