package com.kambaa.model;

import java.util.concurrent.ScheduledFuture;

public class TaskWithObject {
	
	private long id;
	private String taskName;
	private String cronExpression;
	private String status;
	private boolean enabled;
	private Long updatedAt;
	private Long createdAt;
	private Long userid;
	private ScheduledFuture<?> task;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Long getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public ScheduledFuture<?> getTask() {
		return task;
	}
	public void setTask(ScheduledFuture<?> task) {
		this.task = task;
	}
	
	

}
