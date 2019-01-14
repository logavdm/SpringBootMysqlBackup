package com.kambaa.mysql.backup.module;

import java.util.List;
import java.util.Map;


public class MaximumTableAndRowRestrictionValidate implements MysqlBackUpValidator {
	

	private List<Map<String,String>> listTables;
	private Long maxTable;
	private Long maxRows;
	
	public MaximumTableAndRowRestrictionValidate(List<Map<String,String>> listTables,Long maxTable,Long maxRows) {
		this.listTables=listTables;
		this.maxTable=maxTable;
		this.maxRows=maxRows;
	}
		
	@Override
	public void validate() {
		if(listTables.size()>=maxTable)
			throw new IllegalArgumentException("Number of Table size exceed the configured maximum table size Total tables :"+listTables.size()+" configured :"+maxTable);
		
		for (Map<String, String> map : listTables) {
			if(!(Long.valueOf(map.get("ROW"))<=maxRows))
				throw new IllegalArgumentException("The table "+map.get("TABLE")+" exceed the configured row limit row size is :"+map.get("ROW") +" configured limit is :"+maxRows);
		}
	}

}
