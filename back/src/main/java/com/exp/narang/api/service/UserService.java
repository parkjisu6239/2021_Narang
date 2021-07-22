package com.exp.narang.api.service;

import com.exp.narang.api.request.UserRegisterPostReq;
import com.exp.narang.db.entity.User;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 인터페이스 정의.
 */
public interface UserService {
	User createUser(UserRegisterPostReq userRegisterInfo);
	User getUserByEmail(String email);
	boolean idExists(String email);
	void deleteById(Long id);
	User updateUser(UserRegisterPostReq updateInfo, String email);
	boolean userNameExists(String username);
}
