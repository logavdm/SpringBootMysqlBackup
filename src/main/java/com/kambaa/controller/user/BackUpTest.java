package com.kambaa.controller.user;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.model.Response;
import com.smattme.MysqlExportService;

@RestController
public class BackUpTest {
	
	@Autowired
	Response response;
	
	private static final Logger logger = LoggerFactory.getLogger(BackUpTest.class);

	@RequestMapping(value="/user/backup",method=RequestMethod.POST)
	public Response StartBackup() {
		try {
			logger.info("-------------USER BACKUP TASK START HERE--------------");
			
			//required properties for exporting of db
			Properties properties = new Properties();
			properties.setProperty(MysqlExportService.DB_NAME, "backup");
			properties.setProperty(MysqlExportService.DB_USERNAME, "root");
			properties.setProperty(MysqlExportService.DB_PASSWORD, "");
			
			//properties relating to email config
			properties.setProperty(MysqlExportService.EMAIL_HOST, "smtp.mailtrap.io");
			properties.setProperty(MysqlExportService.EMAIL_PORT, "25");
			properties.setProperty(MysqlExportService.EMAIL_USERNAME, "mailtrap-username");
			properties.setProperty(MysqlExportService.EMAIL_PASSWORD, "mailtrap-password");
			properties.setProperty(MysqlExportService.EMAIL_FROM, "test@smattme.com");
			properties.setProperty(MysqlExportService.EMAIL_TO, "backup@smattme.com");
			
			
			//set the outputs temp dir
			properties.setProperty(MysqlExportService.TEMP_DIR, new File("external").getPath());
			MysqlExportService mysqlExportService = new MysqlExportService(properties);
			mysqlExportService.export();
			
			response.setResponsecode("200");
			response.setMessage("backup done");
			response.setResponse(null);
			logger.info("backup completed");

		}catch (Exception e) {
			logger.error("Error occured when doing the mysql backup task");
			response.setResponsecode("500");
			response.setMessage("something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-------------USER BACKUP TASK END HERE--------------");
		return response;
	}
	
}
