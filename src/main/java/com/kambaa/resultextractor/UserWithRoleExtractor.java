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
				userWithRole.setCity(row.getString("users_city"));
				userWithRole.setCountry(row.getString("users_country"));
				userWithRole.setPincode(row.getString("users_pincode"));
				userWithRole.setState(row.getString("users_state"));
				userWithRole.setCreatedAt(row.getDate("users_created_at"));
				userWithRole.setDob(row.getString("users_dob"));
				userWithRole.setEmail(row.getString("users_email"));
				userWithRole.setFullname(row.getString("users_fullname"));
				userWithRole.setGender(row.getString("users_gender"));
				userWithRole.setEnable(row.getBoolean("users_is_enable"));
				userWithRole.setLocked(row.getBoolean("users_is_locked"));
				userWithRole.setMobile(row.getString("users_mobile"));
				userWithRole.setUpdatedAt(row.getDate("users_updated_at"));
				userWithRole.setPassword(row.getString("users_password"));
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
