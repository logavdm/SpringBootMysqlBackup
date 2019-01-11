package com.kambaa.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

public class MysqlExportService {

	private Statement stmt;
    private String database;
    private String generatedSql = "";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final String LOG_PREFIX = "java-mysql-exporter";
    private String dirName = "java-mysql-exporter-temp";
    private String sqlFileName = "";
    private String zipFileName = "";
    private Properties properties;
    private File generatedZipFile;

    public static final String EMAIL_HOST = "EMAIL_HOST";
    public static final String EMAIL_PORT = "EMAIL_PORT";
    public static final String EMAIL_USERNAME = "EMAIL_USERNAME";
    public static final String EMAIL_PASSWORD = "EMAIL_PASSWORD";
    public static final String EMAIL_SUBJECT = "EMAIL_SUBJECT";
    public static final String EMAIL_MESSAGE = "EMAIL_MESSAGE";
    public static final String EMAIL_FROM = "EMAIL_FROM";
    public static final String EMAIL_TO = "EMAIL_TO";
    
    public static final String JDBC_CONNECTION_STRING = "JDBC_CONNECTION_STRING";
    public static final String JDBC_DRIVER_NAME = "JDBC_DRIVER_NAME";
    public static final String SQL_FILE_NAME = "SQL_FILE_NAME";
    
    public static final String DB_NAME = "DB_NAME";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String PRESERVE_GENERATED_ZIP = "PRESERVE_GENERATED_ZIP";
    public static final String TEMP_DIR = "TEMP_DIR";
    public static final String ADD_IF_NOT_EXISTS = "ADD_IF_NOT_EXISTS";

    public static final String DROP_TABLES = "DROP_TABLES";
    public static final String DELETE_EXISTING_DATA = "DELETE_EXISTING_DATA";

    


    public MysqlExportService(Properties properties) {
        this.properties = properties;
    }

    private boolean isValidateProperties() {
        return properties != null &&
                properties.containsKey(DB_USERNAME) &&
                properties.containsKey(DB_PASSWORD) &&
                (properties.containsKey(DB_NAME) || properties.containsKey(JDBC_CONNECTION_STRING));
    }


    private boolean isSqlFileNamePropertySet(){
        return properties != null &&
                properties.containsKey(SQL_FILE_NAME);
    }


    private String getTableInsertStatement(String table) throws SQLException {

        StringBuilder sql = new StringBuilder();
        ResultSet rs;
        boolean addIfNotExists = Boolean.parseBoolean(properties.containsKey(ADD_IF_NOT_EXISTS) ? properties.getProperty(ADD_IF_NOT_EXISTS, "true") : "true");

        if(table != null && !table.isEmpty()){
            rs = stmt.executeQuery("SHOW CREATE TABLE " + "`" + table + "`;");
            while ( rs.next() ) {
                String qtbl = rs.getString(1);
                String query = rs.getString(2);
                sql.append("\n\n--");
                sql.append("\n").append(MysqlBaseService.SQL_START_PATTERN).append("  table dump : ").append(qtbl);
                sql.append("\n--\n\n");

                if(addIfNotExists) {
                    query = query.trim().replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS");
                }
                sql.append(query).append(";\n\n");
            }

            sql.append("\n\n--");
            sql.append("\n").append(MysqlBaseService.SQL_END_PATTERN).append("  table dump : ").append(table);
            sql.append("\n--\n\n");
        }

        return sql.toString();
    }



    private String getDataInsertStatement(String table) throws SQLException {

        StringBuilder sql = new StringBuilder();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + "`" + table + "`;");

        //move to the last row to get max rows returned
        rs.last();
        int rowCount = rs.getRow();

        //there are no records just return empty string
        if(rowCount <= 0) {
            return sql.toString();
        }

        sql.append("\n--").append("\n-- Inserts of ").append(table).append("\n--\n\n");
        //temporarily disable foreign key constraint
        sql.append("\n/*!40000 ALTER TABLE `").append(table).append("` DISABLE KEYS */;\n");
        sql.append("\n--\n")
                .append(MysqlBaseService.SQL_START_PATTERN).append(" table insert : ").append(table)
                .append("\n--\n");

