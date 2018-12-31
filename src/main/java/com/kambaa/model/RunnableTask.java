package com.kambaa.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableTask implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(RunnableTask.class);

	private String message;

	public RunnableTask(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		logger.info(new Date() + " Runnable Task with " + message + " on thread " + Thread.currentThread().getName());
	}

}
