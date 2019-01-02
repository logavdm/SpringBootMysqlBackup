package com.kambaa.resultextractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.kambaa.model.UserWithRole;

public class UserWithRoleExtractor implements ResultSetExtractor<List<UserWithRole>> {

	@Override
	public List<UserWithRole> extractData(ResultSet row) throws SQLException, DataAccessException {

		Map<Long, UserWithRole> map = new HashMap<Long, UserWithRole>();
		UserWithRole userWithRole = null;
		while (row.next()) {
			long userID = row.getLong("users_id");
			userWithRole = map.get(userID);
			if (userWithRole == null) {
				userWithRole = new UserWithRole();
				userWithRole.setId(row.getLong("users_id"));
				userWithRole.setFullname(row.getString("users_fullname"));
				userWithRole.setDob(row.getString("users_dob"));
				userWithRole.setGender(row.getString("users_gender"));
				userWithRole.setMobile(row.getString("users_mobile"));
				userWithRole.setEmail(row.getString("users_email"));
				userWithRole.setCity(row.getString("users_city"));
				userWithRole.setCountry(row.getString("users_country"));
				userWithRole.setPincode(row.getString("users_pincode"));
				userWithRole.setState(row.getString("users_state"));
				userWithRole.setAddressLine1(row.getString("users_address1"));
				userWithRole.setAddressLine2(row.getString("users_address2"));
				userWithRole.setAddressLine3(row.getString("users_address3"));
				userWithRole.setPassword(row.getString("users_password"));
				userWithRole.setEnable(row.getBoolean("users_is_enable"));
				userWithRole.setLocked(row.getBoolean("users_is_locked"));
				userWithRole.setUpdatedAt(row.getTimestamp("users_updated_at")==null?null:row.getTimestamp("users_updated_at").getTime());
				userWithRole.setCreatedAt(row.getTimestamp("users_created_at")==null?null:row.getTimestamp("users_created_at").getTime());
				userWithRole.setRole(new ArrayList<String>());
				map.put(userID, userWithRole);
			}
			String UserRole = row.getString("user_roles_role");
			if (UserRole != null && UserRole != "") {
				userWithRole.getRole().add(UserRole);
			}
		}
		return new ArrayList<UserWithRole>(map.values());
	}

}
