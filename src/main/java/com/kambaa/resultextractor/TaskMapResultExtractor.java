package com.kambaa.resultextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.kambaa.model.TaskWithObject;

public class TaskMapResultExtractor implements ResultSetExtractor<Map<Long,TaskWithObject>> {

	@Override
	public Map<Long,TaskWithObject> extractData(ResultSet row) throws SQLException, DataAccessException {

		Map<Long, TaskWithObject> map = new HashMap<Long, TaskWithObject>();
		TaskWithObject taskWithObject = null;
		while (row.next()) {
			long taskID = row.getLong("task_id");
			taskWithObject = map.get(taskID);
			if (taskWithObject == null) {
				taskWithObject = new TaskWithObject();
				taskWithObject.setId(row.getLong("task_id"));
				taskWithObject.setTaskName(row.getString("task_name"));
				taskWithObject.setCronExpression(row.getString("task_cron"));
				taskWithObject.setEnabled(row.getBoolean("task_enabled"));
				taskWithObject.setStatus("task_status");
				taskWithObject.setUserid(row.getLong("task_users_id"));
				taskWithObject.setUpdatedAt(row.getTimestamp("task_updated_at")==null?null:row.getTimestamp("task_updated_at").getTime());
				taskWithObject.setCreatedAt(row.getTimestamp("task_created_at")==null?null:row.getTimestamp("task_created_at").getTime());
				map.put(taskID, taskWithObject);
			}
		}
		return map;
	}

}