        sql.append("INSERT INTO `").append(table).append("`(");

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        //generate the column names that are present
        //in the returned result set
        //at this point the insert is INSERT INTO (`col1`, `col2`, ...)
        for(int i = 0; i < columnCount; i++) {
            sql.append("`")
                    .append(metaData.getColumnName( i + 1))
                    .append("`, ");
        }

        //remove the last whitespace and comma
        sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1).append(") VALUES \n");

        //now we're going to build the values for data insertion
        rs.beforeFirst();
        while(rs.next()) {
            sql.append("(");
            for(int i = 0; i < columnCount; i++) {

                int columnType = metaData.getColumnType(i + 1);
                int columnIndex = i + 1;

                //this is the part where the values are processed based on their type
                if(Objects.isNull(rs.getObject(columnIndex))) {
                    sql.append("").append(rs.getObject(columnIndex)).append(", ");
                }
                else if( columnType == Types.INTEGER || columnType == Types.TINYINT || columnType == Types.BIT) {
                    sql.append(rs.getInt(columnIndex)).append(", ");
                }
                else {

                    String val = rs.getString(columnIndex);
                   //escape the single quotes that might be in the value
                    val = val.replace("'", "\\'");

                    sql.append("'").append(val).append("', ");
                }
            }

            //now that we're done with a row
            //let's remove the last whitespace and comma
            sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1);

            //if this is the last row, just append a closing
            //parenthesis otherwise append a closing parenthesis and a comma
            //for the next set of values
            if(rs.isLast()) {
                sql.append(")");
            } else {
                sql.append("),\n");
            }
        }

        //now that we are done processing the entire row
        //let's add the terminator
        sql.append(";");

        sql.append("\n--\n")
                .append(MysqlBaseService.SQL_END_PATTERN).append(" table insert : ").append(table)
                .append("\n--\n");

        //enable FK constraint
        sql.append("\n/*!40000 ALTER TABLE `").append(table).append("` ENABLE KEYS */;\n");

        return sql.toString();
    }

    private String exportToSql() throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("--");
        sql.append("\n-- Generated By Kambaa MYSQL Back Scheduler");
        sql.append("\n-- https://github.com/logavdm");
        sql.append("\n-- Date: ").append(new SimpleDateFormat("d-M-Y H:m:s").format(new Date()));
        sql.append("\n--");

        //get the tables that are in the database
        List<String> tables = MysqlBaseService.getAllTables(database, stmt);

        //for every table, get the table creation and data
        // insert statement
        for (String s: tables) {
            try {
                sql.append(getTableInsertStatement(s.trim()));
                sql.append(getDataInsertStatement(s.trim()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        this.generatedSql = sql.toString();
        return sql.toString();
    }


    public void export() throws IOException, SQLException, ClassNotFoundException {

        //check if properties is set or not
        if(!isValidateProperties()) {
            logger.error("Invalid config properties: The config properties is missing important parameters: DB_NAME, DB_USERNAME and DB_PASSWORD");
            return;
        }

        //connect to the database
        database = properties.getProperty(DB_NAME);
        String jdbcURL = properties.getProperty(JDBC_CONNECTION_STRING);
        String driverName = properties.getProperty(JDBC_DRIVER_NAME, "");

        Connection connection;

        	if (jdbcURL.contains("?")){
                database = jdbcURL.substring(jdbcURL.lastIndexOf("/") + 1, jdbcURL.indexOf("?"));
            } else {
                database = jdbcURL.substring(jdbcURL.lastIndexOf("/") + 1);
            }
            logger.debug("database name extracted from connection string: " + database);
            connection = MysqlBaseService.connectWithURL(properties.getProperty(DB_USERNAME), properties.getProperty(DB_PASSWORD),jdbcURL, driverName);
        

        stmt = connection.createStatement();

        //generate the final SQL
        String sql = exportToSql();

        //create a temp dir to store the exported file for processing
        dirName = properties.getProperty(MysqlExportService.TEMP_DIR, dirName);
        File file = new File(dirName);
        if(!file.exists()) {
            boolean res = file.mkdir();
            if(!res) {
//                logger.error(LOG_PREFIX + ": Unable to create temp dir: " + file.getAbsolutePath());
                throw new IOException(LOG_PREFIX + ": Unable to create temp dir: " + file.getAbsolutePath());
            }
        }

        //write the sql file out
        File sqlFolder = new File(dirName + "/sql");
        if(!sqlFolder.exists()) {
            boolean res = sqlFolder.mkdir();
            if(!res) {
                throw new IOException(LOG_PREFIX + ": Unable to create temp dir: " + file.getAbsolutePath());
            }
        }

        sqlFileName = getSqlFilename();
        FileOutputStream outputStream = new FileOutputStream( sqlFolder + "/" + sqlFileName);
        outputStream.write(sql.getBytes());
        outputStream.close();

        //zip the file
        zipFileName = dirName + "/" + sqlFileName.replace(".sql", ".zip");
        generatedZipFile = new File(zipFileName);
        ZipUtil.pack(sqlFolder, generatedZipFile);

        //clear the generated temp files
        clearTempFiles(Boolean.parseBoolean(properties.getProperty(PRESERVE_GENERATED_ZIP, Boolean.FALSE.toString())));

    }


    public void clearTempFiles(boolean preserveZipFile) {

        //delete the temp sql file
        File sqlFile = new File(dirName + "/sql/" + sqlFileName);
        if(sqlFile.exists()) {
            boolean res = sqlFile.delete();
            logger.debug(LOG_PREFIX + ": " + sqlFile.getAbsolutePath() + " deleted successfully? " + (res ? " TRUE " : " FALSE "));
        } else {
            logger.debug(LOG_PREFIX + ": " + sqlFile.getAbsolutePath() + " DOES NOT EXIST while clearing Temp Files");
        }

        File sqlFolder = new File(dirName + "/sql");
        if(sqlFolder.exists()) {
            boolean res = sqlFolder.delete();
            logger.debug(LOG_PREFIX + ": " + sqlFolder.getAbsolutePath() + " deleted successfully? " + (res ? " TRUE " : " FALSE "));
        } else {
            logger.debug(LOG_PREFIX + ": " + sqlFolder.getAbsolutePath() + " DOES NOT EXIST while clearing Temp Files");
        }


        //only execute this section if the
        //file is not to be preserved

        if(!preserveZipFile) {

            //delete the zipFile
            File zipFile = new File(zipFileName);
            if (zipFile.exists()) {
                boolean res = zipFile.delete();
                logger.debug(LOG_PREFIX + ": " + zipFile.getAbsolutePath() + " deleted successfully? " + (res ? " TRUE " : " FALSE "));
            } else {
                logger.debug(LOG_PREFIX + ": " + zipFile.getAbsolutePath() + " DOES NOT EXIST while clearing Temp Files");
            }

            //delete the temp folder
            File folder = new File(dirName);
            if (folder.exists()) {
                boolean res = folder.delete();
                logger.debug(LOG_PREFIX + ": " + folder.getAbsolutePath() + " deleted successfully? " + (res ? " TRUE " : " FALSE "));
            } else {
                logger.debug(LOG_PREFIX + ": " + folder.getAbsolutePath() + " DOES NOT EXIST while clearing Temp Files");
            }
        }

        logger.debug(LOG_PREFIX + ": generated temp files cleared successfully");
    }


    public String getSqlFilename(){
        return isSqlFileNamePropertySet() ? properties.getProperty(SQL_FILE_NAME) + ".sql" :
                new SimpleDateFormat("d_M_Y_H_mm_ss").format(new Date()) + "_" + database + "_database_dump.sql";
    }

    public String getSqlFileName() {
        return sqlFileName;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }

    public File getGeneratedZipFile() {
        if(generatedZipFile != null && generatedZipFile.exists()) {
            return generatedZipFile;
        }
        return null;
    }
	
}
