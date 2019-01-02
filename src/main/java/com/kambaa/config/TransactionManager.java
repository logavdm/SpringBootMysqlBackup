package com.kambaa.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configurable
public class TransactionManager {

	@Bean
	public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
	    DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
	    dataSourceTransactionManager.setDataSource(dataSource);
	    return dataSourceTransactionManager;
	}
	
}
