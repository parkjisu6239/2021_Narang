package com.exp.narang.api.service;

import com.exp.narang.api.request.UserRegisterPostReq;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.UserRepository;
import com.exp.narang.db.repository.UserRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRepositorySupport userRepositorySupport;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public User createUser(UserRegisterPostReq userRegisterInfo) {
		return userRepository.save(
				User.builder().
						email(userRegisterInfo.getEmail())
						.username(userRegisterInfo.getUsername())
						.password(passwordEncoder.encode(userRegisterInfo.getPassword())) // 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
						.build()
		);
	}

	@Override
	public User getUserByEmail(String email) {
		// 디비에 유저 정보 조회 (userId 를 통한 조회).
		Optional<User> userOpt = userRepositorySupport.findUserByEmail(email);
		return userOpt.orElse(null);
	}

	@Override
	public boolean idExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean userNameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional
	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	//@Transactional
	@Override
	public User updateUser(UserRegisterPostReq updateInfo, String email) {
		User user = userRepositorySupport.findUserByEmail(email).get();
		user.setUsername(updateInfo.getUsername());
		return userRepository.save(user);
	}
}
