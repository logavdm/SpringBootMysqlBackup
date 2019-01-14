package com.kambaa.mysql.backup.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kambaa.model.TaskWithObject;
import com.kambaa.services.TaskActionLogServices;
import com.mysql.cj.jdbc.MysqlDataSource;

@Service
public class MysqlExportServices {
	
	@Value("${database.backup.maximum.table}")
	private Long maxTable;
	
	@Value("${database.backup.maximum.row.pertable}")
	private Long maxRows;
	
	@Value("${database.mysql.driver.class}")
	private String mysqlDriverName;
	
	@Value("${database.backup.maximum.table}")
	Long maximumTablesConfig;
	
	@Value("${database.backup.maximum.row.pertable}")
	Long maximumRowPerTable;
	
	@Value("${database.backup.location}")
	String backupLocation;
	
	@Autowired
	TaskActionLogServices taskActivityLogServices;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	static final String SQL_START_PATTERN = "-- START MYSQL BACKUP FOR ";
    static final String SQL_END_PATTERN = "-- END MYSQL BACKUP FOR ";
		
	private static final Logger logger = LoggerFactory.getLogger(MysqlExportServices.class);
	
	public void exportMysqlDatabase(TaskWithObject task) {

		File file = null;
		FileOutputStream fileWriter = null;
		List<Map<String, String>> listTables = null;
		MysqlDataSource dataSource = null;
		
		try {

			dataSource = createDatasource(task.getConnectionString(), task.getDatabase(),task.getUsername(), task.getPassword());
			JdbcTemplate jdbcTemplate = createConnection(dataSource);
			listTables = getAllTables(jdbcTemplate, task.getDatabase());
			
			MaximumTableAndRowRestrictionValidate validateUserMaximumTable = new MaximumTableAndRowRestrictionValidate(listTables,maxTable,maxRows);
			validateUserMaximumTable.validate();
			
			file = getBackupFile(task.getUserid(), task.getId(), task.getDatabase());
			fileWriter = new FileOutputStream(file);
			writeBackupHeader(fileWriter);
			
			for (Map<String, String> table : listTables) {
				writeCreateTableQuery(jdbcTemplate,fileWriter, table.get("TABLE"));
				writeDataInsertStatement(jdbcTemplate, fileWriter, table.get("TABLE"));
			}
			
		} catch (IllegalArgumentException e) {
			logger.error("Error occured when export the mysql database :" + e);
			databasebackUpFailed(task, e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error("Error occured when database backup :" + e);
			databasebackUpFailed(task, e.getLocalizedMessage());
		}finally {
			try {
				fileWriter.close();
				file=null;
			}catch(Exception e) {
				logger.error("Error when close the file");
			}
		}
	}
	
	
	public MysqlDataSource createDatasource(String host, String databaseName, String username, String password) {
		try {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setDatabaseName(databaseName);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setServerName("localhost");
			return dataSource;
		}catch (Exception e) {
			logger.error("Error occured when create the datasource");
			return null;
		}
	}
	
	
	private JdbcTemplate createConnection(MysqlDataSource mysqlDataSource) {
		try{
			JdbcTemplate jdbcTemplate;
			jdbcTemplate=new JdbcTemplate(mysqlDataSource);
			String sql="SELECT CHAR(97)";
			jdbcTemplate.queryForObject(sql,char.class);
			return jdbcTemplate;
		}catch (Exception e) {
			System.err.println("Error :"+e);
			throw new IllegalArgumentException("Database connection failed");
		}
	}
	
	private List<Map<String,String>> getAllTables(JdbcTemplate jdbcTemplate,String database){
		try {
			String sql="SELECT table_name, table_rows FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?;";
			List<Map<String,String>> lisTableResult=jdbcTemplate.query(sql,new Object[] {database},new ResultSetExtractor<List<Map<String,String>>>() {		
				@Override
				public List<Map<String, String>> extractData(ResultSet row) throws SQLException, DataAccessException {
					List<Map<String,String>> listRow=new LinkedList<>();
					while(row.next()) {
						Map<String,String> mapString=new LinkedHashMap<>();
						mapString.put("TABLE",row.getString("table_name"));
						mapString.put("ROW",row.getString("table_rows"));
						listRow.add(mapString);
					}
					return listRow;
				}
			});
			return lisTableResult;
		}catch (Exception e) {
			logger.error("Error occured when getting the all Table list :"+e);
			return null;
		}
	}
	
	private File getBackupFile(Long userid,Long taskid,String databaseName) {
		try {
	         File file = new File(getBackupFolder(userid,taskid)+"/"+getSqlFilename(databaseName));
	         return file;
		}catch (Exception e) {
			logger.error("Error occured when create the backup file :"+e);
			throw new IllegalArgumentException("Backup File creation failed");
		}
	}
	
	private String getBackupFolder(Long userid,Long taskid) {
		try {
			File file=new File(backupLocation+""+userid+"/"+taskid);
			if(file.exists()&& file.isDirectory()) {
				return file.getAbsolutePath();
			}else {
				if(file.mkdirs())
					return file.getAbsolutePath();
				else {
					throw new IllegalArgumentException("Backup file creation failed");
				}
			}
		}catch (Exception e) {
			throw new IllegalArgumentException("Backup file creation failed");
		}
	}
	

	private String getSqlFilename(String databaseName) {
		return databaseName +"_dump_"+new SimpleDateFormat("Y_M_d_H_mm_ss").format(new Date())+".sql";
	}

		
	static List<String> getAllTables(String database, Statement stmt) throws SQLException {
        List<String> table = new ArrayList<>();
        ResultSet rs;
        rs = stmt.executeQuery("SHOW TABLE STATUS FROM `" + database + "`;");
        while ( rs.next() ) {
            table.add(rs.getString("Name"));
        }
        return table;
    }
	
	private  boolean writeBackupHeader(FileOutputStream fileWriter) {
		try {
			StringBuilder  sql = new StringBuilder();
			sql.append("--");
	        sql.append("\n-- Generated By Kambaa MYSQL Backup Scheduler");
	        sql.append("\n-- https://github.com/logavdm/mysqlbackup");
	        sql.append("\n-- Date: ").append(new SimpleDateFormat("d-M-Y H:m:s").format(new Date()));
	        sql.append("\n--\n");
	        fileWriter.write(sql.toString().getBytes());
			return true;
		}catch (Exception e) {
			logger.error("Error occured when write the backup file header :"+e);
			return false;
		}
	}
	
	
	private boolean writeCreateTableQuery(JdbcTemplate jdbcTemplate, FileOutputStream fileWriter, String tableName) {
		try {
			String sql = "SHOW CREATE table " + tableName + ";";
			jdbcTemplate.query(sql,new ResultSetExtractor<String>() {
				@Override
				public String extractData(ResultSet row) throws SQLException, DataAccessException {
					while (row.next()) {
						try {
							fileWriter.write(row.getString("Create Table").getBytes());
						} catch (IOException e) {
							logger.error("Error when write the table insert query :" + e);
						}
					}
					return null;
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("Error occured when write table create statement :" + e);
			return false;
		}
	}
	
	
	private boolean writeDataInsertStatement(JdbcTemplate jdbcTemplate, FileOutputStream fileWriter, String tableName) {
		try {
			String getTableData = "SELECT * FROM " + tableName + ";";
			StringBuilder tableHeader = new StringBuilder("\n-- DATA'S FOR THE TABLE " + tableName + "--\n\n");
			fileWriter.write(tableHeader.toString().getBytes());
			jdbcTemplate.query(getTableData, new ResultSetExtractor<Object>() {
				@Override
				public Object extractData(ResultSet row) throws SQLException, DataAccessException {
					try {
						ResultSetMetaData metaData = row.getMetaData();
						int columnCount = metaData.getColumnCount();
						StringBuilder insertDatabaRowHeader = new StringBuilder("INSERT INTO " + tableName + "(");
						for (int i = 0; i < columnCount; i++) {
							if (i == columnCount - 1)
								insertDatabaRowHeader.append(metaData.getColumnName(i + 1));
							else
								insertDatabaRowHeader.append(metaData.getColumnName(i + 1) + ",");
						}
						insertDatabaRowHeader.append(") VALUES \n");
						fileWriter.write(insertDatabaRowHeader.toString().getBytes());
						while (row.next()) {
							StringBuilder rowItem = new StringBuilder("(");
							for (int i = 0; i < columnCount; i++) {
								int columnType = metaData.getColumnType(i + 1);
								int columnIndex = i + 1;
								if (Objects.isNull(row.getObject(columnIndex))) {
									rowItem.append("").append(row.getObject(columnIndex)).append(", ");
								} else if (columnType == Types.INTEGER || columnType == Types.TINYINT
										|| columnType == Types.BIT) {
									rowItem.append(row.getInt(columnIndex)).append(", ");
								} else {
									String val = row.getString(columnIndex);
									val = val.replace("'", "\\'");
									rowItem.append("'").append(val).append("', ");
								}
							}
							rowItem.deleteCharAt(rowItem.length() - 1);
							rowItem.deleteCharAt(rowItem.length() - 1);
							if (row.isLast()) {
								rowItem.append(")");
							} else {
								rowItem.append("),\n");
							}
							fileWriter.write(rowItem.toString().getBytes());
						}
						return null;
					} catch (Exception e) {
						logger.error("Error occured when insert the table rows :" + e);
						return null;
					}
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("Error occured when insert the table data :" + e);
			return false;
		}
	}
	
	
	@Async
	public void databasebackUpFailed(TaskWithObject task,String errorMessage) {
		try {
			String sql="INSERT INTO task_activity_log(task_activity_log_task_id,task_activity_log_user_id,task_activity_log_activity,task_activity_log_status,task_activity_log_description,task_activity_log_updated_at,task_activity_log_created_at) VALUES(?,?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql,new Object[] {task.getId(),task.getUserid(),"DATABASE BACKUP","FAILED",errorMessage});
		}catch (Exception e) {
			logger.error("Error occured when Database connection failed activity log write "+e);
		}
	}
	

	@Async
	public void databasebackUpSuccess(TaskWithObject task,String message) {
		try {
			String sql="INSERT INTO task_activity_log(task_activity_log_task_id,task_activity_log_user_id,task_activity_log_activity,task_activity_log_status,task_activity_log_description,task_activity_log_updated_at,task_activity_log_created_at) VALUES(?,?,?,?,?,now(),now())";
			this.jdbcTemplate.update(sql,new Object[] {task.getId(),task.getUserid(),"DATABASE BACKUP","SUCCESS",message});
		}catch (Exception e) {
			logger.error("Error occured when Database connection failed activity log write "+e);
		}
	}

}
