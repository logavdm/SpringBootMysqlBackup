package com.kambaa.services;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.kambaa.entity.User;
import com.kambaa.model.UserModel;
import com.kambaa.model.UserWithRole;
import com.kambaa.resultextractor.UserWithRoleExtractor;
import com.kambaa.rowmapper.UserRowMapper;

@Service
public class UserService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Value("${password.hash.key}")
	String passwordSalt;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	public <S extends User> S save(S entity) {
		return null;
	}

	public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
		return null;
	}

	public User findById(Long id) {
		try {
			String sql = "SELECT * FROM users WHERE users_id=?";
			RowMapper<User> rowMapper = new UserRowMapper();
			return this.jdbcTemplate.query(sql, new Object[] { id }, rowMapper).get(0);
		} catch (Exception e) {
			logger.error("Error occured when get user by user id:" + e);
			return null;
		}
	}

	public boolean existsById(Long id) {
		String sql = "SELECT COUNT(users_id) from User WHERE users_id = ?";
		if (this.jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public Iterable<User> findAll() {
		String sql = "SELECT * FROM users";
		RowMapper<User> rowMapper = new UserRowMapper();
		return this.jdbcTemplate.query(sql, rowMapper);
	}

	public Iterable<User> findAllById(Iterable<Long> ids) {
		return null;
	}

	public long count() {
		String sql = "SELECT COUNT(users_id) from users";
		return this.jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public void deleteById(Long id) {

	}

	public void delete(User entity) {
		// TODO Auto-generated method stub

	}

	public void deleteAll(Iterable<? extends User> entities) {
		// TODO Auto-generated method stub

	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public int countByMobile(String mobile) {
		String sql = "SELECT COUNT(users_id) from User WHERE users_mobile = ?";
		return this.jdbcTemplate.queryForObject(sql, new Object[] { mobile }, Integer.class);
	}

	public User findByEmail(String email) {
		String sql = "SELECT * FROM users WHERE users_email = ?";
		RowMapper<User> rowMapper = new UserRowMapper();
		return (User) this.jdbcTemplate.query(sql, new Object[] { email }, rowMapper).get(0);
	}

	public User findByMobile(String mobile) {
		try {
			String sql = "SELECT * FROM users WHERE users_mobile = ?";
			RowMapper<User> rowMapper = new UserRowMapper();
			return this.jdbcTemplate.query(sql, new Object[] { mobile }, rowMapper).get(0);
		} catch (Exception e) {
			logger.error("Error occured when user find by mobile:" + e);
			return null;
		}
	}

	public UserWithRole findByMobileWithRole(String mobile) {
		try {
			String sql = "SELECT u.*,r.user_roles_role from users AS u JOIN user_roles AS r ON u.users_id=r.user_roles_user_id WHERE u.users_mobile=?";
			UserWithRoleExtractor resultSetExtractor = new UserWithRoleExtractor();
			return this.jdbcTemplate.query(sql, new Object[] { mobile }, resultSetExtractor).get(0);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

	public int countByEmail(String email) {
		String sql = "SELECT COUNT(User_id) from User WHERE User_email = ?";
		return this.jdbcTemplate.queryForObject(sql, new Object[] { email }, Integer.class);
	}

	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(User_id) from User WHERE User_email = ?";
		if (this.jdbcTemplate.queryForObject(sql, new Object[] { email }, Integer.class) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean existsMobile(String mobile) {
		String sql = "SELECT COUNT(users_id) from users WHERE users_mobile = ?";
		if (this.jdbcTemplate.queryForObject(sql, new Object[] { mobile }, Integer.class) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<UserWithRole> findAllWithRole() {
		String sql = "SELECT u.*, ur.user_roles_role FROM User as u JOIN user_roles as ur ON u.User_id = ur.user_roles_user_id";
		UserWithRoleExtractor resultSetExtractor = new UserWithRoleExtractor();
		return this.jdbcTemplate.query(sql, resultSetExtractor);
	}

	public void changePassword(long userID, String password) {
		String sql = "UPDATE User SET User_password=? WHERE User_id=?";
		jdbcTemplate.update(sql, password, userID);
	}

	public void enableAccount(String mobile, boolean flag) {
		String sql = "UPDATE User SET User_is_enable=? WHERE User_mobile=?";
		jdbcTemplate.update(sql, flag, mobile);
	}

	public void updateUserNameByUserID(long userID, String name, String aadhar) {
		String sql = "UPDATE User SET User_fullname=?,User_aadhar_number=? WHERE User_id=?";
		this.jdbcTemplate.update(sql, new Object[] { name, aadhar, userID });
	}

	
	public boolean validateMobileAndPassword(String mobile,String password,HttpSession session) {
		try {
			if(existsMobile(mobile)) {
				User user=findByMobile(mobile);
				if(user!=null && BCrypt.checkpw(password, user.getPassword()) && user.isEnable() && !user.isLocked()) {
					logger.info("user validation success");
					convertToUserModel(user, session);
					return true;
				}else{
					logger.info("User entered invalid password");
					return false;
				}
			}else {
				logger.info("Mobile number not found on database");
				return false;
			}
		}catch (Exception e) {
			logger.error("Error occured when validate the mobile number and password :"+e);
			return false;
		}
	}
	
	
	boolean convertToUserModel(User user, HttpSession session) {
		try {
			UserModel userModel = new UserModel();
			userModel.setEmail(user.getEmail());
			userModel.setMobile(user.getMobile());
			userModel.setFullname(user.getFullname());
			userModel.setEnabled(user.isEnable());
			userModel.setId(user.getId());
			session.setAttribute("USERDETAILS", userModel);
			return true;
		} catch (Exception e) {
			logger.error("Error occcured when convert to user model:"+e);
			return false;
		}
	}
	
}
