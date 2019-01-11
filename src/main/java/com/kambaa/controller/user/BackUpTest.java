//package com.kambaa.controller.user;
//
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.kambaa.helper.mysqlbackup.MaximumTableAndRowRestrictionValidate;
//import com.kambaa.helper.mysqlbackup.MysqlExportServiceHelper;
//import com.kambaa.helper.mysqlbackup.MysqlExportServices;
//import com.kambaa.model.Response;
//import com.mysql.cj.jdbc.MysqlDataSource;
//
//@RestController
//public class BackUpTest {
//	
//	@Autowired
//	Response response;
//	
//	@Autowired
//	MysqlExportServices exportServices;
//	
//	@Autowired
//	MysqlExportServiceHelper exportHelper;
//	
//	@Autowired
//	JdbcTemplate jdbcTemplate;
//	
//	private static final Logger logger = LoggerFactory.getLogger(BackUpTest.class);
//
//	@RequestMapping(value="/user/backup",method=RequestMethod.POST)
//	public Response StartBackup() {
//		try {
//			
//			MysqlDataSource dataSource=exportHelper.createDatasource("localhost","test","root","");
//			JdbcTemplate jdbcTemplate=exportHelper.createConnection(dataSource);
//			List<Map<String,String>> listTables=exportHelper.getAllTables(jdbcTemplate,"test");
//			
////			MaximumTableAndRowRestrictionValidate validateUserMaximumTable=new MaximumTableAndRowRestrictionValidate(listTables,maxt);
////			validateUserMaximumTable.validate();
//
//			
//			
//		}catch (Exception e) {
//			logger.error("Error occured when doing the mysql backup task :"+e);
//			response.setResponsecode("500");
//			response.setMessage("something went wrong please try again later");
//			response.setResponse(null);
//		}
//		logger.info("-------------USER BACKUP TASK END HERE--------------");
//		return response;
//	}
//	
//}
