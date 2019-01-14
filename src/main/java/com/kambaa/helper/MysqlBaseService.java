//package com.kambaa.helper;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class MysqlBaseService {
//	
//	 private static Logger logger = LoggerFactory.getLogger(MysqlBaseService.class);
//
//	    static final String SQL_START_PATTERN = "-- START MYSQL BACKUP FOR ";
//	    static final String SQL_END_PATTERN = "-- END MYSQL BACKUP FOR ";
//
//
//	    static Connection connect(String hostname,String username, String password, String database, String driverName) throws ClassNotFoundException, SQLException {
//	        String url = "jdbc:mysql://"+hostname+":3306/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
//	        String driver = (Objects.isNull(driverName) || driverName.isEmpty()) ? "com.mysql.cj.jdbc.Driver" : driverName;
//	        return doConnect(driver, url, username, password);
//	    }
//
//
//	    static Connection connectWithURL(String username, String password, String jdbcURL, String driverName) throws ClassNotFoundException, SQLException {
//	        String driver = (Objects.isNull(driverName) || driverName.isEmpty()) ? "com.mysql.cj.jdbc.Driver" : driverName;
//	        return doConnect(driver, jdbcURL, username, password);
//	    }
//
//
//	    private static Connection doConnect(String driver, String url, String username, String password) throws SQLException, ClassNotFoundException {
//	        Class.forName(driver);
//	        Connection connection = DriverManager.getConnection(url, username, password);
//	        logger.debug("DB Connected Successfully");
//	        return  connection;
//	    }
//
//
//
//	    static List<String> getAllTables(String database, Statement stmt) throws SQLException {
//	        List<String> table = new ArrayList<>();
//	        ResultSet rs;
//	        rs = stmt.executeQuery("SHOW TABLE STATUS FROM `" + database + "`;");
//	        while ( rs.next() ) {
//	            table.add(rs.getString("Name"));
//	        }
//	        return table;
//	    }
//
//
//	    static String getEmptyTableSQL(String database, String table) {
//	        String safeDeleteSQL = "SELECT IF( \n" +
//	                 "(SELECT COUNT(1) as table_exists FROM information_schema.tables \n" +
//	                    "WHERE table_schema='" + database + "' AND table_name='" + table + "') > 1, \n" +
//	                 "'DELETE FROM " + table + "', \n" +
//	                 "'SELECT 1') INTO @DeleteSQL; \n" +
//	                "PREPARE stmt FROM @DeleteSQL; \n" +
//	                "EXECUTE stmt; DEALLOCATE PREPARE stmt; \n";
//
//	        return  "\n" + MysqlBaseService.SQL_START_PATTERN + "\n" +safeDeleteSQL + "\n" +"\n" + MysqlBaseService.SQL_END_PATTERN + "\n";
//	    }
//}
