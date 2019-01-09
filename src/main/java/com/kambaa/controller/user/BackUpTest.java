package com.kambaa.controller.user;

import java.io.File;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kambaa.helper.MysqlExportService;
import com.kambaa.model.Response;

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
			properties.setProperty(MysqlExportService.DB_NAME, "test");
			properties.setProperty(MysqlExportService.DB_USERNAME, "root");
			properties.setProperty(MysqlExportService.DB_PASSWORD, "");

			//set the outputs temp dir
			properties.setProperty(MysqlExportService.TEMP_DIR, new File("d:/backup").getPath());
			properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
			MysqlExportService mysqlExportService = new MysqlExportService(properties);
			mysqlExportService.export();
			//mysqlExportService.clearTempFiles(false);
			//File file = mysqlExportService.getGeneratedZipFile();
			
			response.setResponsecode("200");
			response.setMessage("backup done");
			response.setResponse(null);
			logger.info("backup completed");

		}catch (Exception e) {
			logger.error("Error occured when doing the mysql backup task :"+e);
			response.setResponsecode("500");
			response.setMessage("something went wrong please try again later");
			response.setResponse(null);
		}
		logger.info("-------------USER BACKUP TASK END HERE--------------");
		return response;
	}
	
}
