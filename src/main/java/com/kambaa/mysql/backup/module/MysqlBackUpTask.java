package com.kambaa.mysql.backup.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kambaa.model.TaskWithObject;

public class MysqlBackUpTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MysqlBackUpTask.class);
	
	private MysqlExportServices mysqlExportServices;
	private TaskWithObject taskWithObject;
	
	public MysqlBackUpTask(MysqlExportServices exportService,TaskWithObject task){
		this.mysqlExportServices=exportService;
		this.taskWithObject=task;
	}
	
	@Override
	public void run() {
		try {
			this.mysqlExportServices.exportMysqlDatabase(taskWithObject);
		
		}catch (Exception e) {
			logger.error("Error occured when task backup");
		}
	}
}
