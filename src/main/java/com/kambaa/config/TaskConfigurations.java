package com.kambaa.config;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.kambaa.model.TaskWithObject;
import com.kambaa.mysql.backup.module.MysqlBackUpTask;
import com.kambaa.mysql.backup.module.MysqlExportServices;
import com.kambaa.resultextractor.UserKeyWithTaskMapResultExtractor;
import com.kambaa.services.TaskActivityLogServices;
import com.kambaa.services.TaskServices;

@Configuration
@PropertySource("classpath:application.properties")
public class TaskConfigurations {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	TaskServices taskServices;
	
	@Autowired
	MysqlExportServices mysqlExportServices;
	
	@Autowired
	TaskActivityLogServices taskLogServices;

	@Value("${task.list.query}")
	String taskGetQuery;

	private static final Logger logger = LoggerFactory.getLogger(TaskConfigurations.class);

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}

	@Bean
	public Map<Long, Map<Long, TaskWithObject>> getAllTaskList() {
		Map<Long, Map<Long, TaskWithObject>> userMapWithTaskList = this.jdbcTemplate.query(taskGetQuery,new UserKeyWithTaskMapResultExtractor());
		if (userMapWithTaskList != null && userMapWithTaskList.size() > 0) {
			for (Long userid : userMapWithTaskList.keySet()) {
				Map<Long, TaskWithObject> taskList = userMapWithTaskList.get(userid);				
				if (taskList != null && taskList.size() > 0) {
					for (Long taskid : taskList.keySet()) {
						try {
							ScheduledFuture<?> taskItem = threadPoolTaskScheduler().schedule(new MysqlBackUpTask(mysqlExportServices,taskList.get(taskid)),new CronTrigger(taskList.get(taskid).getCronExpression()));
							taskList.get(taskid).setTask(taskItem);
							taskList.get(taskid).setStatus("RUNNING");
							taskServices.updateTaskStatusByID(taskid, "RUNNING");
						} catch (Exception e) {
							logger.error("Error occured when scedule the task");
						}
					}
				} else {
					logger.info("user task list get as empty or null");
				}
				logger.info("Task run success");
			}
		} else {
			logger.info("No Task list available");
		}
		return userMapWithTaskList;
	}

}
