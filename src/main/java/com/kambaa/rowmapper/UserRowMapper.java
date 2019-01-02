package com.kambaa.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kambaa.entity.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet row, int rowNum) throws SQLException {		
		User user=new User();
		user.setId(row.getLong("users_id"));
		user.setFullname(row.getString("users_fullname"));
		user.setDob(row.getString("users_dob"));
		user.setGender(row.getString("users_gender"));
		user.setMobile(row.getString("users_mobile"));
		user.setEmail(row.getString("users_email"));
		user.setCity(row.getString("users_city"));
		user.setCountry(row.getString("users_country"));
		user.setPincode(row.getString("users_pincode"));
		user.setState(row.getString("users_state"));
		user.setAddressLine1(row.getString("users_address1"));
		user.setAddressLine2(row.getString("users_address2"));
		user.setAddressLine3(row.getString("users_address3"));
		user.setPassword(row.getString("users_password"));
		user.setEnable(row.getBoolean("users_is_enable"));
		user.setLocked(row.getBoolean("users_is_locked"));
		user.setUpdatedAt(row.getTimestamp("users_updated_at")==null?null:row.getTimestamp("users_updated_at").getTime());
		user.setCreatedAt(row.getTimestamp("users_created_at")==null?null:row.getTimestamp("users_created_at").getTime());
		return user;
	}
}
