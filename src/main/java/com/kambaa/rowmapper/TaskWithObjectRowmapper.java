package com.kambaa.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kambaa.model.TaskWithObject;

public class TaskWithObjectRowmapper implements RowMapper<TaskWithObject> {

	@Override
	public TaskWithObject mapRow(ResultSet row, int rowNumber) throws SQLException {
		TaskWithObject task=new TaskWithObject();
		task.setId(row.getLong("task_id"));
		task.setTaskName(row.getString("task_name"));
		task.setCronExpression(row.getString("task_cron"));
		task.setEnabled(row.getBoolean("task_enabled"));
		task.setStatus(row.getString("task_status"));
		task.setUserid(row.getLong("task_users_id"));
		task.setUpdatedAt(row.getTimestamp("task_updated_at")==null?null:row.getTimestamp("task_updated_at").getTime());
		task.setCreatedAt(row.getTimestamp("task_created_at")==null?null:row.getTimestamp("task_created_at").getTime());
		return task;
	}

}
