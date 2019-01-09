package com.kambaa.helper.mysqlbackup;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.kambaa.model.TaskWithObject;
import com.kambaa.services.TaskActionLogServices;

@Service
public class MysqlExportServices {
	
	@Autowired
	MysqlExportServiceHelper exportServiceHelper;
	
	@Autowired
	TaskActionLogServices taskActivityLogServices;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(MysqlExportServices.class);
	
	public void exportMysqlDatabase(TaskWithObject task) {
		try {
			logger.info("Mysql export task start success");
			Connection connection=exportServiceHelper.connect(task.getConnectionString(),task.getDatabase(),task.getUsername(),task.getPassword());
			if(connection==null) {
				logger.info("connection establish failed");
				databaseConnectionTestFailed(task);
			}else {
				
			}
		}catch (Exception e) {
			logger.error("Error occured when export the mysql database :"+e);
		}
	}
	
	
	@Async
	public void databaseConnectionTestFailed(TaskWithObject task) {
		try {
			String sql="INSERT INTO task_activity_log(task_activity_log_task_id,task_activity_log_user_id,task_activity_log_activity,task_activity_log_status,task_activity_log_description,task_activity_log_updated_at,task_activity_log_created_at) VALUES(?,?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql,new Object[] {task.getId(),task.getUserid(),"DATABASE BACKUP","FAILED","Database Backup has been failed due to cannot establish the database connection please check user name and password or connection reachability"});
		}catch (Exception e) {
			logger.error("Error occured when Database connection failed activity log write "+e);
		}
	}

}
