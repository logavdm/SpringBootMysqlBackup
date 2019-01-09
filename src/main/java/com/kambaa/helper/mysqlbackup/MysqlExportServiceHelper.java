package com.kambaa.helper.mysqlbackup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MysqlExportServiceHelper {

	@Value("${database.mysql.driver.class}")
	private String mysqlDriverName;

	private static final Logger logger = LoggerFactory.getLogger(MysqlExportServiceHelper.class);

	public Connection connect(String connectionString,String database,String username, String password) {
		String url = connectionString+"/" + database+ "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
		return doConnect(mysqlDriverName, url, username, password);
	}

	private Connection doConnect(String driver, String url, String username, String password) {
		Connection connection = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, username, password);
			logger.debug("DB Connected Successfully");
			return connection;
		} catch (Exception e) {
			logger.error("Error occured when create the connection :" + e);
			try {
				connection.close();
			} catch (Exception e1) {
				logger.error("error when close the connection");
			}
			return null;
		}
	}

	public String getSqlFilename(String databaseName) {
		return new SimpleDateFormat("d_M_Y_H_mm_ss").format(new Date()) + "_" + databaseName + "_database_dump.sql";
	}

}
