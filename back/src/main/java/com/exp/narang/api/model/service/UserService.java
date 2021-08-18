package com.exp.narang.api.model.service;

import com.exp.narang.api.model.request.UserInfoUpdateReq;
import com.exp.narang.api.model.request.UserRegisterPostReq;
import com.exp.narang.api.model.db.entity.User;

import java.util.List;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface UserService {
	List<User> findUserList();
	User createUser(UserRegisterPostReq userRegisterInfo);
	User getUserByEmail(String email);
	boolean idExists(String email);
	void deleteById(Long id);
	void deleteProfile(User user);
	User updateUser(UserInfoUpdateReq updateInfo, User user);
	boolean userNameExists(String username);
}
