package com.kambaa.resultextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.kambaa.entity.Task;
import com.kambaa.model.UserWithTask;

public class UserWithTaskResultExtractor implements ResultSetExtractor<List<UserWithTask>>  {

	@Override
	public List<UserWithTask> extractData(ResultSet row) throws SQLException, DataAccessException {
		
		Map<Long, UserWithTask> map = new HashMap<Long, UserWithTask>();
		UserWithTask userWithTask = null;
		while (row.next()) {
			long userID = row.getLong("users_id");
			userWithTask = map.get(userID);
			if (userWithTask == null) {
				userWithTask = new UserWithTask();
				userWithTask.setId(row.getLong("users_id"));
				userWithTask.setFullname(row.getString("users_fullname"));
				userWithTask.setDob(row.getString("users_dob"));
				userWithTask.setGender(row.getString("users_gender"));
				userWithTask.setMobile(row.getString("users_mobile"));
				userWithTask.setEmail(row.getString("users_email"));
				userWithTask.setCity(row.getString("users_city"));
				userWithTask.setCountry(row.getString("users_country"));
				userWithTask.setPincode(row.getString("users_pincode"));
				userWithTask.setState(row.getString("users_state"));
				userWithTask.setAddressLine1(row.getString("users_address1"));
				userWithTask.setAddressLine2(row.getString("users_address2"));
				userWithTask.setAddressLine3(row.getString("users_address3"));
				userWithTask.setPassword(row.getString("users_password"));
				userWithTask.setEnable(row.getBoolean("users_is_enable"));
				userWithTask.setLocked(row.getBoolean("users_is_locked"));
				userWithTask.setUpdatedAt(row.getTimestamp("users_updated_at")==null?null:row.getTimestamp("users_updated_at").getTime());
				userWithTask.setCreatedAt(row.getTimestamp("users_created_at")==null?null:row.getTimestamp("users_created_at").getTime());
				userWithTask.setListTasks(new ArrayList<Task>());
				map.put(userID, userWithTask);
			}
			String taskID = row.getString("task_id");
			if (taskID != null && taskID != "") {
				Task task=new Task();
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
				userWithTask.getListTasks().add(task);
			}
		}
		return new ArrayList<UserWithTask>(map.values());
	}

}
