package com.kambaa.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kambaa.entity.TaskActionLog;

public class TaskActionLogRowMapper implements RowMapper<TaskActionLog> {

	@Override
	public TaskActionLog mapRow(ResultSet row, int rowNum) throws SQLException {
		TaskActionLog taskAction=new TaskActionLog();
		taskAction.setId(row.getLong("task_action_log_id"));
		taskAction.setTaskID(row.getLong("task_action_log_task_id"));
		taskAction.setUserID(row.getLong("task_action_log_users_id"));
		taskAction.setAction(row.getString("task_action_log_action_type"));
		taskAction.setDescription(row.getString("task_action_log_action_description"));		
		taskAction.setUpdatedAt(row.getTimestamp("task_action_log_created_at")==null?null:row.getTimestamp("task_action_log_created_at").getTime());
		taskAction.setCreatedAt(row.getTimestamp("task_action_log_updated_at")==null?null:row.getTimestamp("task_action_log_updated_at").getTime());
		return taskAction;
	}
}
