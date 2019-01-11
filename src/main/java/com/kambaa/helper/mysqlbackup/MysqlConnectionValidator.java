package com.kambaa.helper.mysqlbackup;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

public class MysqlConnectionValidator implements MysqlBackUpValidator{

	MysqlDataSource dataSource;
	
	
	public MysqlConnectionValidator(MysqlDataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	@Override
	public void validate() {
		@SuppressWarnings("unused")
		JdbcTemplate template;
		try {
			template=new JdbcTemplate(this.dataSource);
			template=null;
		}catch (Exception e) {
			template=null;
			throw new IllegalArgumentException("Database Connection Test failed");
		}
	}

}
