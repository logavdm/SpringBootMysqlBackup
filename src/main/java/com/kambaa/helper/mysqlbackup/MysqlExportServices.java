package com.kambaa.helper.mysqlbackup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kambaa.model.TaskWithObject;
import com.kambaa.services.TaskActionLogServices;
import com.mysql.cj.jdbc.MysqlDataSource;

@Service
public class MysqlExportServices {
	
	@Value("${database.backup.maximum.table}")
	private Long maxTable;
	
	@Value("${database.backup.maximum.row.pertable}")
	private Long maxRows;
	
	@Autowired
	MysqlExportServiceHelper exportServiceHelper;
	
	@Autowired
	TaskActionLogServices taskActivityLogServices;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(MysqlExportServices.class);
	
	public void exportMysqlDatabase(TaskWithObject task) {

		File file = null;
		FileOutputStream fileWriter = null;
		List<Map<String, String>> listTables = null;
		MysqlDataSource dataSource = null;
		try {

			dataSource = exportServiceHelper.createDatasource(task.getConnectionString(), task.getDatabase(),task.getUsername(), task.getPassword());
			
			JdbcTemplate jdbcTemplate = exportServiceHelper.createConnection(dataSource);
			
			listTables = exportServiceHelper.getAllTables(jdbcTemplate, task.getDatabase());
			
			MaximumTableAndRowRestrictionValidate validateUserMaximumTable = new MaximumTableAndRowRestrictionValidate(listTables,maxTable,maxRows);
			validateUserMaximumTable.validate();
			
			file = exportServiceHelper.getBackupFile(task.getUserid(), task.getId(), task.getDatabase());
			fileWriter = new FileOutputStream(file);
			
			exportServiceHelper.writeBackupHeader(fileWriter);
			
			for (Map<String, String> table : listTables) {
				exportServiceHelper.writeCreateTableQuery(jdbcTemplate,fileWriter, table.get("TABLE"));
			}

			// JdbcTemplate
			// template=exportServiceHelper.connect(task.getConnectionString(),task.getDatabase(),task.getUsername(),task.getPassword());
			// List<String> tableList=exportServiceHelper.getAllTables(template,
			// task.getDatabase());

			// if(tableList!=null) {
			// logger.info("File backedup success");
			// for (String string : tableList) {
			// logger.info("Table Name :"+string);
			// }
			// }else {
			// logger.info("File backup failed");
			// }

		} catch (IllegalArgumentException e) {
			logger.error("Error occured when export the mysql database :" + e);
			databasebackUpFailed(task, e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error("Error occured when database backup :" + e);
			databasebackUpFailed(task, e.getLocalizedMessage());
		}
	}
	
	@Async
	public void databasebackUpFailed(TaskWithObject task,String errorMessage) {
		try {
			String sql="INSERT INTO task_activity_log(task_activity_log_task_id,task_activity_log_user_id,task_activity_log_activity,task_activity_log_status,task_activity_log_description,task_activity_log_updated_at,task_activity_log_created_at) VALUES(?,?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql,new Object[] {task.getId(),task.getUserid(),"DATABASE BACKUP","FAILED","Database Backup has been failed due to cannot establish the database connection please check user name and password or connection reachability"});
		}catch (Exception e) {
			logger.error("Error occured when Database connection failed activity log write "+e);
		}
	}

}
