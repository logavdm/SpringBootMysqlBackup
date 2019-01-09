package com.kambaa.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kambaa.helper.mysqlbackup.MysqlExportServices;

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
		logger.info("mysql backup task running");
		this.mysqlExportServices.exportMysqlDatabase(taskWithObject);
	}

}
