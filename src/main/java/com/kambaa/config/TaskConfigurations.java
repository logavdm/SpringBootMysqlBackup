package com.kambaa.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.kambaa.entity.Task;
import com.kambaa.model.RunnableTask;

@Configuration
@PropertySource("classpath:application.properties")
public class TaskConfigurations {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskConfigurations.class);
	
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		return threadPoolTaskScheduler;
	}
	
	@Bean
	public Map<Long, Task> getAllTaskList() {
		Map<Long,Task> mapTasks;
		String sql="SELECT * FROM config";
		mapTasks=this.jdbcTemplate.query(sql,new ResultSetExtractor<Map<Long,Task>>() {

			@Override
			public Map<Long,Task> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long,Task> taskMap=new LinkedHashMap<Long,Task>();
				while(rs.next()) {
					Task tempTask=new Task();
					tempTask.setId(rs.getLong("id"));
					tempTask.setTaskName(rs.getString("name"));
					tempTask.setCronExpression(rs.getString("expression"));
					tempTask.setEnabled(rs.getBoolean("enabled"));
					taskMap.put(tempTask.getId(),tempTask);
				}
				return taskMap;
			}
		});
		
		
		if(mapTasks!=null && mapTasks.size()>0) {
			for (Long taskid : mapTasks.keySet()) {
				logger.info("Task name :"+mapTasks.get(taskid).getTaskName());
				if(mapTasks.get(taskid).isEnabled()) {
					ScheduledFuture<?> taskItem=threadPoolTaskScheduler().schedule(new RunnableTask(mapTasks.get(taskid).getTaskName()),new CronTrigger(mapTasks.get(taskid).getCronExpression()));
					mapTasks.get(taskid).setTask(taskItem);
				}else {
					logger.info("Task not enabled so no need to run the task");
				}
				logger.info("Task run success");
			}
		}else {
			logger.info("No Task list available");
		}
		return mapTasks;
	}
	
	
}
