package com.kambaa.resultextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.kambaa.model.TaskWithObject;

public class UserKeyWithTaskMapResultExtractor implements ResultSetExtractor<Map<Long,Map<Long,TaskWithObject>>> {

	@Override
	public Map<Long, Map<Long, TaskWithObject>> extractData(ResultSet row) throws SQLException, DataAccessException {
		Map<Long,Map<Long,TaskWithObject>> map = new HashMap<Long,Map<Long,TaskWithObject>>();
		Long userid=null;
		while(row.next()) {
			userid=row.getLong("users_id");
			if(map.containsKey(userid)) {
				Map<Long,TaskWithObject> listTask=map.get(userid);
				TaskWithObject task=new TaskWithObject();
				task.setId(row.getLong("task_id"));
				task.setTaskName(row.getString("task_name"));
				task.setCronExpression(row.getString("task_cron"));
				task.setEnabled(row.getBoolean("task_enabled"));
				task.setStatus(row.getString("task_status"));
				task.setUserid(row.getLong("task_users_id"));
				task.setUpdatedAt(row.getTimestamp("task_updated_at")==null?null:row.getTimestamp("task_updated_at").getTime());
				task.setCreatedAt(row.getTimestamp("task_created_at")==null?null:row.getTimestamp("task_created_at").getTime());
				listTask.put(task.getId(),task);
			}else {
				Map<Long,TaskWithObject> listTask=new HashMap<Long,TaskWithObject>();
				TaskWithObject task=new TaskWithObject();
				task.setId(row.getLong("task_id"));
				task.setTaskName(row.getString("task_name"));
				task.setCronExpression(row.getString("task_cron"));
				task.setEnabled(row.getBoolean("task_enabled"));
				task.setStatus(row.getString("task_status"));
				task.setUserid(row.getLong("task_users_id"));
				task.setConnectionString(row.getString("task_connection_string"));
				task.setDatabase(row.getString("task_database"));
				task.setUsername(row.getString("task_database_username"));
				task.setPassword(row.getString("task_database_password"));
				task.setUpdatedAt(row.getTimestamp("task_updated_at")==null?null:row.getTimestamp("task_updated_at").getTime());
				task.setCreatedAt(row.getTimestamp("task_created_at")==null?null:row.getTimestamp("task_created_at").getTime());
				listTask.put(task.getId(),task);
				map.put(userid, listTask);
			}
		}
		return map;
	}

}
